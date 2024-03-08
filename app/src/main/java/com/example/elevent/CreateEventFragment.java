package com.example.elevent;

import android.Manifest;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * This fragment displays the UI for allowing a user to input event information
 * Creates an event object
 */
public class CreateEventFragment extends Fragment {



    private byte[] eventPoster = null;
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            getEventPosterImage();
        }
    });
    // OpenAI, 2024, ChatGPT, Allow user to upload image file
    private final ActivityResultLauncher<String> getContentLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        if (uri != null) {
            Uri eventPosterURI = uri;
            // OpenAI, 2024, ChatGPT, Convert to byte array
            try {
                InputStream inputStream = requireActivity().getContentResolver().openInputStream(eventPosterURI);
                if (inputStream != null){
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1){ //changed logic to -1(end of array)
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    eventPoster = outputStream.toByteArray();
                    inputStream.close();
                    outputStream.close();
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    });

    /**
     * Interface for listener that handles event creation
     * Implemented by MainActivity
     */
    //create event listener to be implemented by main activity
    interface CreateEventListener {
        void onPositiveClick(Event event);
        //void onCloseCreateEventFragment();
    }

    private CreateEventListener listener;

    private void createEvent(Event event) {
        EventDB eventDB = new EventDB(new EventDBConnector());

        eventDB.addEvent(event).thenRun(() -> {
            // Ensure operations that update the UI are run on the main thread
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getActivity(), "Event added successfully", Toast.LENGTH_SHORT).show();
                navigateToMyEventsFragment(); // Navigate back to MyEventsFragment after event creation
            });
        }).exceptionally(e -> {
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getActivity(), "Failed to add event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
            return null;
        });
    }

    private void navigateToMyEventsFragment() {
        // Ensure this operation is also considered to be executed on the main thread
        if (isAdded() && getActivity() != null && getFragmentManager() != null) {
            MyEventsFragment myEventsFragment = new MyEventsFragment();
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_main_framelayout, myEventsFragment)
                    .commit();
        }
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CreateEventListener) {
            listener = (CreateEventListener) context;
        } else {
            throw new RuntimeException(context + " must implement CreateEventListener");
        }
    }

    /**
     * Called to have the fragment instantiate its user interface view
     * Instantiates the UI features that accept user input for event details
     * Creates the event object
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return View for the user interface
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_createevent, container, false);
        EditText eventName = view.findViewById(R.id.event_name_input);
        EditText eventAddress = view.findViewById(R.id.event_location_input);
        EditText eventTime = view.findViewById(R.id.event_time_input);
        EditText eventDate = view.findViewById(R.id.event_date_input);
        EditText eventDescription = view.findViewById(R.id.event_description_input);
        Button addEventImage = view.findViewById(R.id.eventPoster_create);
        addEventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        });
        Button createEventButton = view.findViewById(R.id.create_the_event);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = eventName.getText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(getActivity(), "Event Name Required", Toast.LENGTH_SHORT).show();
                    return; // Add return to exit early if validation fails
                }

                // Arguments for event constructor to be passed into addEvent
                byte[] promotionalQR = null;
                byte[] checkinQR = null;
                String event_date = eventDate.getText().toString();
                String event_time = eventTime.getText().toString();
                String event_desc = eventDescription.getText().toString();
                String event_location = eventAddress.getText().toString();
                String[] notifications = null;

                Event event = new Event(name, null, null, 0,
                        event_date, event_time, event_desc, event_location,eventPoster, notifications);
                // Call createEvent method to add the event and handle navigation
                createEvent(event);
            }
        });

        return view;
    }

    /**
     * Launches the content launcher that allows the user to upload an event poster
     */
    private void getEventPosterImage() {
        getContentLauncher.launch("image/*");
    }
}