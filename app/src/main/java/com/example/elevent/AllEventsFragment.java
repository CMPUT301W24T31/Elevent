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
import android.widget.Toast;

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
    Outstanding issues: n/a
 */
/**
 * This fragment displays all events posted to the app
 */
public class AllEventsFragment extends Fragment {

    ArrayList<Event> AllEvents;
    //defaultEvent.add("Sample Event"); // Add your default event details here

    /**
     * Listener for when user clicks on an event on their screen
     */
    public interface OnEventClickListener {
        void onEventClicked(Event event);
    }

    // Define a listener member variable
    private OnEventClickListener eventClickListener;
    List<String> signedUpEvents;

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
        return inflater.inflate(R.layout.fragment_allevents, container, false);
    }

    /**
     * Called when a fragment is first attached to its host activity
     * @param context Host activity of the fragment
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Attach your listener interface
        if (context instanceof OnEventClickListener) {
            eventClickListener = (OnEventClickListener) context;
        } else {
            // If you want to enforce the implementation of the interface, you can throw an exception
            // However, make sure your MainActivity implements OnEventClickListener interface
            // Otherwise, just log a warning
            Log.w("AllEventsFragment", "Parent context does not implement OnEventClickListener");
        }
    }

    /**
     * Called when the fragment is no longer attached to its activity.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        // Detach the listener to avoid memory leaks
        eventClickListener = null;
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
                    EventViewAttendee eventViewAttendeeFragment = new EventViewAttendee();
                    Bundle args = new Bundle();
                    args.putSerializable("event", clickedEvent); // Ensure Event implements Serializable
                    eventViewAttendeeFragment.setArguments(args);

                    helper.replaceFragment(eventViewAttendeeFragment); // Navigate to EventViewAttendee with event details
                }
            }
        });
        try{
            filterStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selection = (String) parent.getItemAtPosition(position);
                    if (Objects.equals(selection, "signed-up")){
                        fetchSignedUpEventsList();
                        displaySignedUpEvents();
                    } else if (Objects.equals(selection, "all")) {
                        fetchEvents();
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (Exception e) {
            showErrorFragment();
        }
    }

    private void showErrorFragment() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.allevents_error, new ErrorFragment())
                .commit();
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
        } else {
            showErrorFragment();
        }
    }

    private void displaySignedUpEvents(){
        if (signedUpEvents == null) {
            Toast.makeText(requireContext(), "no signed-up events available", Toast.LENGTH_LONG).show();
            return;
        }
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
        ListView listView = getView().findViewById(R.id.list_view);
        listView.setAdapter(eventAdapter);
    }
}