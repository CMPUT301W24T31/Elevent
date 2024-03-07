package com.example.elevent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements CreateEventFragment.CreateEventDialogListener {

    BottomNavigationView navigationView;

    AllEventsFragment allEventsFragment = new AllEventsFragment();
    MyEventsFragment myEventsFragment = new MyEventsFragment();
    ScannerFragment scannerFragment = new ScannerFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    private ActivityResultLauncher<Intent> generateQRLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // OpenAI, 2024, ChatGPT, Generate unique user ID when opening app for first time
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);  // SharedPreferences stores a small collection of key-value pairs; maybe we can put this into the firebase???
        boolean isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true);
        if (isFirstLaunch){
            String userID = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFirstLaunch", false);
            editor.putString("userID", userID);
            editor.apply();
        }

        initNavView();
        Log.d("DEBUG", "test");


//        Button scanTest = findViewById(R.id.scan_test);
//        scanTest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, ScanQRCodeActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        // these buttons are just for testing if the scanner and generator work
////        Button genTest = findViewById(R.id.gen_test);
//        genTest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, GenerateQRCodeActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    private void initNavView() {
        navigationView = findViewById(R.id.activity_main_navigation_bar);

        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_framelayout,allEventsFragment).commit();
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.nav_bar_allevents) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_framelayout, allEventsFragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.nav_bar_myevents) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_framelayout, myEventsFragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.nav_bar_scanner) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_framelayout, scannerFragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.nav_bar_profile) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_framelayout, profileFragment).commit();
                        return true;
                }

                return false;
            }
        });

    }

    public void onPositiveClick(Event event){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String eventID = UUID.randomUUID().toString();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("eventID", eventID);
        editor.apply();
        generateQRLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
            if (result.getResultCode() == RESULT_OK){
                if (result.getData() != null && result.getData().hasExtra("qrCode")) {
                    Bitmap qrCode = result.getData().getParcelableExtra("qrCode");
                    event.setCheckinQR(qrCode);  // TODO: figure out how to create two QR codes; one for check in and the other to display event info
                }
            }
        });
        Bundle args = new Bundle();
        args.putSerializable("event", event);
        MyEventsFragment fragment = new MyEventsFragment();
        fragment.setArguments(args);
    }

}