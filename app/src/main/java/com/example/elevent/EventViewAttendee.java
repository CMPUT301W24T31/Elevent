package com.example.elevent;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class EventViewAttendee extends Fragment {

    // Placeholder for event data model
    // private EventModel eventModel;

    public EventViewAttendee() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_attendee, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize UI components
        TextView eventDescriptionTextView = view.findViewById(R.id.event_description_textview);
        ImageView eventPosterImageView = view.findViewById(R.id.event_poster);
        TextView mostRecentNotificationTextView = view.findViewById(R.id.notification_text);
        // Extracting event details from arguments
        Event event = (Event) getArguments().getSerializable("event");

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
}
