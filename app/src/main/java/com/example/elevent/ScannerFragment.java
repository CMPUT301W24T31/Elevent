package com.example.elevent;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
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
    // OpenAI, 2024, ChatGPT, How to create a QR Code Scanner Fragment
    private ActivityResultLauncher<String> requestPermissionLauncher;

    /**
     * Required empty public constructor
     */
    public ScannerFragment(){

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
        qrScannerLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() == null) {
                Intent originalIntent = result.getOriginalIntent();
                if (originalIntent == null) {
                    Log.d("ScanQRCodeActivity", "Cancelled scan");
                } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                    Log.d("ScanQRCodeActivity", "Cancelled scan due to missing camera permission");
                    Toast.makeText(getContext(), "Camera permission required", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d("ScanQRCodeActivity", "Scanned");
                Toast.makeText(getContext(), "Scanned", Toast.LENGTH_SHORT).show();  // placeholder; TODO: replace this with the actual content of the QR code
            }
        });
         requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted){
                scanQR();
            }
        });
        requestPermissionLauncher.launch(Manifest.permission.CAMERA);  // OpenAI, 2024, ChatGPT, How to create a QR Code Scanner Fragment
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
        checkCameraPermission();
    }

    /**
     * Check that the user has given permission to use the camera
     * If so, open the scanner
     */
    private void checkCameraPermission(){
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            scanQR();
        }
    }

    /**
     * Launches the scanner
     */
    // https://github.com/journeyapps/zxing-android-embedded/blob/master/sample/src/main/java/example/zxing/MainActivity.java
    private void scanQR(){
        ScanOptions options = new ScanOptions();
        options.setOrientationLocked(true);
        options.setPrompt("");
        qrScannerLauncher.launch(options);
    }

}