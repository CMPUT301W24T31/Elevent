package com.example.elevent;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notificationcentre, container, false);

        // Find views by their respective IDs
        notificationListTextView = view.findViewById(R.id.notif_centre_textview);
        listOfNotifications = view.findViewById(R.id.list_of_notifs);
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
                    // Display the dialog fragment to add notification
                    AddNotificationDialogFragment dialogFragment = new AddNotificationDialogFragment();
                    dialogFragment.show(getChildFragmentManager(), "AddNotificationDialogFragment");
                }
            });

        }
    }


    @Override
    public void onNotificationAdded(String notification) {
        String currentText = notificationListTextView.getText().toString();
        String newText = currentText + "\n" + notification; // Append the new notification
        notificationListTextView.setText(newText);
    }


    // You can also set data to your TextView and ListView
        // attendeeListTextView.setText("Attendees List");
        // Set adapter to ListView
        // Example: listOfAttendees.setAdapter(yourAdapter);

}
