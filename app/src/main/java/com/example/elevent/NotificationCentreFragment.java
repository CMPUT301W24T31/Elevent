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

    private NotificationCentreFragment.NotificationCentreDialogListener listener;

    /**
     * Called when a fragment is first attached to its host activity
     * @param context Host activity
     */
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

    /**
     * Called to have the fragment instantiate its user interface view
     * Initialize display for notifications
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return View for the fragment's UI, or null
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notificationcentre, container, false);

        // Find views by their respective IDs
        notificationListTextView = view.findViewById(R.id.notif_centre_textview);
        listOfNotifications = view.findViewById(R.id.list_of_notifs);
        return view;
    }

    /**
     * Called after the view has been created
     * Initialize UI that allow organizer to create an dpush new notification
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
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

    /**
     * Add the notification to the display
     * @param notification The notification text.
     */
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
