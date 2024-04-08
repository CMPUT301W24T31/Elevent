package com.example.elevent;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import com.avatarfirst.avatargenlib.AvatarConstants;
import com.avatarfirst.avatargenlib.AvatarGenerator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/*
    This file is responsible for being the host activity of all fragments in the app
 */

/**
 * This is the main activity that all fragments and listeners attach to
 * Contains the navigation bar
 */
public class MainActivity extends AppCompatActivity implements CreateEventFragment.CreateEventListener, CreatedEventFragment.CreatedEventListener, AddNotificationDialogFragment.AddNotificationDialogListener, ManageEventFragment.ManageEventListener, NotificationCentreFragment.NotificationCentreDialogListener, EventSignUpDialogFragment.EventSignUpListener, ScannerFragment.ScannerListener {


    private FragmentManagerHelper fragmentManagerHelper;
    BottomNavigationView navigationView;
    AllEventsFragment allEventsFragment = new AllEventsFragment();
    MyEventsFragment myEventsFragment = new MyEventsFragment();
    ScannerFragment scannerFragment = new ScannerFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_USER_ID = "userID";
    private static final String CHANNEL_ID = "EleventChannel";
    private ActivityResultLauncher<String> requestPermissionLauncher;
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
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
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
        createNotificationChannel();
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (!isGranted){
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
            }
        });

        // Check if the user ID belongs to an admin
        if (adminUserIds.contains(userID)) {
            // Admin user logic
            navigationView.setVisibility(View.GONE);
            fragmentManagerHelper.replaceFragment(new AdminHomeFragment());
        } else {
            // Regular user logic
            navigationView.setVisibility(View.VISIBLE);
            initNavView();
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
        handleIntent(getIntent());
        setEventAnnouncementListener();
        setMilestoneListener();
    }

    /**
     * Gets a fragment manager helper
     *
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

        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_framelayout, allEventsFragment).commit();
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
     *
     * @param title Title to be updated to
     */
    public void updateAppBarTitle(String title) {
        TextView appBarTitle = findViewById(R.id.appbar_text);
        appBarTitle.setText(title);
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (!documentSnapshot.exists()) {
                            createUser();
                        }
                    }
                }
            });
        }
    }

    /**
     * Create a new user and store it in the database
     */
    private void createUser() {
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
                "Elevent"
        );
        Bitmap generatedPFPBitmap = generatedPFP.getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        generatedPFPBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] generatedPFPBA = outputStream.toByteArray();
        Blob generatedPFPBlob = Blob.fromBytes(generatedPFPBA);

        User newUser = new User(userID, generatedPFPBlob, true);

        UserDBConnector connector = new UserDBConnector();
        UserDB userDB = new UserDB(connector);
        userDB.addUser(newUser);
    }

    /**
     * Implements updateEvent in interface CreatedEventListener
     *
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
     * Handles a notification's intent
     *
     * @param intent The intent to be handled
     */
    private void handleIntent(Intent intent) {
        if (intent != null) {
            if (intent.hasExtra("eventIDToOpen")) {
                String eventIDToOpen = intent.getStringExtra("eventIDToOpen");

                FirebaseFirestore db = new EventDBConnector().getDb();
                db.collection("events").document(eventIDToOpen).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Event eventToOpen = documentSnapshot.toObject(Event.class);
                            Bundle args = new Bundle();
                            args.putSerializable("event", eventToOpen);
                            if (Objects.equals(intent.getStringExtra("FragmentToOpen"), "ManageEventFragment")) {
                                ManageEventFragment manageEventFragment = new ManageEventFragment();
                                manageEventFragment.setArguments(args);
                                fragmentManagerHelper.replaceFragment(manageEventFragment);
                            } else if (Objects.equals(intent.getStringExtra("FragmentToOpen"), "NotificationFragmentAttendee")){
                                NotificationFragmentAttendee notificationFragmentAttendee = new NotificationFragmentAttendee();
                                notificationFragmentAttendee.setArguments(args);
                                fragmentManagerHelper.replaceFragment(notificationFragmentAttendee);
                            }
                        }
                    }
                });
            }
        }
    }

    private void setEventAnnouncementListener() {
        FirebaseFirestore userDB = new UserDBConnector().getDb();
        FirebaseFirestore eventDB = new EventDBConnector().getDb();
        userDB.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    ArrayList<String> signedUpEvents = (ArrayList<String>) documentSnapshot.get("signedUpEvents");
                    ArrayList<String> checkedInEvents = (ArrayList<String>) documentSnapshot.get("checkedInEvents");
                    if (signedUpEvents != null && checkedInEvents != null){
                        for (String eventID : checkedInEvents){
                            if (!signedUpEvents.contains(eventID)){
                                signedUpEvents.add(eventID);
                            }
                        }
                        for (String eventID : signedUpEvents) {
                            eventDB.collection("events").document(eventID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if (error != null) {
                                        Log.d("NotificationSnapshotListener", error.toString());
                                    }
                                    if (value != null && value.exists()) {
                                        Event event = value.toObject(Event.class);
                                        if (event != null) {
                                            ArrayList<String> notifications = (ArrayList<String>) event.getNotifications();
                                            if (user != null && !notifications.isEmpty()){
                                                String recentNotification = notifications.get(notifications.size() - 1);
                                                List<String> receivedNotifications = user.getReceivedNotifications();
                                                    if (!receivedNotifications.contains(recentNotification)) {
                                                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(MainActivity.this);
                                                        stackBuilder.addNextIntentWithParentStack(intent);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        intent.putExtra("eventIDToOpen", event.getEventID());
                                                        intent.putExtra("FragmentToOpen", "NotificationFragmentAttendee");
                                                        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "EleventChannel")
                                                                .setSmallIcon(R.drawable.default_profile_pic)
                                                                .setContentTitle(event.getEventName())
                                                                .setContentText(recentNotification)
                                                                .setStyle(new NotificationCompat.BigTextStyle().bigText(recentNotification))
                                                                .setAutoCancel(true)
                                                                .setContentIntent(pendingIntent);

                                                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                                                        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                                            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
                                                            return;
                                                        }
                                                        notificationManagerCompat.notify(1, builder.build());
                                                        receivedNotifications.add(recentNotification);
                                                        user.setReceivedNotifications(receivedNotifications);
                                                        UserDB db = new UserDB();
                                                        db.updateUser(user);
                                                    }
                                                }
                                            }
                                        }
                                    }
                            });
                        }
                    }
                }
            }
        });
    }
    private void setMilestoneListener(){
        FirebaseFirestore db = new EventDBConnector().getDb();
        db.collection("events").whereEqualTo("organizerID", userID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                db.collection("events").document(documentSnapshot.getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null){
                                Log.w("MilestoneSnapshotListener", "Listen Failed", error);
                                return;
                            }
                            if (value != null && value.exists()) {
                                Event event = value.toObject(Event.class);
                                if (event != null) {
                                    if (event.getAttendeesCount() != event.getPreviousAttendeesCount()) {
                                        if (event.getMilestone() != 0 && event.getAttendeesCount() != 0 && event.getAttendeesCount() % event.getMilestone() == 0) {
                                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(MainActivity.this);
                                            stackBuilder.addNextIntentWithParentStack(intent);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            intent.putExtra("eventIDToOpen", event.getEventID());
                                            intent.putExtra("FragmentToOpen", "ManageEventFragment");
                                            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "EleventChannel")
                                                    .setSmallIcon(R.drawable.default_profile_pic)
                                                    .setContentTitle((String) value.get("eventName"))
                                                    .setContentText(String.format("Your event has %d checked in attendees!", event.getAttendeesCount()))
                                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(String.format("Your event has %d checked in attendees!", event.getAttendeesCount())))
                                                    .setAutoCancel(true)
                                                    .setContentIntent(pendingIntent);

                                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                                            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
                                                return;
                                            }
                                            notificationManagerCompat.notify(1, builder.build());
                                        }
                                        event.setPreviousAttendeesCount(event.getAttendeesCount());
                                        EventDB db = new EventDB();
                                        db.updateEvent(event);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void createNewEvent() {
        setMilestoneListener();
    }

    @Override
    public void onSignUp() {
        setEventAnnouncementListener();
    }

    @Override
    public void onCheckIn() { setEventAnnouncementListener();}
}