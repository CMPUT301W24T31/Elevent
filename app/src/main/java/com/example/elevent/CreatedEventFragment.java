package com.example.elevent;

import android.Manifest;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * This fragment displays the organizer's view of an event they created
 * Allows organizer to edit event details
 */
public class CreatedEventFragment extends Fragment {

    /*private Uri eventPosterURI = null;
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            getEventPosterImage();
        }
    });
    // OpenAI, 2024, ChatGPT, Allow user to upload image file
    private final ActivityResultLauncher<String> getContentLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        if (uri != null) {
            eventPosterURI = uri;
        }
    });*/

    /**
     * Listener that checks for when the user clicks a button to crea
     */
    interface CreatedEventListener {
        //void onCreateEvent(Event event);

        void onPositiveClick(Event event);
    }
    //comment random. 

    private Event event;
    private CreatedEventListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CreatedEventListener) {
            listener = (CreatedEventListener) context;
        } else {
            throw new RuntimeException(context + " must implement CreatedEventListener (tapped on event in myEvents)");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            event = (Event) getArguments().getSerializable("selected_event");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_createdevent, container, false);
        EditText eventName = view.findViewById(R.id.event_name_text);
        EditText eventAddress = view.findViewById(R.id.event_location_text);
        EditText eventTime = view.findViewById(R.id.event_time_text);
        EditText eventDate = view.findViewById(R.id.event_date_text);
        EditText eventDescription = view.findViewById(R.id.event_description_text);
        Button addEventImage = view.findViewById(R.id.eventPoster_image);
        ImageView checkInQR = view.findViewById(R.id.checkinQR_image);
        //checkInQR.setImageBitmap(BitmapFactory.decodeByteArray(event.getCheckinQR(),0, event.getCheckinQR().length));




        /*addEventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        });*/


       /* changed the button to edit event button to enable editing the event info or whatever,
            but the implementation is not there (its the same as CREATE for now)
        Button createdEventButton = view.findViewById(R.id.edit_the_event);
        createdEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventName.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Event Name Required", Toast.LENGTH_SHORT).show();
                    return;
                }
                listener.onPositiveClick(new Event(eventName.getText().toString(), eventPosterURI, eventAddress.getText().toString(), eventDescription.getText().toString(), eventDate.getText().toString(), eventTime.getText().toString()));
            }
        });
        */
        return view;
    }

    /*private void getEventPosterImage() {
        getContentLauncher.launch("image/*");
    }*/

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button manageEventButton = view.findViewById(R.id.manage_the_event);
        manageEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //did fragment switching using fragment helper, creates instance of main to tie with the fragment to enable switching
                //(same implementation as the random floating button in all events :))
                if (getActivity() instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    FragmentManagerHelper helper = mainActivity.getFragmentManagerHelper();
                    helper.replaceFragment(new ManageEventFragment());
                }
                //return null;
            }
        });
    }
}
