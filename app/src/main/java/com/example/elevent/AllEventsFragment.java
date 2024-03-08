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

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 * Displays all events created on the app
 */
public class AllEventsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ArrayList<Event> AllEvents;
    //defaultEvent.add("Sample Event"); // Add your default event details here

    /**
     * Interface for implementing the listener for clicking on an event
     */
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

    /**
     * Inflates the view of the fragment
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return View that has been inflated
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_allevents, container, false);
    }

    /**
     * Attaches the listener to a host activity
     * @param context The hose activity to be attached to
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
     * Detach the listener from the host activity
     */
    @Override
    public void onDetach() {
        super.onDetach();
        // Detach the listener to avoid memory leaks
        eventClickListener = null;
    }

    /**
     * Called after the view has been created
     * Initialize the events filter
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
        // Create a default event - adjust constructor parameters as per your Event class definition
        Event defaultEvent = new Event("default",null);
        events.add(defaultEvent);
        EventArrayAdapter eventAdapter = new EventArrayAdapter(getActivity(), events);
        listView.setAdapter(eventAdapter);


        // Make sure the ID matches your ListView's ID in the XML
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            // Handle the list item click event here
            // Example action: Show a Toast message
            //Toast.makeText(getActivity(), "Clicked on item: " + position, Toast.LENGTH_SHORT).show();
            if (getActivity() instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) getActivity();
                FragmentManagerHelper helper = mainActivity.getFragmentManagerHelper();
                helper.replaceFragment(new EventViewAttendee()); // Replace with any fragment
            }

            Event clickedEvent = events.get(position);
            if (eventClickListener != null) {
                eventClickListener.onEventClicked(clickedEvent);
            }
            // If you want to switch fragments or perform other actions, you can do that here
        });

        //EventArrayAdapter eventAdapter = new EventArrayAdapter(getActivity(), events);
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