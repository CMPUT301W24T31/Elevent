package com.example.elevent;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.example.elevent.Admin.AdminHomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
    private static final String CHANNEL_ID = "EleventChannel";
    private List<String> adminUserIds = Arrays.asList(
            "b5334c2f-4faf-441b-9151-3de5ce92339b",
            "da58ae40-1501-410d-9d27-a87e2c81c445",
            "0b046a02-4a9d-4727-9522-ec3223b48e21",
            "c297401a-6d7a-4f09-823d-626234226e16"
    );

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
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString(KEY_USER_ID, null);
        if (userID == null) {
            userID = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_USER_ID, userID);
            editor.apply();

            // Assuming creation of a new user is necessary if no ID is found
            User newUser = new User(userID);
            UserDBConnector connector = new UserDBConnector();
            UserDB userDB = new UserDB(connector);
            userDB.addUser(newUser);
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fragmentManagerHelper = new FragmentManagerHelper(getSupportFragmentManager(), R.id.activity_main_framelayout);
        navigationView = findViewById(R.id.activity_main_navigation_bar);

        // Check if the user ID belongs to an admin
        if (adminUserIds.contains(userID)) {
            // Admin user logic
            navigationView.setVisibility(View.GONE);
            fragmentManagerHelper.replaceFragment(new AdminHomeFragment());
        } else {
            // Regular user logic
            navigationView.setVisibility(View.VISIBLE);
            createNotificationChannel();
            initNavView();
        }

        handleIntent(getIntent());
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
    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Event Announcements";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void createUser() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);  // SharedPreferences stores a small collection of key-value pairs; maybe we can put this into the firebase???
        String userID = sharedPreferences.getString(KEY_USER_ID, null);
        if (userID == null) {
            userID = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_USER_ID, userID);
            editor.apply();
            User newUser = new User(userID);

            UserDBConnector connector = new UserDBConnector();
            UserDB userDB = new UserDB(connector);
            userDB.addUser(newUser);
        }
    }

    @Override
    public void updateEvent(Event event) {
        MyEventsFragment myEventsFragment = (MyEventsFragment) getSupportFragmentManager().findFragmentByTag("MY_EVENTS_FRAGMENT_TAG");
        if (myEventsFragment != null) {
            myEventsFragment.updateEvent(event);
        }
    }

    private Object convertByteArrayToObject(byte[] eventBA){
        InputStream inputStream = new ByteArrayInputStream(eventBA);
        try (ObjectInputStream in = new ObjectInputStream(inputStream)){
            return in.readObject();
        } catch (Exception e){
            e.printStackTrace();
        }
        throw new RuntimeException();
    }
    private void handleIntent(Intent intent) {
        if (intent.hasExtra("OpenNotificationFromFragment")) {
            if (Objects.equals(intent.getStringExtra("OpenNotificationFromFragment"), "NotificationFragmentAttendee")) {
                NotificationFragmentAttendee notificationFragmentAttendee = new NotificationFragmentAttendee();
                byte[] eventToOpenBA = intent.getByteArrayExtra("eventByteArray");
                Event eventToOpen = (Event) convertByteArrayToObject(eventToOpenBA);
                Bundle args = new Bundle();
                args.putSerializable("event", eventToOpen);
                notificationFragmentAttendee.setArguments(args);
                fragmentManagerHelper.replaceFragment(notificationFragmentAttendee);
            }
        }
    }
}