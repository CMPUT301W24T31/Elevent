package com.example.elevent;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
/*
    This file contains the implementation of the CreateEventFragment that is responsible for displaying the UI
    to allow an organizer to input event information and create the event.
 */
/**
 * This fragment displays the UI for allowing a user to input event information
 * Creates an event object
 */
public class CreateEventFragment extends Fragment {
    /**
     * Required empty constructor
     */
    public CreateEventFragment() {}

    /**
     * Interface for CreateEventListener
     */
    interface CreateEventListener{
        void createNewEvent();
    }

    private CreateEventListener listener;
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            getEventPosterImage();
        }
    });
    // OpenAI, 2024, ChatGPT, Allow user to upload image file
    private ActivityResultLauncher<String> getContentLauncher;
    private ActivityResultLauncher<ScanOptions> qrScannerLauncher;

    byte[] eventPosterByteArray;
    byte[] reusedQRBA;
    String sha256ReusedQRContent;

    /**
     * Interface for listener that handles event creation
     * Implemented by MainActivity
     */
    //create event listener to be implemented by main activity

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

    /**
     * Handles navigation to the MyEventsFragment
     */
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

    /**
     * Attaches listener to host activity
     * @param context Host activity
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CreateEventListener){
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

        qrScannerLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() != null){
                Log.d("ScanQRCodeActivity", "Scanned");
                String resultContents = result.getContents();
                reusedQRBA = generateQRCode(resultContents);
                sha256ReusedQRContent = sha256Hash(resultContents);
                checkQRInUse(sha256ReusedQRContent);
                if (reusedQRBA != null){
                    Toast.makeText(requireContext(), "Scan successful. Scanned code will be used for check in", Toast.LENGTH_LONG).show();
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

        Button reuseQRButton = view.findViewById(R.id.reuse_qr_button);
        reuseQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanQR();
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

                String maxAttendeesInput = eventMaxAttendees.getText().toString().trim();
                int maxAttendees = -1; // required minimum

                if (!maxAttendeesInput.isEmpty()) {
                    try {
                        maxAttendees = Integer.parseInt(maxAttendeesInput);
                        //maxAttendees = Integer.parseInt(eventMaxAttendees.getText().toString());
                        if (maxAttendees < 0) {
                            Toast.makeText(getActivity(), "Please enter a non-negative number for maximum attendees", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(getActivity(), "Please enter a valid number for maximum attendees", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                String eventID = String.valueOf(System.currentTimeMillis());
                Blob eventPoster = null;
                if (eventPosterByteArray != null){
                    eventPoster = Blob.fromBytes(eventPosterByteArray);
                }
                // Arguments for event constructor to be passed into addEvent
                byte[] promotionalQR = generateQRCode("Promotion," + eventID);
                String event_date = eventDate.getText().toString();
                String event_time = eventTime.getText().toString();
                String event_desc = eventDescription.getText().toString();
                String event_location = eventAddress.getText().toString();

                Event event;
                if (reusedQRBA == null){
                    byte[] checkInQR = generateQRCode("Check In:" + eventID);
                    event = new Event(eventID, organizerID, name, Blob.fromBytes(promotionalQR), Blob.fromBytes(checkInQR), 0,
                            event_date, event_time, event_desc, event_location, eventPoster, maxAttendees);
                } else {
                    event = new Event(eventID, organizerID, name, Blob.fromBytes(promotionalQR), Blob.fromBytes(reusedQRBA), 0,
                            event_date, event_time, event_desc, event_location, eventPoster, maxAttendees, sha256ReusedQRContent);
                }
                createEvent(event);
                listener.createNewEvent();
                

                // Pass the event object to CreatedEventFragment
                CreatedEventFragment createdEventFragment = new CreatedEventFragment();
                Bundle args = new Bundle();
                args.putParcelable("selected_event", event); // Assuming "event" is your Event object
                createdEventFragment.setArguments(args);

            }
        });
        return view;
    }

    /**
     * Displays the date of the event
     * @param eventDate Date of the event
     */
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

    /**
     * Displays the time of the event
     * @param eventTime Time of the event
     */
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

    /**
     * Generates a QR Code
     * @param data Data to be encoded into the QR Code
     * @return QR Code as a byte array
     */
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

    /**
     * Launches activity to scan QR
     */
    private void scanQR(){
        ScanOptions options = new ScanOptions();
        options.setOrientationLocked(true);
        options.setPrompt("Scan the QR code you would like to reuse");
        options.setCaptureActivity(CaptureAct.class);
        qrScannerLauncher.launch(options);
    }

    /**
     * Convert reused QR data to SHA-256
     * @param input data of the reused QR
     * @return SHA-256 encrypted data
     */
    // Open AI, 2024, ChatGPT, How to use SHA-256 hashing
    private String sha256Hash(String input){
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for(byte hashByte : hashBytes){
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1){
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Checks if reused QR is already in use by another activity
     * @param sha256 encrypted SHA-256 data
     */
    private void checkQRInUse(String sha256){
        FirebaseFirestore db = new EventDBConnector().getDb();

        db.collection("events").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    if (documentSnapshot.exists()){
                        String thisSHA256 = (String) documentSnapshot.get("sha256ReusedQRContent");
                        if (Objects.equals(thisSHA256, sha256)){
                            reusedQRBA = null;
                            Toast.makeText(requireContext(), "Cannot reuse QR: already in use by another event", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }
            }
        });
    }
}