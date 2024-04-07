package com.example.elevent;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.Blob;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
/*
    This file contains the implementation of the CreateEventFragment that is responsible for displaying the UI
    to allow an organizer to input event information and create the event.
    Outstanding issues: encoding and creating QR code for activities needs work
 */
/**
 * This fragment displays the UI for allowing a user to input event information
 * Creates an event object
 */
public class CreateEventFragment extends Fragment {


    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            getEventPosterImage();
        }
    });
    // OpenAI, 2024, ChatGPT, Allow user to upload image file
    private ActivityResultLauncher<String> getContentLauncher;

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
    byte[] eventPosterByteArray;

    private void createEvent(Event event) {
        EventDB eventDB = new EventDB(new EventDBConnector());

        eventDB.addEvent(event).thenRun(() -> {
            // ensure operations run on the main thread
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getActivity(), "Event added successfully", Toast.LENGTH_SHORT).show();
                navigateToMyEventsFragment(); // navigate back to MyEventsFragment after event creation
            });
        }).exceptionally(e -> {
            getActivity().runOnUiThread(() -> {
                //Toast.makeText(getActivity(), "Failed to add event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                throw new RuntimeException(e.getMessage());
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
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return View for the user interface
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_createevent, container, false);

        try {
            EditText eventName = view.findViewById(R.id.event_name_input);
            EditText eventAddress = view.findViewById(R.id.event_location_input);
            EditText eventTime = view.findViewById(R.id.event_time_input);
            EditText eventDate = view.findViewById(R.id.event_date_input);
            EditText eventDescription = view.findViewById(R.id.event_description_input);
            Button addEventImage = view.findViewById(R.id.eventPoster_create);
            ImageView eventPosterView = view.findViewById(R.id.create_event_image_view);
            TextView editEventPoster = view.findViewById(R.id.change_event_poster_text);
            TextView deleteEventPoster = view.findViewById(R.id.remove_event_poster_text);
            EditText eventMaxAttendees = view.findViewById(R.id.event_max_attendees_input);

            eventMaxAttendees.setInputType(InputType.TYPE_CLASS_NUMBER);

            eventPosterView.setVisibility(View.INVISIBLE);
            editEventPoster.setVisibility(View.INVISIBLE);
            deleteEventPoster.setVisibility(View.INVISIBLE);

            eventDate.setInputType(InputType.TYPE_NULL);
            eventTime.setInputType(InputType.TYPE_NULL);
            getContentLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    // OpenAI, 2024, ChatGPT, Convert to byte array
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        eventPosterByteArray = byteArrayOutputStream.toByteArray();
                        eventPosterView.setImageBitmap(bitmap);
                        eventPosterView.setVisibility(View.VISIBLE);
                        addEventImage.setVisibility(View.INVISIBLE);
                        editEventPoster.setVisibility(View.VISIBLE);
                        deleteEventPoster.setVisibility(View.VISIBLE);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            addEventImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                }
            });

            editEventPoster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                }
            });
            deleteEventPoster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventPosterByteArray = null;
                    eventPosterView.setVisibility(View.INVISIBLE);
                    deleteEventPoster.setVisibility(View.INVISIBLE);
                    editEventPoster.setVisibility(View.INVISIBLE);
                    addEventImage.setVisibility(View.VISIBLE);
                }
            });

            eventDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDateDialog(eventDate);
                }
            });

            eventTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTimeDialog(eventTime);
                }
            });

            Button createEventButton = view.findViewById(R.id.create_the_event);
            createEventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    String organizerID = sharedPreferences.getString("userID", null);
                    String name = eventName.getText().toString();
                    if (name.isEmpty()) {
                        Toast.makeText(getActivity(), "Event Name Required", Toast.LENGTH_SHORT).show();
                        return; // Add return to exit early if validation fails
                    }

                    int maxAttendees = 0; // required minimum
                    try {
                        maxAttendees = Integer.parseInt(eventMaxAttendees.getText().toString());
                    } catch (NumberFormatException e) {
                        Toast.makeText(getActivity(), "Please enter a valid number for maximum attendees", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String eventID = String.valueOf(System.currentTimeMillis());
                    Blob eventPoster = null;
                    if (eventPosterByteArray != null) {
                        eventPoster = Blob.fromBytes(eventPosterByteArray);
                    }
                    // Arguments for event constructor to be passed into addEvent
                    byte[] promotionalQR = generateQRCode("Promotion," + eventID);
                    byte[] checkInQR = generateQRCode("Check In," + eventID);
                    String event_date = eventDate.getText().toString();
                    String event_time = eventTime.getText().toString();
                    String event_desc = eventDescription.getText().toString();
                    String event_location = eventAddress.getText().toString();

                    Event event = new Event(eventID, organizerID, name, Blob.fromBytes(promotionalQR), Blob.fromBytes(checkInQR), 0,
                            event_date, event_time, event_desc, event_location, eventPoster, maxAttendees);
                    // Call createEvent method to add the event and handle navigation
                    createEvent(event);


                    // Pass the event object to CreatedEventFragment
                    CreatedEventFragment createdEventFragment = new CreatedEventFragment();
                    Bundle args = new Bundle();
                    args.putSerializable("selected_event", event); // Assuming "event" is your Event object
                    createdEventFragment.setArguments(args);

                }
            });
        } catch (Exception e) {
            showErrorFragment();
        }
        return view;
    }

    private void showErrorFragment() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_main_framelayout, new ErrorFragment())
                .commit();
    }

    private void showDateDialog(final EditText eventDate) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy"); // Corrected date format
                eventDate.setText(simpleDateFormat.format(calendar.getTime()));
            }
        };

        new DatePickerDialog(requireContext(), dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimeDialog(final EditText eventTime) {
        final Calendar calendar = Calendar.getInstance();

        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                eventTime.setText(simpleDateFormat.format(calendar.getTime()));
            }
        };

        new TimePickerDialog(requireContext(), timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
    }



    /**
     * Launches the content launcher that allows the user to upload an event poster
     */
    private void getEventPosterImage() {
        getContentLauncher.launch("image/*");
    }

    private byte[] generateQRCode(String data) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 300, 300);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}