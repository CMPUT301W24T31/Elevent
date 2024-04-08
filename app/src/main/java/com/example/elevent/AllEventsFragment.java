package com.example.elevent;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/*
    This file is responsible for providing the UI to display the list o all events that have been created in the app.
 */
/**
 * This fragment displays all events posted to the app
 */
public class AllEventsFragment extends Fragment {

    private ArrayList<Event> AllEvents;
    private List<String> signedUpEvents;
    private ListView listView;

    /**
     * Required empty constructor
     */
    public AllEventsFragment() {
        // Required empty public constructor
    }


    /**
     * Called to have the fragment instantiate its user interface view
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return View for the fragment's UI, or null
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_allevents, container, false);
        listView = view.findViewById(R.id.list_view);
        return view;
    }

    /**
     * Called when the fragment is visible to the user and actively running
     * Updates the app bar
     */
    public void onResume() {
        super.onResume();
        // Update the app bar title when navigating back to the AllEventsFragment
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateAppBarTitle(getString(R.string.all_events_title));
        }
    }

    /**
     * Called immediately after has returned, but before any saved state has been restored in to the view.
     * Initializes the event filter and the event array adapter for displaying the events
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Spinner filterStatus = view.findViewById(R.id.event_filter_spinner);
        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.event_filter_spinner_array,
                android.R.layout.simple_spinner_item
        );
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterStatus.setAdapter(filterAdapter);

        ListView listView = view.findViewById(R.id.list_view);
        ArrayList<Event> events = new ArrayList<>();
        EventArrayAdapter eventArrayAdapter = new EventArrayAdapter(requireActivity(), events);
        listView.setAdapter(eventArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (getActivity() instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    FragmentManagerHelper helper = mainActivity.getFragmentManagerHelper();

                    // Assuming you will modify EventViewAttendee to accept an Event object as an argument.
                    Event clickedEvent = (Event) parent.getItemAtPosition(position);
                    if (clickedEvent != null) {
                        // Proceed with further actions
                    } else {
                        Log.e("AllEventsFragment", "Clicked event is null");
                    }

                    EventViewAttendee eventViewAttendeeFragment = new EventViewAttendee();
                    Bundle args = new Bundle();
                    args.putParcelable("event", clickedEvent); // Ensure Event implements Serializable
                    eventViewAttendeeFragment.setArguments(args);

                    helper.replaceFragment(eventViewAttendeeFragment); // Navigate to EventViewAttendee with event details
                }
            }
        });
        filterStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (Objects.equals(selection, "signed-up")) {
                    fetchSignedUpEventsList();
                    displaySignedUpEvents();
                } else if (Objects.equals(selection, "all")){
                    fetchEvents();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //ListView listView = view.findViewById(R.id.list_view);
        //ArrayList<Event> events = new ArrayList<>();

    /*
        Event defaultEvent = new Event("default",null,null,3,
                "yyyy-MM-dd","HH:mm","This is a default event description.","Default location",null, null);
        events.add(defaultEvent);
        EventArrayAdapter eventAdapter = new EventArrayAdapter(getActivity(), events);
        listView.setAdapter(eventAdapter);
    */

        //EventArrayAdapter eventAdapter = new EventArrayAdapter(getActivity(), events);
        //listView.setAdapter(eventAdapter);

        /*listView.setOnItemClickListener((parent, view1, position, id) -> {
            if (getActivity() instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) getActivity();
                FragmentManagerHelper helper = mainActivity.getFragmentManagerHelper();

                // Assuming you will modify EventViewAttendee to accept an Event object as an argument.
                Event clickedEvent = (Event) parent.getItemAtPosition(position);
                EventViewAttendee eventViewAttendeeFragment = new EventViewAttendee();
                Bundle args = new Bundle();
                args.putSerializable("event", clickedEvent); // Ensure Event implements Serializable
                eventViewAttendeeFragment.setArguments(args);

                helper.replaceFragment(eventViewAttendeeFragment); // Navigate to EventViewAttendee with event details
            }
        });
        fetchEvents();*/
    }

    /**
     * Get the event from the database
     */
    private void fetchEvents() {
        EventDBConnector connector = new EventDBConnector(); // Assuming this is correctly set up
        FirebaseFirestore db = connector.getDb();

        db.collection("events").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Event> eventsList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Event event = document.toObject(Event.class);
                    eventsList.add(event);
                }
                updateListView(new ArrayList<>(eventsList)); // Convert to ArrayList before updating the view
            } else {
                Log.d("AllEventsFragment", "Error getting documents: ", task.getException());
            }
        });
    }

    /**
     * Fetch signed up events from db
     */
    private void fetchSignedUpEventsList(){
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString("userID", null);
        UserDBConnector userDBConnector = new UserDBConnector();
        FirebaseFirestore userDB = userDBConnector.getDb();
        if(userID != null) {
            userDB.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            signedUpEvents = user.getSignedUpEvents();
                        }
                    }
                }
            });
        }
    }

    /**
     * Convert signed up events to event objects and call updateListView to display
     */
    private void displaySignedUpEvents(){
        EventDBConnector connector = new EventDBConnector(); // Assuming this is correctly set up
        FirebaseFirestore db = connector.getDb();

        db.collection("events").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Event> eventsList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Event event = document.toObject(Event.class);
                    if (signedUpEvents.contains(event.getEventID())) {
                        eventsList.add(event);
                    }
                }
                updateListView(new ArrayList<>(eventsList)); // Convert to ArrayList before updating the view
            } else {
                Log.d("AllEventsFragment", "Error getting documents: ", task.getException());
            }
        });
    }

    /**
     * Update the display of the events
     * @param events List of events
     */
    private void updateListView(ArrayList<Event> events) { // Ensure parameter is ArrayList<Event>
        EventArrayAdapter eventAdapter = new EventArrayAdapter(requireContext(), events); // Use requireActivity() to ensure non-null Context
        listView.setAdapter(eventAdapter);
    }
}