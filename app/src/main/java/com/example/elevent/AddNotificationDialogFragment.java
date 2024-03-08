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

/**
 * A dialog fragment for adding notifications.
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddNotificationDialogListener) {
            listener = (AddNotificationDialogListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement AddNotificationDialogListener");
        }
    }

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