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

        try {
            // Initialize UI components
            TextView eventDescriptionTextView = view.findViewById(R.id.event_description_textview);
            ImageView eventPosterImageView = view.findViewById(R.id.event_poster);
            TextView mostRecentNotificationTextView = view.findViewById(R.id.notification_text);
            TextView eventAttendanceTextView = view.findViewById(R.id.event_attendance_textview);
            Button signUpButton = view.findViewById(R.id.sign_up_event_button);

            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String userID = sharedPreferences.getString("userID", null);

            Bundle args = getArguments();
            if (args != null) {
                Event event = (Event) args.getSerializable("event");
                if (event != null) {
                    eventDescriptionTextView.setText(event.getDescription());

                    if (event.getEventPoster() != null) {
                        Blob eventPosterBlob = event.getEventPoster();
                        Bitmap eventPoster = convertBlobToBitmap(eventPosterBlob);
                        eventPosterImageView.setImageBitmap(eventPoster);
                    }

                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).updateAppBarTitle(event.getEventName());
                    }

                    int currentAttendees = event.getSignedUpAttendees().size();
                    int maxAttendees = event.getMaxAttendance();
                    eventAttendanceTextView.setText(String.format("Attendees: %d/%d", currentAttendees, maxAttendees));

                    List<String> signedUp = event.getSignedUpAttendees();

                    if (signedUp.contains(userID)) {
                        signUpButton.setText("Already Signed Up");
                        signUpButton.setEnabled(false);
                        signUpButton.setBackgroundColor(getResources().getColor(com.google.android.material.R.color.design_default_color_background));
                    } else {
                        if (currentAttendees >= maxAttendees) {
                            signUpButton.setText("Event Full");
                            signUpButton.setEnabled(false);
                            signUpButton.setBackgroundColor(getResources().getColor(com.google.android.material.R.color.design_default_color_background));
                        } else {
                            signUpButton.setText("Sign Up");
                            signUpButton.setEnabled(true);
                            signUpButton.setOnClickListener(v -> {
                                EventSignUpDialogFragment eventSignUpDialogFragment = new EventSignUpDialogFragment();
                                Bundle signUpArgs = new Bundle();
                                signUpArgs.putSerializable("event", event);
                                signUpArgs.putString("userID", userID);
                                eventSignUpDialogFragment.setArguments(signUpArgs);
                                eventSignUpDialogFragment.show(requireActivity().getSupportFragmentManager(), "EventSignUpDialogFragment");
                                signUpButton.setText("Signed Up");
                            });
                        }
                    }

                    mostRecentNotificationTextView.setOnClickListener(v -> {
                        // Navigate to NotificationFragment
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.activity_main_framelayout, new NotificationFragmentAttendee())
                                .addToBackStack(null)
                                .commit();
                    });
                }
            }
        } catch(Exception e){
            // If an error occurs, replace the fragment with the error fragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_main_framelayout, new ErrorFragment())
                    .commit();
        }
    }

    private Bitmap convertBlobToBitmap(Blob blob){
        byte[] byteArray = blob.toBytes();
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }
}
