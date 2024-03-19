package com.example.elevent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.UUID;
/*
    This file is responsible for being the host activity of all fragments in the app
    Outstanding issues: some listeners that are implemented through MainActivity must be refined
 */

/**
 * This is the main activity that all fragments and listeners attach to
 * Contains the navigation bar
 */
public class MainActivity extends AppCompatActivity{

    BottomNavigationView navigationView;
    AllEventsFragment allEventsFragment = new AllEventsFragment();
    MyEventsFragment myEventsFragment = new MyEventsFragment();
    ScannerFragment scannerFragment = new ScannerFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    private ActivityResultLauncher<Intent> generateQRLauncher;
    private byte[] checkinQR;
    private byte[] promotionQR;

    /**
     * Called when the activity is starting
     * Initializes the tool bar
     * Gives user a unique user ID when they open the app for the first time
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

        // Find the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        // Set the Toolbar to act as the ActionBar
        setSupportActionBar(toolbar);

        // OpenAI, 2024, ChatGPT, Generate unique user ID when opening app for first time
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);  // SharedPreferences stores a small collection of key-value pairs; maybe we can put this into the firebase???
        boolean isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true);
        if (isFirstLaunch) {
            String userID = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFirstLaunch", false);
            editor.putString("userID", userID);
            editor.apply();
        }
        generateQRLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null && result.getData().hasExtra("qrCode")) {
                    checkinQR = result.getData().getByteArrayExtra("qrCode");
                }
            }
        });

    }


    //to implement the fragment to create event fragment
    /*@Override
    public void onCreateEvent(Event event) {

    }*/

    /**
     * Implementation of the CreateEventListener
     *
     * @param event The created event
     */
    public void onPositiveClick(Event event) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String eventID = UUID.randomUUID().toString();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("eventID", eventID);
        editor.apply();
        MyEventsFragment fragment = new MyEventsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("event", (Parcelable) event);
        fragment.setArguments(bundle);
        Navigation.findNavController(this, R.id.fragmentContainerView)
                .navigate(R.id.action_mainFragment_to_myEventsFragment, bundle);
        updateAppBarTitle("My Events");

    }

    /**
     * Updates the title of the app bar at the top of the screen
     *
     * @param title Title to be updated to
     */
    public void updateAppBarTitle(String title) {
        TextView appBarTitle = findViewById(R.id.appbar_text);
        appBarTitle.setText(title);
    }


    // use this method to get the UUID give to a user at
    // first launch in the UserDB to be used as the document
    // name in the firestore database collection 'User'

    /**
     * Gets the user ID to store into the user database
     *
     * @return User ID
     */
    public String getUserIDForUserDB() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString("userID", null); // Return null or a default value if not found
    }

}