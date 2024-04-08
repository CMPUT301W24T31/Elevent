package com.example.elevent;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.N;

import java.util.ArrayList;
/*
    This file contains the implementation for the NotificationFragmentAttendee that displays the UI for the attendee's view
    of notifications
    Outstanding issues: needs to be implemented
 */
/**
 * This class displays the UI for an attendee's view of notifications
 */
public class NotificationFragmentAttendee extends Fragment {

    private Event event;
    private ListView notifList;
    private NotificationArrayAdapter notificationArrayAdapter;
    private ArrayList<String> notificationsList = new ArrayList<>();
    /**
     * Required empty constructor
     */
    public NotificationFragmentAttendee() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            event = (Event) getArguments().getSerializable("event");
        }
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
     * @return View of the user interface
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notif_centre_attendee, container, false);
        notifList = view.findViewById(R.id.list_of_notifs);
        notificationArrayAdapter = new NotificationArrayAdapter(requireContext(), notificationsList);
        notifList.setAdapter(notificationArrayAdapter);
        return view;
    }

    /**
     * Called immediately after onCreateView has returned, but before any saved state has been restored in to the view
     * Implements notification display
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Implementation for displaying notifications
        ArrayList<String> notifications = (ArrayList<String>) event.getNotifications();
        updateListView(notifications);
    }
    private void updateListView(ArrayList<String> notifications){
        notificationArrayAdapter = new NotificationArrayAdapter(requireContext(), notifications);
        notifList.setAdapter(notificationArrayAdapter);
    }
}
