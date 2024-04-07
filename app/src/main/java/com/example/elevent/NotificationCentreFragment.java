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
public class NotificationCentreFragment extends Fragment implements AddNotificationDialogFragment.AddNotificationDialogListener {

    /**
     * Interface for dialog listener that handles notification creation
     */
    interface NotificationCentreDialogListener {
        //void onCreateEvent(Event event);

        void onPositiveClick(Event event);
    }

    private NotificationCentreDialogListener listener;
    private Event event;
    private ListView listOfNotifications;
    private ArrayAdapter<String> notificationAdapter;
    private ArrayList<String> notificationsList = new ArrayList<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof NotificationCentreDialogListener) {
            listener = (NotificationCentreDialogListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement NotificationCentreDialogListener");
        }
    }

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
        notificationAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, notificationsList);
        listOfNotifications.setAdapter(notificationAdapter);

        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try{
        Button addNotification = view.findViewById(R.id.add_notif_button);
        if (addNotification != null) {
            addNotification.setOnClickListener(v -> {
                showAddNotificationDialog();
            });
        }
        } catch (Exception e) {
            showErrorFragment();
        }
    }

    private void showErrorFragment() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_main_framelayout, new ErrorFragment())
                .commit();
    }

    private void showAddNotificationDialog() {
        if (getActivity() != null) {
            AddNotificationDialogFragment dialogFragment = new AddNotificationDialogFragment();
            // Pass the current fragment as the listener
            dialogFragment.setListener(this);
            Bundle args = new Bundle();
            args.putSerializable("event", event);
            dialogFragment.setArguments(args);
            dialogFragment.show(getChildFragmentManager(), "AddNotificationDialogFragment");
        }
    }

    @Override
    public void onNotificationAdded(String notification) {
        // Add the new notification to the list
        notificationsList.add(0, notification);
        notificationAdapter.notifyDataSetChanged(); // Notify adapter of data change
    }
}
