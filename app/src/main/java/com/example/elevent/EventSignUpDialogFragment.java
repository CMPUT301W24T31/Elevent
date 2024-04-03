package com.example.elevent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
                    if (getActivity() instanceof MainActivity){
                        FragmentManagerHelper helper = ((MainActivity) getActivity()).getFragmentManagerHelper();
                        helper.replaceFragment(eventViewAttendee);
                    }
                })
                .create();
    }
    private void addAttendeeSignUpToEvent() {
        List<String> newSignUp = event.getSignedUpAttendees();
        newSignUp.add(userID);
        event.setSignedUpAttendees(newSignUp);
        EventDB eventDB = new EventDB(new EventDBConnector());
        eventDB.updateEvent(event);
    }

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
