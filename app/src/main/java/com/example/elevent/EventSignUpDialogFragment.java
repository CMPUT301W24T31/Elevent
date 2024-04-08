package com.example.elevent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/*
    This file implements dialog fragment that shows a disclaimer telling the attendee that they must
    promise to attend if they sign up.
    As well, handles the sign up in the database
 */

/**
 * A dialog fragment that confirms that an attendee promises to attend the event if they sign up.
 * Handles sign up in the database
 */
public class EventSignUpDialogFragment extends DialogFragment {

    interface EventSignUpListener{
        void onSignUp();
    }
    private Event event;
    private String userID;
    private EventSignUpListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof EventSignUpListener) {
            listener = (EventSignUpListener) context;
        } else {
            throw new RuntimeException(context + " must implement EventSignUpListener");
        }
    }

    /**
     * Called to do initial creation of a fragment
     * Gets the event being signed up to and the user signing up
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("event");
            userID = getArguments().getString("userID");
        }
    }

    /**
     * Builds the dialog fragment
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return Builder of the dialog fragment
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_add_attendee_sign_up, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Disclaimer")
                .setNegativeButton("Cancel", ((dialog, which) -> {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("event", event);
                    EventViewAttendee eventViewAttendee = new EventViewAttendee();
                    eventViewAttendee.setArguments(bundle);
                    if (getActivity() instanceof MainActivity){
                        FragmentManagerHelper helper = ((MainActivity) getActivity()).getFragmentManagerHelper();
                        helper.replaceFragment(eventViewAttendee);
                    }
                }))
                .setPositiveButton("Confirm", (dialog, which) -> {
                    addAttendeeSignUpToEvent();
                    addEventSignedUpByAttendee();
                    Toast.makeText(requireContext(), "You are now signed up!", Toast.LENGTH_LONG).show();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("event", event);
                    EventViewAttendee eventViewAttendee = new EventViewAttendee();
                    eventViewAttendee.setArguments(bundle);
                    listener.onSignUp();
                    if (getActivity() instanceof MainActivity){
                        FragmentManagerHelper helper = ((MainActivity) getActivity()).getFragmentManagerHelper();
                        helper.replaceFragment(eventViewAttendee);
                    }
                })
                .create();
    }

    /**
     * Updates event's signed up attendees list in the database
     */
    private void addAttendeeSignUpToEvent() {
        List<String> newSignUp = event.getSignedUpAttendees();
        newSignUp.add(userID);
        event.setSignedUpAttendees(newSignUp);
        EventDB eventDB = new EventDB(new EventDBConnector());
        eventDB.updateEvent(event);
    }

    /**
     * Adds the event to the list of signed up events for the user
     */
    private void addEventSignedUpByAttendee() {
        UserDBConnector connector = new UserDBConnector();
        FirebaseFirestore db = connector.getDb();
        db.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class); // TODO: fix
                    if (user != null) {
                        List<String> signedUpEvents = user.getSignedUpEvents();
                        signedUpEvents.add(event.getEventID());
                        user.setSignedUpEvents(signedUpEvents);
                        UserDB userDB = new UserDB(new UserDBConnector());
                        userDB.updateUser(user);
                    } else {
                        Log.d("addEventSignedUpByAttendee", "User is not in db");
                    }
                } else {
                    Log.d("addEventSignedUpByAttendee", "Document does not exist");
                }
            }
        });
    }
}