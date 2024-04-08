package com.example.elevent;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.FusedOrientationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.journeyapps.barcodescanner.camera.CameraSettings;

import org.checkerframework.checker.units.qual.C;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
/*
    This file contains the implementation of the ScannerFragment that is responsible for
    requesting for camera permission and opening the camera to scan QR codes.
    Outstanding issues: need to finish implementing the decoding
 */

/**
 * This fragment request permission to open the camera and scans the QR code
 */
public class ScannerFragment extends Fragment {

    // https://github.com/journeyapps/zxing-android-embedded/blob/master/sample/src/main/java/example/zxing/MainActivity.java
    private ActivityResultLauncher<ScanOptions> qrScannerLauncher;
    private ActivityResultLauncher<String[]> locationPermissionRequest;
    private LatLng latLng;

    private FusedLocationProviderClient fusedLocationClient;
    // OpenAI, 2024, ChatGPT, How to create a QR Code Scanner Fragment

    /**
     * Required empty public constructor
     */
    public ScannerFragment() {

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
                String[] data = resultContents.split(",");
                if (Objects.equals(data[0], "Check In")){
                    onAttendeeCheckIn(data[1]);
                } else if (Objects.equals(data[0], "Promotion")){
                    onPromotionScan(data[1]);
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scanner, container, false);
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
    }

    /**
     * Launches the scanner
     */
    // https://github.com/journeyapps/zxing-android-embedded/blob/master/sample/src/main/java/example/zxing/MainActivity.java
    private void scanQR(){
        ScanOptions options = new ScanOptions();
        options.setOrientationLocked(true);
        options.setPrompt("");
        options.setCaptureActivity(CaptureAct.class);
        qrScannerLauncher.launch(options);

        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });



    }

    private void onAttendeeCheckIn(String eventID){
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString("userID", null);
        EventDBConnector eventDBConnector = new EventDBConnector();
        FirebaseFirestore db = eventDBConnector.getDb();

//        setLocation();


        db.collection("events").document(eventID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                        /*
                              eventDB.updateEvent(eventName, updates);
                              required: Event
                              found:    String,Map<String,Object>
                              reason: actual and formal argument lists differ in length
                              so i tried using gpt's suggestion

                        Event updatedEvent = new Event(eventName, updates);
                        // Pass the updated Event object to the updateEvent method
                        eventDB.updateEvent(updatedEvent);*/
                    }
                    Toast.makeText(getContext(), "You have successfully checked in!", Toast.LENGTH_LONG).show();
                    if (getActivity() instanceof MainActivity){
                        MainActivity mainActivity = (MainActivity) getActivity();
                        FragmentManagerHelper helper = mainActivity.getFragmentManagerHelper();
                        EventViewAttendee eventViewAttendeeFragment = new EventViewAttendee();
                        Bundle args = new Bundle();
                        args.putSerializable("event", event);
                        eventViewAttendeeFragment.setArguments(args);
                        helper.replaceFragment(eventViewAttendeeFragment);

                    }
                } else{
                    Log.d("onAttendeeCheckIn", "Document does not exist");
                }
            }
        });
    }
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
                        args.putSerializable("event", event);
                        eventViewAttendeeFragment.setArguments(args);

                        helper.replaceFragment(eventViewAttendeeFragment);
                    }
                }
            }
        });
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