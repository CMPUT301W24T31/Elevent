package com.example.elevent;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class App extends Application {

    // Variable to hold reference to the current activity
    private static Activity mCurrentActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();

        // Registering a callback to track the activity lifecycle
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            // Called when a new activity is created
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                mCurrentActivity = activity;
            }

            // Called when an activity is started
            @Override
            public void onActivityStarted(Activity activity) {
                mCurrentActivity = activity;
            }

            // Called when an activity is resumed
            @Override
            public void onActivityResumed(Activity activity) {
                mCurrentActivity = activity;
            }

            // Other lifecycle methods not needed for this purpose are left empty

            @Override
            public void onActivityPaused(Activity activity) {
                // Not needed for this purpose
            }

            @Override
            public void onActivityStopped(Activity activity) {
                // Not needed for this purpose
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                // Not needed for this purpose
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                // Not needed for this purpose
            }
        });
    }

    // Method to get the current activity
    public static Activity getCurrentActivity() {
        return mCurrentActivity;
    }
}
