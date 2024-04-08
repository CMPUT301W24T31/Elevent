package com.example.elevent;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.Blob;

import org.checkerframework.checker.units.qual.N;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
        TextView eventAttendanceTextView = view.findViewById(R.id.event_attendance_textview);
        // Extracting event details from arguments
        assert getArguments() != null;
        Event event = (Event) getArguments().getSerializable("event");
        Button signUpButton = view.findViewById(R.id.sign_up_event_button);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString("userID", null);
        if(event!=null){
            List<String> signedUp = event.getSignedUpAttendees();
            if (signedUp.contains(userID)){
                String signedUpText = "Signed up!";
                signUpButton.setText(signedUpText);
            } else{
                signUpButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventSignUpDialogFragment eventSignUpDialogFragment = new EventSignUpDialogFragment();
                        Bundle args = new Bundle();
                        args.putSerializable("event", event);
                        args.putString("userID", userID);
                        eventSignUpDialogFragment.setArguments(args);
                        eventSignUpDialogFragment.show(requireActivity().getSupportFragmentManager(), "EventSignUpDialogFragment");
                        String signedUpText = "Signing up...";
                        signUpButton.setText(signedUpText);
                    }
                });
            }
        }

        if (event != null) {
            eventDescriptionTextView = view.findViewById(R.id.event_description_textview);
            eventDescriptionTextView.setText(event.getDescription()); //after description is implemented
            if (event.getEventPoster() != null){
                Blob eventPosterBlob = event.getEventPoster();
                Bitmap eventPoster = convertBlobToBitmap(eventPosterBlob);
                eventPosterImageView.setImageBitmap(eventPoster);
            }
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).updateAppBarTitle(event.getEventName());
            }

            // display attendance information
            int currentAttendees = event.getSignedUpAttendees().size();
            int maxAttendees = event.getMaxAttendance();
            int spotsRemaining = maxAttendees - currentAttendees;

            eventAttendanceTextView.setText(getString(R.string.spots_remaining, spotsRemaining));

            // adjust sign up button based on attendance
            List<String> signedUp = event.getSignedUpAttendees();

            if (signedUp.contains(userID)) {
                signUpButton.setText(R.string.already_signed_up);
                signUpButton.setEnabled(false);
                signUpButton.setBackgroundColor(getResources().getColor(com.google.android.material.R.color.design_default_color_background));
            } else {
                if (currentAttendees >= maxAttendees) {
                    signUpButton.setText(R.string.event_full);
                    signUpButton.setEnabled(false);
                    signUpButton.setBackgroundColor(getResources().getColor(com.google.android.material.R.color.design_default_color_background));
                } else {
                    signUpButton.setText(R.string.sign_up_for_event);
                    signUpButton.setEnabled(true);
                    signUpButton.setOnClickListener(v -> {
                        EventSignUpDialogFragment eventSignUpDialogFragment = new EventSignUpDialogFragment();
                        Bundle args = new Bundle();
                        args.putSerializable("Event", event);
                        args.putString("userID", userID);
                        eventSignUpDialogFragment.setArguments(args);
                        eventSignUpDialogFragment.show(requireActivity().getSupportFragmentManager(), "EventSignUpDialogFragment");
                    });
                }
            }
        }


        // Set event data (placeholders for now)
        //eventDescriptionTextView.setText("Event Description Here"); // Placeholder for event.getDescription()
        //eventPosterImageView.setImageBitmap(eventModel.getPosterImage()); // Placeholder for event.getPosterImage()
        if (event.getNotifications() != null && event.getNotifications().size() != 0) {
            mostRecentNotificationTextView.setText(event.getNotifications().get(event.getNotifications().size() - 1));
        }

        // On clicking the most recent notification, navigate to NotificationFragment

        mostRecentNotificationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putSerializable("event", event);
                NotificationFragmentAttendee notificationFragmentAttendee = new NotificationFragmentAttendee();
                notificationFragmentAttendee.setArguments(args);
                if (getActivity() instanceof MainActivity){
                    FragmentManagerHelper helper = ((MainActivity) getActivity()).getFragmentManagerHelper();
                    helper.replaceFragment(notificationFragmentAttendee);
                }
            }
        });


    }
    private Bitmap convertBlobToBitmap(Blob blob){
        byte[] byteArray = blob.toBytes();
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }
}