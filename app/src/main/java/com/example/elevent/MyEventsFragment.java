package com.example.elevent;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.checkerframework.checker.units.qual.A;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Displays all events that the organizer has created
 * Allows organizer to create events
 */
public class MyEventsFragment extends Fragment{

    private ListView myEventsList;
    private EventArrayAdapter myEventsArrayAdapter;
    private ArrayList<Event> myEvents;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_myevents, container, false);
        myEventsList = view.findViewById(R.id.my_events_list);
        myEvents = new ArrayList<Event>();
        myEventsArrayAdapter = new EventArrayAdapter(getContext(), myEvents);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton createEventButton = view.findViewById(R.id.create_my_event_button);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CreateEventFragment().show(requireActivity().getSupportFragmentManager(), "Create an event");
            }
        });
    }

    public void addEvent(Event event){
        myEventsArrayAdapter.add(event);
        myEventsArrayAdapter.notifyDataSetChanged();
    }
}