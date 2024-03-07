package com.example.elevent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class ManageEventFragment extends Fragment {

    /*

    tried to initialize but not too sure if its right so gonna comment it all out T-T

    private Button notifCentreButton;
    private TextView attendeeListTextView;
    private ListView listOfAttendees;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manageevent, container, false);

        // Find views by their respective IDs
        notifCentreButton = rootView.findViewById(R.id.notif_centre_button);
        attendeeListTextView = rootView.findViewById(R.id.attendee_list_textview);
        listOfAttendees = rootView.findViewById(R.id.list_of_attendees);

        // Set click listener for the notifCentreButton
        notifCentreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace current fragment with NotificationCentreFragment
                NotificationCentreFragment notificationCentreFragment = new NotificationCentreFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.layout.fragment_notificationcentre, notificationCentreFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        // You can also set data to your TextView and ListView
        // attendeeListTextView.setText("Attendees List");
        // Set adapter to ListView
        // Example: listOfAttendees.setAdapter(yourAdapter);

        return rootView;
    }*/
}