package com.example.elevent;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


public class CreateEventFragment extends DialogFragment {

    private Uri eventPosterURI = null;
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->{
        if (isGranted){
            getEventPosterImage();
        }
    });
    // OpenAI, 2024, ChatGPT, Allow user to upload image file
    private final ActivityResultLauncher<String> getContentLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri ->{
        if (uri != null){
            eventPosterURI = uri;
        }
    });


    interface CreateEventDialogListener{
        void onPositiveClick(Event event);
    }
    private CreateEventDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CreateEventDialogListener){
            listener = (CreateEventDialogListener) context;
        } else {
            throw new RuntimeException(context + " must implement CreateEventDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.create_event_fragment, null);
        EditText eventName = view.findViewById(R.id.event_name_edit_text);
        EditText eventAddress = view.findViewById(R.id.event_address_edit_text);
        EditText eventTime = view.findViewById(R.id.event_time_edit_text);
        EditText eventDate = view.findViewById(R.id.event_date_edit_text);
        EditText eventDescription = view.findViewById(R.id.event_description_edit_text);
        Button addEventImage = view.findViewById(R.id.add_event_image_button);
        // getting event image is buggy; gonna bypass for now
        addEventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Create an event")
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("Create Event", (dialog, which) -> {
                    if (eventName.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(), "Event Name Required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    listener.onPositiveClick(new Event(eventName.getText().toString(), eventPosterURI, eventAddress.getText().toString(), eventDescription.getText().toString(), eventDate.getText().toString(), eventTime.getText().toString()));
                })
                .create();
    }

    private void getEventPosterImage(){
        getContentLauncher.launch("image/*");
    }
    private void requestPermission(){requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);}
}
