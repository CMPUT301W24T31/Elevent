package com.example.elevent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;


import android.content.Intent;
import android.graphics.Bitmap;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements AllEventsFragment.OnEventClickListener, CreateEventFragment.CreateEventListener, CreatedEventFragment.CreatedEventListener{

    private FragmentManagerHelper fragmentManagerHelper;
    BottomNavigationView navigationView;


    AllEventsFragment allEventsFragment = new AllEventsFragment();
    MyEventsFragment myEventsFragment = new MyEventsFragment();
    ScannerFragment scannerFragment = new ScannerFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    private ActivityResultLauncher<Intent> generateQRLauncher;
    private Bitmap checkinQR;
    private Bitmap promotionQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManagerHelper = new FragmentManagerHelper(getSupportFragmentManager(), R.id.activity_main_framelayout);

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
        generateQRLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null && result.getData().hasExtra("qrCode")) {
                    checkinQR = result.getData().getParcelableExtra("qrCode");
                }
            }
        });

        initNavView();
        Log.d("DEBUG", "test");

    }

    public FragmentManagerHelper getFragmentManagerHelper() {
        return fragmentManagerHelper;
    }

    // Implement the interface method


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
                    getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_framelayout, myEventsFragment, "MY_EVENTS_FRAGMENT_TAG").commit();
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

    //to implement the fragment to create event fragment
    /*@Override
    public void onCreateEvent(Event event) {

    }*/

    public void onPositiveClick(Event event) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String eventID = UUID.randomUUID().toString();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("eventID", eventID);
        editor.apply();
        MyEventsFragment fragment = (MyEventsFragment) getSupportFragmentManager().findFragmentByTag("MY_EVENTS_FRAGMENT_TAG");
        if (fragment != null){
            fragment.addEvent(event);
        }
    }
    public void updateAppBarTitle(String title) {
        TextView appBarTitle = findViewById(R.id.appbar_text);
        appBarTitle.setText(title);
    }


    // use this method to get the UUID give to a user at
    // first launch in the UserDB to be used as the document
    // name in the firestore database collection 'User'
    public String getUserIDForUserDB() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString("userID", null); // Return null or a default value if not found
    }


    @Override
    public void onEventClicked(Event event) {
        updateAppBarTitle(event.getEventName());
    }

}