package com.example.elevent;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationRequest;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.FusedOrientationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.CancellationTokenSource;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
/*
    This file contains the implementation of the ScannerFragment that is responsible for
    requesting for camera permission and opening the camera to scan QR codes.
 */
/**
 * This fragment request permission to open the camera and scans the QR code
 */
public class ScannerFragment extends Fragment {

    // https://github.com/journeyapps/zxing-android-embedded/blob/master/sample/src/main/java/example/zxing/MainActivity.java
    private ActivityResultLauncher<ScanOptions> qrScannerLauncher;
    private ScannerListener listener;
    private ActivityResultLauncher<String[]> locationPermissionRequest;
    private LatLng latLng;

    private FusedLocationProviderClient fusedLocationClient;
    // OpenAI, 2024, ChatGPT, How to create a QR Code Scanner Fragment

    /**
     * Required empty public constructor
     */
    public ScannerFragment() {}

    /**
     * Interface for ScannerListener
     */
    interface ScannerListener{
        void onCheckIn(String eventID);
    }

    /**
     * Called when a fragment is first attached to its host activity
     * @param context Host activity
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ScannerListener){
            listener = (ScannerListener) context;
        }
    }

    /**
     * Called to do initial creation of a fragment
     * Initializes the scanner launcher and the request permission launcher
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getContext());


        locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                Log.d("locloc", "onCreate: fine");
                                // Precise location access granted.
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                                Log.d("locloc", "onCreate: coarse");
                            } else {
                                // No location access granted.
                            }
                        }
                );



        qrScannerLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() != null){
                Log.d("ScanQRCodeActivity", "Scanned");
                String resultContents = result.getContents();
                String[] data = resultContents.split(":");
                if (Objects.equals(data[0], "Check In")){
                    onAttendeeCheckIn(data[1]);
                } else if (Objects.equals(data[0], "Promotion")){
                    onPromotionScan(data[1]);
                } else {
                    String encryptedContent = sha256Hash(data[0]);
                    findEventToCheckIn(encryptedContent);
                }
            }
        });

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        scanQR();
        setLocation();
    }

    /**
     * Called to have the fragment instantiate its user interface view
     * Instantiate view for the scanner
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return View of the scanner
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_scanner, container, false);
        Log.d("locloc", "onCreateView: ");
        scanQR();
        // Inflate the layout for this fragment
        return v;
    }

    /**
     * Called immediately after onCreateView has returned, but before any saved state has been restored in to the view
     * Calls the checkCameraPermission function to check if permission is granted
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scanQR();
    }

    /**
     * Launches the scanner
     */
    // https://github.com/journeyapps/zxing-android-embedded/blob/master/sample/src/main/java/example/zxing/MainActivity.java
    private void scanQR(){
        ScanOptions options = new ScanOptions();
//        options.setOrientationLocked(true);
//
//        options.setPrompt("");
//        options.setCaptureActivity(CaptureAct.class);
        qrScannerLauncher.launch(options);

        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });



    }

    /**
     * Handles attendee check in
     * @param eventID ID of the event that is being checked into
     */
    private void onAttendeeCheckIn(String eventID){
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString("userID", null);

        UserDBConnector userDBConnector = new UserDBConnector();
        FirebaseFirestore userDB = userDBConnector.getDb();
        userDB.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null){
                        List<String> checkedInEvents = user.getCheckedInEvents();
                        checkedInEvents.add(eventID);
                        user.setCheckedInEvents(checkedInEvents);
                        UserDB db = new UserDB();
                        db.updateUser(user);
                    }
                }
            }
        });
        listener.onCheckIn(eventID);
        EventDBConnector eventDBConnector = new EventDBConnector();
        FirebaseFirestore eventDB = eventDBConnector.getDb();

//        setLocation();


        eventDB.collection("events").document(eventID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    Event event = documentSnapshot.toObject(Event.class);
                    if(event != null) {
                        Map<String, Integer> checkedInAttendees = event.getCheckedInAttendees();
                        int attendeeCount = event.getAttendeesCount();
                        if (checkedInAttendees.containsKey(userID)) {
                            int checkInCount = checkedInAttendees.get(userID);
                            checkInCount++;
                            checkedInAttendees.put(userID, checkInCount);
                        } else {
                            checkedInAttendees.put(userID, 1);
                            attendeeCount++;
                        }
                        event.setCheckedInAttendees(checkedInAttendees);
                        event.setAttendeesCount(attendeeCount);


                        if (latLng != null) {
                            Log.d("locloc", "s"+latLng.toString());
                            event.addCheckInLocation(userID, latLng);
                        }

                        EventDB eventDB = new EventDB(eventDBConnector);
                        eventDB.updateEvent(event);
                    }
                    Toast.makeText(getContext(), "You have successfully checked in!", Toast.LENGTH_LONG).show();
                    if (getActivity() instanceof MainActivity){
                        MainActivity mainActivity = (MainActivity) getActivity();
                        FragmentManagerHelper helper = mainActivity.getFragmentManagerHelper();
                        EventViewAttendee eventViewAttendeeFragment = new EventViewAttendee();
                        Bundle args = new Bundle();
                        args.putParcelable("event", event);
                        eventViewAttendeeFragment.setArguments(args);
                        helper.replaceFragment(eventViewAttendeeFragment);

                    }
                } else{
                    Log.d("onAttendeeCheckIn", "Document does not exist");
                }
            }
        });
    }

    /**
     * Called when the attendee scans the promotional QR
     * @param eventID ID of the event whose promotional QR is scanned
     */
    private void onPromotionScan(String eventID){
        EventDBConnector connector = new EventDBConnector();
        FirebaseFirestore db = connector.getDb();

        db.collection("events").document(eventID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (getActivity() instanceof MainActivity) {
                        MainActivity mainActivity = (MainActivity) getActivity();
                        FragmentManagerHelper helper = mainActivity.getFragmentManagerHelper();

                        Event event = documentSnapshot.toObject(Event.class);
                        EventViewAttendee eventViewAttendeeFragment = new EventViewAttendee();
                        Bundle args = new Bundle();
                        args.putParcelable("event", event);
                        eventViewAttendeeFragment.setArguments(args);

                        helper.replaceFragment(eventViewAttendeeFragment);
                    }
                }
            }
        });
    }

    /**
     * For reused QR, finds the event associated with the QR and checks the attendee into the event
     * @param encryptedContent SHA-256 encrypted content of the QR code
     */
    private void findEventToCheckIn(String encryptedContent){
        FirebaseFirestore db = new EventDBConnector().getDb();

        db.collection("events").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    String sha256Content = (String) documentSnapshot.get("sha256ReusedQRContent");
                    if (Objects.equals(encryptedContent, sha256Content)){
                        String eventID = (String) documentSnapshot.get("eventID");
                        onAttendeeCheckIn(eventID);
                    }
                }
            }
        });
    }
    // Open AI, 2024, ChatGPT, How to use SHA-256 hashing
    /**
     * Takes the content of the reused QR code and SHA-256 hashes it
     * @param input content of the QR code
     * @return encrypted QR content
     */
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

    private void setLocation() {
        CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
        if (!(ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {


            fusedLocationClient.getCurrentLocation(LocationRequest.QUALITY_BALANCED_POWER_ACCURACY, cancellationTokenSource.getToken())
                    .addOnSuccessListener(this.getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                Log.d("locloc", "inLoop");
                                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                // Logic to handle location object
                            }
                        }
                    });
            Log.d("locloc", "loc1");
        }
    }



}