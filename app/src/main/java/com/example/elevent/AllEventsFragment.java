package com.example.elevent;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    /*
        Event defaultEvent = new Event("default",null,null,3,
                "yyyy-MM-dd","HH:mm","This is a default event description.","Default location",null, null);
        events.add(defaultEvent);
        EventArrayAdapter eventAdapter = new EventArrayAdapter(getActivity(), events);
        listView.setAdapter(eventAdapter);
    */


        listView.setOnItemClickListener((parent, view1, position, id) -> {
            if (getActivity() instanceof MainActivity) {

                // Assuming you will modify EventViewAttendee to accept an Event object as an argument.
                Event clickedEvent = (Event) parent.getItemAtPosition(position);
                EventViewAttendee eventViewAttendeeFragment = new EventViewAttendee();
                Bundle args = new Bundle();
                args.putSerializable("event", clickedEvent); // Ensure Event implements Serializable
                eventViewAttendeeFragment.setArguments(args);

                Navigation.findNavController(view).navigate(R.id.action_allEventsFragment_to_eventViewAttendee2);
            }
        });

        EventArrayAdapter eventAdapter = new EventArrayAdapter(getActivity(), events);
        listView.setAdapter(eventAdapter);
        fetchEvents();
    }

    /**
     * Get the event from the database
     */
    public void fetchEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Event> eventsList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    try {
                        Event event = new Event();
                        event.setEventName(document.getString("eventName"));
                        event.setDate(document.getString("date"));
                        event.setTime(document.getString("time"));
                        event.setDescription(document.getString("description"));
                        event.setLocation(document.getString("location"));
                        Long attendeesCount = document.getLong("attendeesCount");
                        if (attendeesCount != null) {
                            event.setAttendeesCount(attendeesCount.intValue());
                        }

                        // Manually handle checkinQR
                        try {
                            Blob checkinQRBlob = document.getBlob("checkinQR");
                            if (checkinQRBlob != null) {
                                event.setCheckinQR(checkinQRBlob.toBytes());
                            }
                        } catch (Exception e) {
                            Log.e("EventFetch", "Error deserializing checkinQR for event: " + event.getEventName(), e);
                        }

                        eventsList.add(event);
                    } catch (Exception e) {
                        Log.e("EventFetch", "Error deserializing document to Event: " + document.getId(), e);
                    }
                }
                // Update UI with the fetched list of events
                updateListView(new ArrayList<>(eventsList));
            } else {
                Log.w("EventFetch", "Error getting documents: ", task.getException());
            }
        });
    }



    /**
     * Update the display of the events
     * @param events List of events
     */
    public void updateListView(ArrayList<Event> events) { // Ensure parameter is ArrayList<Event>
        EventArrayAdapter eventAdapter = new EventArrayAdapter(requireActivity(), events); // Use requireActivity() to ensure non-null Context
        ListView listView = getView().findViewById(R.id.list_view);
        listView.setAdapter(eventAdapter);
    }
}