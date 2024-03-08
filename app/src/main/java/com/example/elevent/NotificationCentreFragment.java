package com.example.elevent;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class NotificationCentreFragment extends Fragment implements AddNotificationDialogFragment.AddNotificationDialogListener {

    interface NotificationCentreDialogListener {
        //void onCreateEvent(Event event);

        void onPositiveClick(Event event);
    }

    private NotificationCentreFragment.NotificationCentreDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof NotificationCentreFragment.NotificationCentreDialogListener) {
            listener = (NotificationCentreFragment.NotificationCentreDialogListener) context;
        } else {
            throw new RuntimeException(context + " must implement NotificationCentreDialogListener");
        }
    }


    private TextView notificationListTextView;
    private ListView listOfNotifications;
    private ArrayAdapter<String> notificationAdapter;
    private ArrayList<String> notificationsList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notificationcentre, container, false);

        notificationListTextView = view.findViewById(R.id.notif_centre_textview);
        listOfNotifications = view.findViewById(R.id.list_of_notifs);

        // Initialize adapter and set it to the ListView
        notificationAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, notificationsList);
        listOfNotifications.setAdapter(notificationAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button addNotification = view.findViewById(R.id.add_notif_button);
        if (addNotification != null) {
            addNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddNotificationDialogFragment dialogFragment = new AddNotificationDialogFragment();
                    dialogFragment.show(getChildFragmentManager(), "AddNotificationDialogFragment");
                }
            });

        }
    }


    @Override
    public void onNotificationAdded(String notification) {
        // Add the new notification to the list
        notificationsList.add(0, notification); // Add to the beginning of the list
        notificationAdapter.notifyDataSetChanged(); // Notify adapter of data change
    }
}
