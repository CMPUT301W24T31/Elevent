package com.example.elevent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.UUID;
/*
    This file is responsible for being the host activity of all fragments in the app
    Outstanding issues: some listeners that are implemented through MainActivity must be refined
 */

/**
 * This is the main activity that all fragments and listeners attach to
 * Contains the navigation bar
 */
public class MainActivity extends AppCompatActivity implements AllEventsFragment.OnEventClickListener, CreateEventFragment.CreateEventListener, CreatedEventFragment.CreatedEventListener, ManageEventFragment.ManageEventListener, NotificationCentreFragment.NotificationCentreDialogListener, AddNotificationDialogFragment.AddNotificationDialogListener {


    private FragmentManagerHelper fragmentManagerHelper;
    BottomNavigationView navigationView;
    AllEventsFragment allEventsFragment = new AllEventsFragment();
    MyEventsFragment myEventsFragment = new MyEventsFragment();
    ScannerFragment scannerFragment = new ScannerFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    private ActivityResultLauncher<Intent> generateQRLauncher;
    private byte[] checkinQR;
    private byte[] promotionQR;
    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_USER_ID = "userID";

    /**
     * Called when the activity is starting
     * Initializes the tool bar
     * Gives user a unique user ID when they open the app for the first time
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
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
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);  // SharedPreferences stores a small collection of key-value pairs; maybe we can put this into the firebase???
        String userID = sharedPreferences.getString(KEY_USER_ID, null);
        if (userID == null){
            userID = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(PREF_NAME, userID);
            editor.apply();
        }
        generateQRLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null && result.getData().hasExtra("qrCode")) {
                    checkinQR = result.getData().getByteArrayExtra("qrCode");
                }
            }
        });

        initNavView();
        Log.d("DEBUG", "test");

    }

    /**
     * Gets a fragment manager helper
     * @return FragmentManagerHelper object
     */
    public FragmentManagerHelper getFragmentManagerHelper() {
        return fragmentManagerHelper;
    }

    // Implement the interface method

    /**
     * Initializes the navigation bar
     */
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

    /**
     * Implementation of the CreateEventListener
     * @param event The created event
     */
    public void onPositiveClick(Event event) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String eventID = UUID.randomUUID().toString();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("eventID", eventID);
        editor.apply();
        MyEventsFragment fragment = (MyEventsFragment) getSupportFragmentManager().findFragmentByTag("MY_EVENTS_FRAGMENT_TAG");
        if (fragment != null){
            fragment.addEvent(event);
            fragmentManagerHelper.replaceFragment(fragment);
            updateAppBarTitle("My Events");
        }
    }

    /**
     * Updates the title of the app bar at the top of the screen
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
     * @return User ID
     */
    public String getUserIDForUserDB() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("userID", null); // Return null or a default value if not found
    }

    /**
     * Handles when a notification is created
     * @param notification The notification text.
     */
    @Override
    public void onNotificationAdded(String notification) {
    }

    /**
     * Implements onEventClickListener
     * @param event the event that has been clicked on
     */
    @Override
    public void onEventClicked(Event event) {
        EventViewAttendee eventViewAttendeeFragment = new EventViewAttendee();
        Bundle args = new Bundle();
        args.putSerializable("event", event); // Ensure Event class implements Serializable
        eventViewAttendeeFragment.setArguments(args);

        fragmentManagerHelper.replaceFragment(eventViewAttendeeFragment);
        updateAppBarTitle(event.getEventName()); // This will set the app bar title as soon as the event is clicked
    }
}