package com.example.elevent;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
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
import androidx.fragment.app.DialogFragment;
/* This file is responsible for providing the UI to allow the organizer to post a notification for their event
    Outstanding issues: pushing notifications causes crashing
 */

/**
 * A dialog fragment for creating and sending notifications.
 */
public class AddNotificationDialogFragment extends DialogFragment {

    public void setListener(NotificationCentreFragment notificationCentreFragment) {
    }

    /**
     * Listener interface to handle notification addition events.
     */
    interface AddNotificationDialogListener {
        /**
         * Called when a notification is added.
         *
         * @param notification The notification text.
         */
        void onNotificationAdded(String notification);
    }

    private AddNotificationDialogListener listener;
    private Event event;
    private final static int NOTIFICATION_PERMISSION_REQUEST_CODE = 100;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private String notificationText;
    private boolean notificationSent;


    /**
     * Called when a fragment is first attached to its context.
     * @param context The context to which the fragment is attached.
     * @throws RuntimeException if the context does not implement {@link AddNotificationDialogListener}.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddNotificationDialogListener) {
            listener = (AddNotificationDialogListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement AddNotificationDialogListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("event");
        }
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->{
            if(isGranted){
                createNotification();
            } else{
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_LONG).show();
            }
        });
        // Request permission when the fragment is created
        requestNotificationPermission();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_addnotif, null);
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
                        requestNotificationPermission();
                    } else {
                        Toast.makeText(getContext(), "Failed to create notification", Toast.LENGTH_SHORT).show();
                    }
                })
                .create();
    }

    private void requestNotificationPermission(){
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
    }

    private void createNotification() {
        if (event != null) {
            // Notification creation code here
            // Make sure to handle notification creation properly
            // Don't forget to call listener.onNotificationAdded() if necessary
        }
        if (listener != null) {
            listener.onNotificationAdded(notificationText);
        } else {
            Toast.makeText(requireContext(), "Event is null", Toast.LENGTH_SHORT).show();
        }
    }
}