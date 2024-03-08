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
                    while ((bytesRead = inputStream.read(buffer)) != 1){
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
//create event listener to be implemented by main activity
    interface CreateEventListener {
        void onPositiveClick(Event event);
        //void onCloseCreateEventFragment();
    }

    private CreateEventListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CreateEventListener) {
            listener = (CreateEventListener) context;
        } else {
            throw new RuntimeException(context + " must implement CreateEventListener");
        }
    }

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
                //return null;
            }
        });
        Button createEventButton = view.findViewById(R.id.create_the_event);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventName.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Event Name Required", Toast.LENGTH_SHORT).show();
                    //return null;
                }
                listener.onPositiveClick(new Event(eventName.getText().toString(), null, null, 0, eventPoster));
                //listener.onCloseCreateEventFragment();
                //return null;
            }
        });
        return view;
    }

    private void getEventPosterImage() {
        getContentLauncher.launch("image/*");
    }
}
