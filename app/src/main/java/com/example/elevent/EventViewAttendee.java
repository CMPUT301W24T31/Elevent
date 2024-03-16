package com.example.elevent;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
/*
    This file is responsible for displaying the UI for an attendee's view of an event
    Outstanding issues: figure out attributes of event, such as QR code and notifications
 */
/**
 * This fragment displays the UI for the attendee's view of an event
 */
public class EventViewAttendee extends Fragment {

    // Placeholder for event data model
    // private EventModel eventModel;

    public EventViewAttendee() {
        // Required empty public constructor
    }


    /**
     * Called to have the fragment instantiate its user interface view
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_attendee, container, false);
    }

    /**
     * Called immediately after OnCreateView has returned, but before any saved state has been restored in to the view.
     * Initialize the UI features
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize UI components
        TextView eventDescriptionTextView = view.findViewById(R.id.event_description_textview);
        ImageView eventPosterImageView = view.findViewById(R.id.event_poster);
        TextView mostRecentNotificationTextView = view.findViewById(R.id.notification_text);
        // Extracting event details from arguments
        Event event = (Event) getArguments().getSerializable("event");
        Button signUpButton = view.findViewById(R.id.sign_up_event_button);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString("userID", null);
        System.out.println(userID);
        if (event!= null) {
            String[] signedUp = event.getSignedUpAttendees();
            System.out.println(Arrays.toString(signedUp));
            if (Arrays.asList(signedUp).contains(userID)){
                signUpButton.setVisibility(View.INVISIBLE);
            }
        }

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (event != null){
                    String[] signedUp = event.getSignedUpAttendees();
                    String[] newSignedUp = new String[signedUp.length + 1];
                    System.arraycopy(signedUp, 0, newSignedUp, 0, signedUp.length);
                    newSignedUp[signedUp.length] = userID;
                    event.setSignedUpAttendees(newSignedUp);
                    System.out.println(Arrays.toString(event.getSignedUpAttendees()));
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("signedUpAttendees", newSignedUp);
                    onSignUp(event.getEventName(), updates);
                }
            }
        });

        if (event != null) {
            eventDescriptionTextView = view.findViewById(R.id.event_description_textview);
            eventDescriptionTextView.setText(event.getDescription()); //after description is implemented
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).updateAppBarTitle(event.getEventName());
            }
        }


        // Set event data (placeholders for now)
        //eventDescriptionTextView.setText("Event Description Here"); // Placeholder for event.getDescription()
        //eventPosterImageView.setImageBitmap(eventModel.getPosterImage()); // Placeholder for event.getPosterImage()
        mostRecentNotificationTextView.setText("Most Recent Notification Here"); // Placeholder for getLastNotification()

        // On clicking the most recent notification, navigate to NotificationFragment

        mostRecentNotificationTextView.setOnClickListener(v -> {
            // Navigate to NotificationFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_main_framelayout, new NotificationFragmentAttendee())
                    .addToBackStack(null)
                    .commit();
        });


    }
    private void onSignUp(String eventName, Map<String, Object> updates){
        EventDB eventDB = new EventDB(new EventDBConnector());

        eventDB.updateEvent(eventName, updates);
    }
}
