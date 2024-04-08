package com.example.elevent;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.List;
/* This file is responsible for providing the UI to allow the organizer to post a notification for their event
    Outstanding issues: pushing notifications causes crashing
 */

/**
 * A dialog fragment for creating and sending notifications.
 */
public class AddNotificationDialogFragment extends DialogFragment {

    private Event event;
    private final static int NOTIFICATION_PERMISSION_REQUEST_CODE = 100;
    private String notificationText;
    private boolean notificationSent;

    /**
     * Called to do initial creation of a fragment. Gets event that is sending notification and initializes permission requester
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = getArguments().getParcelable("event");
        }
    }

    /**
     * Builds notification dialog fragment.
     *
     * @param savedInstanceState The last saved instance state of the Fragment,
     *                           or null if this is a freshly created Fragment.
     * @return The dialog fragment builder
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        try {
            View view = getLayoutInflater().inflate(R.layout.fragment_addnotif, null);
            EditText writeNotif = view.findViewById(R.id.notification_text);
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            return builder
                    .setView(view)
                    .setTitle("Create Notification")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Send", (dialog, which) -> {
                        notificationText = writeNotif.getText().toString();
                        if (!notificationText.isEmpty()) {
                            // Check permission again before sending notification
                            createNotification();
                        } else {
                            Toast.makeText(getContext(), "Failed to create notification", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .create();
        } catch (Exception e) {
            showErrorFragment();
        }
        return super.onCreateDialog(savedInstanceState); // Return default dialog if an error occurs
    }

    private void showErrorFragment() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.allevents_error, new ErrorFragment())
                .commit();
    }

    /**
     * Creates notification and adds to listener
     */
    // https://developer.android.com/develop/ui/views/notifications/build-notification
    // https://developer.android.com/develop/ui/views/notifications/navigation
    private void createNotification() {
        if (event != null) {
            List<String> notifications = event.getNotifications();
            if (notifications != null) {
                notifications.add(notificationText);
                event.setNotifications(notifications);

                EventDB db = new EventDB();
                db.updateEvent(event);

                Bundle args = new Bundle();
                args.putParcelable("event", event);
                NotificationCentreFragment notificationCentreFragment = new NotificationCentreFragment();
                notificationCentreFragment.setArguments(args);
                if (getActivity() instanceof MainActivity) {
                    FragmentManagerHelper helper = ((MainActivity) getActivity()).getFragmentManagerHelper();
                    helper.replaceFragment(notificationCentreFragment);
                }
            } else {
                Toast.makeText(getContext(), "Failed to add notification: notifications list is null", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Failed to add notification: event is null", Toast.LENGTH_SHORT).show();
        }
    }

}