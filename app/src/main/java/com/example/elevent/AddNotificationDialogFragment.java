package com.example.elevent;

import static android.icu.number.NumberRangeFormatter.with;

import android.Manifest;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
            event = (Event) getArguments().getSerializable("event");
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
    }

    /**
     * Creates notification and adds to listener
     */
    // https://developer.android.com/develop/ui/views/notifications/build-notification
    // https://developer.android.com/develop/ui/views/notifications/navigation
    private void createNotification() {
        List<String> notifications = event.getNotifications();
        notifications.add(notificationText);
        event.setNotifications(notifications);

        EventDB db = new EventDB();
        db.updateEvent(event);

        Bundle args = new Bundle();
        args.putSerializable("event", event);
        NotificationCentreFragment notificationCentreFragment = new NotificationCentreFragment();
        notificationCentreFragment.setArguments(args);
        if (getActivity() instanceof MainActivity) {
            FragmentManagerHelper helper = ((MainActivity) getActivity()).getFragmentManagerHelper();
            helper.replaceFragment(notificationCentreFragment);
        }
    }
}