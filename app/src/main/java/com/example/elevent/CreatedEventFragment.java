package com.example.elevent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.Blob;
/*
    This file contains the implementation of the CreatedEventFragment that is responsible for displaying the UI of the organizer's view
    of a created event. The organizer can manage and edit the event in this fragment.
    Outstanding issues: Need to display the QR codes,
 */
/**
 * This fragment displays the organizer's view of an event they created
 * Allows organizer to edit event details
 */
public class CreatedEventFragment extends Fragment {

    /**
     * Listener that handles event creation
     * Implemented by MainActivity
     */
    interface CreatedEventListener {
        //void onCreateEvent(Event event);

        void editEvent(Event event);
    }

    private Event selectedEvent;
    private CreatedEventListener listener;

    /**
     * Called when a fragment is first attached to its host activity
     * @param context Host activity
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CreatedEventListener) {
            listener = (CreatedEventListener) context;
        } else {
            throw new RuntimeException(context + " must implement CreatedEventListener (tapped on event in myEvents)");
        }
    }

    /**
     * Called to do initial creation of a fragment.
     * Gets the selected event in the array adapter
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            selectedEvent = (Event) getArguments().getSerializable("selected_event");
        }
    }


    /**
     * Called to have the fragment instantiate its user interface view.
     * Initializes the UI for editing the event information
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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_createdevent, container, false);
        EditText eventName = view.findViewById(R.id.event_name_text);
        eventName.setText(selectedEvent.getEventName());
        EditText eventAddress = view.findViewById(R.id.event_location_text);
        if (selectedEvent.getLocation() != null){
            eventAddress.setText(selectedEvent.getLocation());
        }
        EditText eventTime = view.findViewById(R.id.event_time_text);
        if (selectedEvent.getTime() != null){
            eventTime.setText(selectedEvent.getTime());
        }
        EditText eventDate = view.findViewById(R.id.event_date_text);
        if (selectedEvent.getDate() != null){
            eventDate.setText(selectedEvent.getDate());
        }
        EditText eventDescription = view.findViewById(R.id.event_description_text);
        if (selectedEvent.getDescription() != null){
            eventDescription.setText(selectedEvent.getDescription());
        }
        Button addEventImage = view.findViewById(R.id.eventPoster_image);

        ImageView checkInQRImageView = view.findViewById(R.id.checkinQR_image);
        ImageView promotionalQRImageView = view.findViewById(R.id.promotionalQR_image);

        if (selectedEvent != null) {
            eventName.setText(selectedEvent.getEventName());
            eventAddress.setText(selectedEvent.getLocation());
            eventTime.setText(selectedEvent.getTime());
            eventDate.setText(selectedEvent.getDate());
            eventDescription.setText(selectedEvent.getDescription());

            Blob checkinQRBlob = selectedEvent.getCheckinQR();
            if (checkinQRBlob != null) {
                Bitmap checkinQRBitmap = convertBlobToBitmap(checkinQRBlob);
                if (checkinQRBitmap != null) {
                    checkInQRImageView.setImageBitmap(checkinQRBitmap);
                }
            }

            Blob promotionalQRBlob = selectedEvent.getPromotionalQR();
            if (promotionalQRBlob != null) {
                Bitmap promotionalQRBitmap = convertBlobToBitmap(promotionalQRBlob);
                if (promotionalQRBitmap != null) {
                    promotionalQRImageView.setImageBitmap(promotionalQRBitmap);
                }
            }
        }

        return view;
    }


    /**
     * Called immediately after onCreateView has returned
     * Initializes option to confirm changes and return to previous fragment
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText eventNameText = view.findViewById(R.id.event_name_text);
        EditText eventLocationText = view.findViewById(R.id.event_location_text);
        EditText eventTimeText = view.findViewById(R.id.event_time_text);
        EditText eventDateText = view.findViewById(R.id.event_date_text);
        EditText eventDescriptionText = view.findViewById(R.id.event_description_text);
        Button addEventImageButton = view.findViewById(R.id.eventPoster_image);

        ImageView checkInQRImageView = view.findViewById(R.id.checkinQR_image);
        ImageView promotionalQRImageView = view.findViewById(R.id.promotionalQR_image);

        Button manageEventButton = view.findViewById(R.id.manage_the_event);
        Button saveChangesButton = view.findViewById(R.id.save_the_event);
        manageEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManageEventFragment manageEventFragment = new ManageEventFragment();
                Bundle args = new Bundle();
                args.putSerializable("event", selectedEvent);
                manageEventFragment.setArguments(args);
                //did fragment switching using fragment helper, creates instance of main to tie with the fragment to enable switching
                //(same implementation as the random floating button in all events :))
                if (getActivity() instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    FragmentManagerHelper helper = mainActivity.getFragmentManagerHelper();
                    helper.replaceFragment(manageEventFragment);
                }
                //return null;
            }
        });

        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve updated data from UI fields
                String updatedEventName = eventNameText.getText().toString();
                String updatedEventLocation = eventLocationText.getText().toString();
                String updatedEventTime = eventTimeText.getText().toString();
                String updatedEventDate = eventDateText.getText().toString();
                String updatedEventDescription = eventDescriptionText.getText().toString();

                // Update selectedEvent object with the retrieved data
                selectedEvent.setEventName(updatedEventName);
                selectedEvent.setLocation(updatedEventLocation);
                selectedEvent.setTime(updatedEventTime);
                selectedEvent.setDate(updatedEventDate);
                selectedEvent.setDescription(updatedEventDescription);

                // Notify the listener about the positive action with the updated selectedEvent
                if (listener != null) {
                    listener.editEvent(selectedEvent);
                }
            }
        });
    }

    // https://firebase.google.com/docs/reference/android/com/google/firebase/firestore/Blob#toBytes()
    // OpenAI, 2024, ChatGPT, Display QR code from byte array
    private Bitmap convertBlobToBitmap(Blob blob){
        byte[] byteArray = blob.toBytes();
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }
}
