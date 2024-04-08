package com.example.elevent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
/*
    This file contains the implementation of the organizer's view of their event's notifications
 */
/**
 * This fragment contains UI for the organizer to handle and push notifications
 */
public class NotificationCentreFragment extends Fragment {
    private Event event;
    private ListView listOfNotifications;
    private NotificationArrayAdapter notificationAdapter;
    private ArrayList<String> notificationsList = new ArrayList<>();

    /**
     * Required empty public constructor
     */
    public NotificationCentreFragment(){}

    /**
     * Called to do initial creation of a fragment
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            event = getArguments().getParcelable("event");
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
     * @return Return the View for the fragment's UI, or null
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notificationcentre, container, false);
        listOfNotifications = view.findViewById(R.id.list_of_notifs);

        // Initialize adapter and set it to the ListView
        notificationAdapter = new NotificationArrayAdapter(requireContext(), notificationsList);
        listOfNotifications.setAdapter(notificationAdapter);

        return view;
    }

    /**
     * Called immediately after onCreateView has returned, but before any saved state has been restored in to the view
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button addNotification = view.findViewById(R.id.add_notif_button);
        if (addNotification != null) {
            addNotification.setOnClickListener(v -> {
                showAddNotificationDialog();
            });
        }
        updateListView((ArrayList<String>) event.getNotifications());
        Collections.reverse(notificationsList);
    }

    /**
     * Shows the notification in a dialog fragment
     */
    private void showAddNotificationDialog() {
        if (getActivity() != null) {
            AddNotificationDialogFragment dialogFragment = new AddNotificationDialogFragment();
            // Pass the current fragment as the listener
            Bundle args = new Bundle();
            args.putParcelable("event", event);
            dialogFragment.setArguments(args);
            dialogFragment.show(getChildFragmentManager(), "AddNotificationDialogFragment");
        }
    }
    /**
     * Updates the display of notifications of the event
     * @param notifications List of notifications to be displayed
     */
    private void updateListView(ArrayList<String> notifications) {
        // If notificationAdapter is already initialized, update the data set
        if (notificationAdapter != null) {
            notificationAdapter.addAll(notifications);
            notificationAdapter.notifyDataSetChanged();
        } else {
            // If notificationAdapter is not initialized, create a new one
            notificationAdapter = new NotificationArrayAdapter(requireContext(), notifications);
            listOfNotifications.setAdapter(notificationAdapter);
        }
    }



}
