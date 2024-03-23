package com.example.elevent;

import android.Manifest;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* This file is responsible for providing the UI to allow the organizer to post a notification for their event
    Outstanding issues: pushing notifications causes crashing
 */

/**
 * A dialog fragment for creating and sending notifications.
 */
public class AddNotificationDialogFragment extends DialogFragment {

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
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
    }

    /**
     * Builds the notification AlertDialog
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return Built notification AlertDialog
     */

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Inflate the layout for the dialog
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_addnotif, null);
        EditText writeNotif = view.findViewById(R.id.notification_text);
        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        return builder
                .setView(view)
                .setTitle("Create Notification")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Send", (dialog, which) -> {
                    notificationText = writeNotif.getText().toString();
                    if (!notificationText.isEmpty()) {
                        requestNotificationPermission();
                        if (notificationSent) {
                            List<String> notifications = event.getNotifications();
                            notifications.add(notificationText);
                            event.setNotifications(notifications);
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("notifications", notifications);
                            updateNotifications(updates);
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to create notification", Toast.LENGTH_SHORT).show();
                    }
                })
                .create();
    }
    private void requestNotificationPermission(){
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
    }
    private void updateNotifications(Map<String, Object> updates) {
        EventDB db = new EventDB(new EventDBConnector());
        db.updateEvent(event.getEventName(), updates);
    }

    private void createNotification() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra("OpenNotificationFromFragment", "NotificationFragmentAttendee");
        intent.putExtra("event", event);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), "EleventChannel")
                .setContentTitle(event.getEventName())
                .setContentText(notificationText)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationText))
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(requireContext());
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(getContext(), "Permissions are required to access notifications", Toast.LENGTH_LONG).show();
            notificationSent = false;
            return;
        }
        notificationSent = true;
        notificationManagerCompat.notify(1, builder.build());
    }
}