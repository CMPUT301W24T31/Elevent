package com.example.elevent;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("post")
                .setNegativeButton("cancel", null)
                .setPositiveButton("add", (dialog, which) -> {
                    String notificationText = writeNotif.getText().toString();
                    if (!notificationText.isEmpty()) {
                        listener.onNotificationAdded(notificationText);
                    } else {
                        Toast.makeText(getContext(), "Notification cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .create();
    }
}