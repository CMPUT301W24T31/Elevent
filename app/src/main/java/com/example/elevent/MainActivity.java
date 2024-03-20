package com.example.elevent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;

import androidx.core.splashscreen.SplashScreen;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.UUID;
/**
 * MainActivity serves as the primary activity that hosts all fragments
 * in the application. It contains the bottom navigation bar for navigating
 * between the fragments and manages the AppBar title updates based on the
 * current screen.
 */
public class MainActivity extends AppCompatActivity {

    // Tag for logging
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Initialize the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize BottomNavigationView and NavController
        BottomNavigationView navView = findViewById(R.id.activity_main_navigation_bar); // Correct ID reference
        NavController navController = Navigation.findNavController(this, R.id.fragmentContainerView); // Correct ID reference

        // Setup AppBarConfiguration with top-level destinations
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.allEventsFragment, R.id.myEventsFragment, R.id.scannerFragment, R.id.profileFragment)
                .build();

        // Link the NavController with the AppBar
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Link the BottomNavigationView with the NavController
        NavigationUI.setupWithNavController(navView, navController);

        // Generate a unique user ID on first launch
        generateUniqueUserID();
    }

    /**
     * Generates a unique user ID and stores it in SharedPreferences
     * if it's the user's first launch of the app.
     */
    private void generateUniqueUserID() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true);

        if (isFirstLaunch) {
            String userID = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFirstLaunch", false);
            editor.putString("userID", userID);
            editor.apply();

            Log.i(TAG, "Generated unique user ID on first launch: " + userID);
        }
    }

    /**
     * Overrides onSupportNavigateUp to handle Up navigation with NavController.
     *
     * @return boolean indicating whether Up navigation was successfully handled.
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.fragmentContainerView);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    /**
     * Updates the title of the AppBar based on the current screen.
     *
     * @param title The new title to set for the AppBar.
     */
    public void updateAppBarTitle(String title) {
        TextView appBarTitle = findViewById(R.id.appbar_text);
        appBarTitle.setText(title);
    }
}