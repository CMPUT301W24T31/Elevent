/*package com.example.elevent;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.Map;
import java.util.Objects;


public class ScanQRCodeActivity extends AppCompatActivity {
    //private ActivityResultLauncher<ScanOptions> qrScannerLauncher;
    // OpenAI, 2024, ChatGPT, How to create a QR Code Scanner Fragment
    //private ActivityResultLauncher<String> requestPermissionLauncher;
    //  https://github.com/journeyapps/zxing-android-embedded/blob/master/sample/src/main/java/example/zxing/MainActivity.java

    // OpenAI, 2024, ChatGPT, How to create a QR code scanner
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_qr_layout);
        ActivityResultLauncher<ScanOptions> qrScannerLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() != null) {
                Log.d("ScanQRCodeActivity", "Scanned");
                String resultContents = result.getContents();
                String[] data = resultContents.split(",");
                if (Objects.equals(data[0], "Check In")) {
                    onAttendeeCheckIn(data[1]);
                } else if (Objects.equals(data[0], "Promotion")) {
                    onPromotionScan(data[1]);
                }
            }
        });
        ScanOptions options = new ScanOptions();
        options.setOrientationLocked(true);
        options.setPrompt("");
        options.setCaptureActivity(CaptureAct.class);
        qrScannerLauncher.launch(options);
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                scanQR();
            }
        });
        requestPermissionLauncher.launch(Manifest.permission.CAMERA);  // OpenAI, 2024, ChatGPT, How to create a QR Code Scanner Fragment
    }

    private void scanQR() {
        ScanOptions options = new ScanOptions();
        options.setOrientationLocked(true);
        options.setPrompt("");
        options.setCaptureActivity(CaptureAct.class);
        qrScannerLauncher.launch(options);  // right now, only launches scanner in landscape (do we want to try to make it only portrait?)
    }

    private void onAttendeeCheckIn(String eventName) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString("userID", null);
        EventDBConnector eventDBConnector = new EventDBConnector();
        FirebaseFirestore db = eventDBConnector.getDb();

        db.collection("events").document(eventName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Integer> checkedInAttendees = (Map<String, Integer>) documentSnapshot.get("checkedInAttendees");
                    if (checkedInAttendees != null) {
                        if (checkedInAttendees.containsKey(userID)) {
                            int checkInCount = checkedInAttendees.get(userID);
                            checkInCount++;
                            checkedInAttendees.put(userID, checkInCount);
                        } else {
                            checkedInAttendees.put(userID, 0);
                        }
                    }
                } else {
                    Log.d("onAttendeeCheckIn", "Document does not exist");
                }
            }
        });
        Toast.makeText(this, "You have successfully checked in!", Toast.LENGTH_SHORT).show();
    }

    private void onPromotionScan(String eventName) {
        EventDBConnector connector = new EventDBConnector();
        FirebaseFirestore db = connector.getDb();

        db.collection("events").document(eventName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Event event = documentSnapshot.toObject(Event.class);
                    EventViewAttendee eventViewAttendeeFragment = new EventViewAttendee();
                    Bundle args = new Bundle();
                    args.putSerializable("event", event);
                    eventViewAttendeeFragment.setArguments(args);

                    getSupportFragmentManager().beginTransaction().replace(R.id.scan_qr_frame_layout, eventViewAttendeeFragment).commit();
                }
            }
        });
    }
}
*/