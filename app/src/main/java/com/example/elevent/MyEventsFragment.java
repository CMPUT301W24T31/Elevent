package com.example.elevent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * This fragment displays the events that an organizer has created
 */
public class MyEventsFragment extends Fragment {

    CreateEventFragment createEventFragment;
    private ArrayList<Event> myEvents;
    private ListView myEventList;
    private EventArrayAdapter myEventsArrayAdapter;

    /**
     * Called upon initial creation of the fragment
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myEvents = new ArrayList<>(); // Initialize ArrayList

    }
    @Override
    public void onResume() {
        super.onResume();
        // Check if the activity is an instance of MainActivity and update the app bar title
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateAppBarTitle("My Events");
        }
    }

    /**
     * Called to have the fragment instantiate its user interface view
     * Initialize the event array adapter
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_myevents, container, false);
        myEventList = view.findViewById(R.id.my_events_list);
        myEventsArrayAdapter = new EventArrayAdapter(getContext(), myEvents);
        myEventList.setAdapter(myEventsArrayAdapter);
        return view;
    }

//andrew/s implementation still here for reference needed
    /*@Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton createEventButton = view.findViewById(R.id.create_my_event_button);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CreateEventFragment().show(requireActivity().getSupportFragmentManager(), "Create an event");
            }
        });
    }*/

    /**
     * Called after the view has been created
     * Initialize UI to allow organizer to finialize creation
     * Handle fragment switching
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton createEventButton = view.findViewById(R.id.create_my_event_button);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //did fragment switching using fragment helper, creates instance of main to tie with the fragment to enable switching
                //(same implementation as the random floating button in all events :))
                if (getActivity() instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    FragmentManagerHelper helper = mainActivity.getFragmentManagerHelper();
                    helper.replaceFragment(new CreateEventFragment());
                    mainActivity.updateAppBarTitle("Creating Event...");
                }
                //return null;
            }
        });

        myEventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected event
                Event selectedEvent = myEvents.get(position);

                // Pass the selected event to CreatedEventFragment should still parse the data on here
                CreatedEventFragment createdEventFragment = new CreatedEventFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("selected_event", selectedEvent);
                createdEventFragment.setArguments(bundle);

                //switch fragments
                if (getActivity() instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.updateAppBarTitle(selectedEvent.getEventName());
                    FragmentManagerHelper helper = mainActivity.getFragmentManagerHelper();
                    helper.replaceFragment(createdEventFragment);
                }
            }
        });
        fetchEvents();
    }

    /**
     * Add an event to the array adapter and notify
     * @param event Created event
     */
    public void addEvent(Event event){
        myEventsArrayAdapter.add(event);
        myEventsArrayAdapter.notifyDataSetChanged();
    }
    private void fetchEvents() {
        EventDBConnector eventDBConnector = new EventDBConnector();
        FirebaseFirestore db = eventDBConnector.getDb(); //

        db.collection("events").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                myEvents.clear(); // Clear existing events before adding new ones
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Event event = document.toObject(Event.class);
                    myEvents.add(event); // Add the fetched event to the list
                }
                myEventsArrayAdapter.notifyDataSetChanged(); // Notify the adapter to refresh the ListView
            } else {
                Toast.makeText(getContext(), "Failed to fetch events.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
