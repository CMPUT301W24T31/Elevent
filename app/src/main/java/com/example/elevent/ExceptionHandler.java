package com.example.elevent;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

public class ExceptionHandler extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Set a custom uncaught exception handler to handle any uncaught exceptions in the app
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                // Show the error fragment
                showErrorFragment();

                // Delay termination to allow the error fragment to be shown
                new Transaction.Handler() {
                    public void postDelayed(Runnable runnable, int i) {
                    }

                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        return null;
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                    }
                }.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Kill the app after a delay
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                }, 2000); // Adjust the delay time as needed
            }
        });
    }

    // Method to show the error fragment
    private void showErrorFragment() {
        // Get the current activity
        Activity activity = App.getCurrentActivity();
        // Check if the current activity is a FragmentActivity
        if (activity instanceof FragmentActivity) {
            // If it is, get the FragmentManager
            FragmentManager fragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();
            // Begin a fragment transaction
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            // Replace the content view with the ErrorFragment
            transaction.replace(android.R.id.content, new ErrorFragment());
            // Add the transaction to the back stack
            transaction.addToBackStack(null);
            // Commit the transaction
            transaction.commitAllowingStateLoss();
        }
    }
}
