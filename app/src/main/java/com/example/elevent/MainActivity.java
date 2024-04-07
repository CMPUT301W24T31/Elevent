package com.example.elevent;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.avatarfirst.avatargenlib.AvatarConstants;
import com.avatarfirst.avatargenlib.AvatarGenerator;
import com.example.elevent.Admin.AdminHomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
/*
    This file is responsible for being the host activity of all fragments in the app
 */

/**
 * This is the main activity that all fragments and listeners attach to
 * Contains the navigation bar
 */
public class MainActivity extends AppCompatActivity implements CreatedEventFragment.CreatedEventListener, AddNotificationDialogFragment.AddNotificationDialogListener {


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
    String userID;
    private List<String> adminUserIds = Arrays.asList(
            "c297401a-6d7a-4f09-823d-626234226e16",
            "4f553d28-6261-49e3-8b15-186a89d01faf",
            "a51328ca-5ee3-4903-990b-01ef1ed2eb3e"
//            ,"45753e1e-bf94-4bd6-9dd6-cfd83fc34037"
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
        checkUserExists();

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

    /**
     * Updates the title of the app bar at the top of the screen
     * @param title Title to be updated to
     */
    public void updateAppBarTitle(String title) {
        TextView appBarTitle = findViewById(R.id.appbar_text);
        appBarTitle.setText(title);
    }

    /**
     * Handles when a notification is created
     * @param notification The notification text.
     */
    @Override
    public void onNotificationAdded(String notification) {
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

    /**
     * Check if this user exists already
     */
    private void checkUserExists() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);  // SharedPreferences stores a small collection of key-value pairs; maybe we can put this into the firebase???
        userID = sharedPreferences.getString(KEY_USER_ID, null);
        if (userID == null) {
            createUser();
        } else {
            FirebaseFirestore db = new UserDBConnector().getDb();
            db.collection("users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (!documentSnapshot.exists()){
                            createUser();
                        }
                    }
                }
            });
        }
    }
    private void createUser(){
        userID = UUID.randomUUID().toString();
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID, userID);
        editor.apply();

        // TODO: do we make name mandatory to implement this?
        // https://github.com/AmosKorir/AvatarImageGenerator?tab=readme-ov-file
        BitmapDrawable generatedPFP = AvatarGenerator.Companion.avatarImage(
                this,
                200,
                AvatarConstants.Companion.getRECTANGLE(),
                String.valueOf(userID.charAt(0))
        );
        Bitmap generatedPFPBitmap = generatedPFP.getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        generatedPFPBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] generatedPFPBA = outputStream.toByteArray();
        Blob generatedPFPBlob = Blob.fromBytes(generatedPFPBA);

        User newUser = new User(userID, generatedPFPBlob);

        UserDBConnector connector = new UserDBConnector();
        UserDB userDB = new UserDB(connector);
        userDB.addUser(newUser);
    }

    /**
     * Implements updateEvent in interface CreatedEventListener
     * @param event Event to be updated
     */
    @Override
    public void updateEvent(Event event) {
        MyEventsFragment myEventsFragment = (MyEventsFragment) getSupportFragmentManager().findFragmentByTag("MY_EVENTS_FRAGMENT_TAG");
        if (myEventsFragment != null) {
            myEventsFragment.updateEvent(event);
        }
    }

    /**
     * Convert a byte[] to a specific object
     * @param eventBA byte[] to be converted
     * @return The resulting object
     */
    private Object convertByteArrayToObject(byte[] eventBA){
        InputStream inputStream = new ByteArrayInputStream(eventBA);
        try (ObjectInputStream in = new ObjectInputStream(inputStream)){
            return in.readObject();
        } catch (Exception e){
            e.printStackTrace();
        }
        throw new RuntimeException();
    }

    /**
     * Handles a notification's intent
     * @param intent The intent to be handled
     */
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