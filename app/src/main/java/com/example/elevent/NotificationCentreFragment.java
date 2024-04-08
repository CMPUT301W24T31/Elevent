package com.example.elevent;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

/**
 * This fragment contains UI for the organizer to handle and push notifications
 */
public class NotificationCentreFragment extends Fragment {
    private Event event;
    private ListView listOfNotifications;
    private NotificationArrayAdapter notificationAdapter;
    private ArrayList<String> notificationsList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            event = (Event) getArguments().getSerializable("event");
        }
    }

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

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button addNotification = view.findViewById(R.id.add_notif_button);
        if (addNotification != null) {
            addNotification.setOnClickListener(v -> {
                showAddNotificationDialog();
            });
        }
        updateListView((ArrayList<String>) event.getNotifications());
    }

    private void showAddNotificationDialog() {
        if (getActivity() != null) {
            AddNotificationDialogFragment dialogFragment = new AddNotificationDialogFragment();
            // Pass the current fragment as the listener
            Bundle args = new Bundle();
            args.putSerializable("event", event);
            dialogFragment.setArguments(args);
            dialogFragment.show(getChildFragmentManager(), "AddNotificationDialogFragment");
        }
    }
    private void updateListView(ArrayList<String> notifications){
        notificationAdapter = new NotificationArrayAdapter(requireContext(), notifications);
        listOfNotifications.setAdapter(notificationAdapter);
    }
}
