package com.example.elevent;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllEventsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ArrayList<Event> AllEvents;
    //defaultEvent.add("Sample Event"); // Add your default event details here

    // Define the interface
    public interface OnEventClickListener {
        void onEventClicked(Event event);
    }

    // Define a listener member variable
    private OnEventClickListener eventClickListener;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AllEventsFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AllEventsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AllEventsFragment newInstance(String param1, String param2) {
        AllEventsFragment fragment = new AllEventsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_allevents, container, false);
    }
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

    @Override
    public void onDetach() {
        super.onDetach();
        // Detach the listener to avoid memory leaks
        eventClickListener = null;
    }
    public void onResume() {
        super.onResume();
        // Update the app bar title when navigating back to the AllEventsFragment
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateAppBarTitle(getString(R.string.all_events_title));
        }
    }
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
                "yyyy-MM-dd","HH:mm","This is a default event description.","Default location",null);
        events.add(defaultEvent);
        EventArrayAdapter eventAdapter = new EventArrayAdapter(getActivity(), events);
        listView.setAdapter(eventAdapter);
    */


        listView.setOnItemClickListener((parent, view1, position, id) -> {
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

        EventArrayAdapter eventAdapter = new EventArrayAdapter(getActivity(), events);
        listView.setAdapter(eventAdapter);
        fetchEvents();
    }
    public void fetchEvents() {
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

    public void updateListView(ArrayList<Event> events) { // Ensure parameter is ArrayList<Event>
        EventArrayAdapter eventAdapter = new EventArrayAdapter(requireActivity(), events); // Use requireActivity() to ensure non-null Context
        ListView listView = getView().findViewById(R.id.list_view);
        listView.setAdapter(eventAdapter);
    }
    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_allevents, container, false);
    }*/
}