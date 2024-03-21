package com.example.elevent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventSignUpDialogFragment extends DialogFragment {

    private Event event;
    private String userID;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("event");
            userID = getArguments().getString("userID");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_add_attendee_sign_up, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Disclaimer")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    addAttendeeSignUp(event, userID);
                })
                .create();
    }
    private void addAttendeeSignUp(Event event, String userID){
        List<String> newSignUp = event.getSignedUpAttendees();
        newSignUp.add(userID);
        event.setSignedUpAttendees(newSignUp);
        Map<String, Object> updates = new HashMap<>();
        updates.put("signedUpAttendees", newSignUp);
        onSignUp(event.getEventName(), updates);
    }
    private void onSignUp(String eventName, Map<String, Object> updates){
        EventDB eventDB = new EventDB(new EventDBConnector());

        eventDB.updateEvent(eventName, updates);
    }
}
