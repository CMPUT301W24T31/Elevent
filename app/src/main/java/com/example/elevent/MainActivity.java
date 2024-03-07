package com.example.elevent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView navigationView;

    AllEventsFragment allEventsFragment = new AllEventsFragment();
    MyEventsFragment myEventsFragment = new MyEventsFragment();
    ScannerFragment scannerFragment = new ScannerFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Find the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        // Set the Toolbar to act as the ActionBar
        setSupportActionBar(toolbar);
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
        updateAppBarTitle(getString(R.string.all_events_title));
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.nav_bar_allevents) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_framelayout, allEventsFragment).commit();
                    updateAppBarTitle(getString(R.string.all_events_title));
                    return true;
                } else if (item.getItemId() == R.id.nav_bar_myevents) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_framelayout, myEventsFragment).commit();
                    updateAppBarTitle(getString(R.string.my_events_title));
                    return true;
                } else if (item.getItemId() == R.id.nav_bar_scanner) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_framelayout, scannerFragment).commit();
                    updateAppBarTitle(getString(R.string.scanner_title));
                    return true;
                } else if (item.getItemId() == R.id.nav_bar_profile) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_framelayout, profileFragment).commit();
                    updateAppBarTitle(getString(R.string.profile_title));
                    return true;
                }

                return false;
            }
        });

    }
    public void updateAppBarTitle(String title) {
        TextView appBarTitle = findViewById(R.id.appbar_text);
        appBarTitle.setText(title);
    }




}