package com.example.elevent;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;

public class UserDBConnector {

    // initialization of the Firebase App will be done here
    static {
        try {
            // Initialize Firebase App with options
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setDatabaseUrl("https://elevent-fb693-default-rtdb.firebaseio.com/")
                    .build();

            // initialize FirebaseApp Here:
            //


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DatabaseReference DBConnector() {
        return FirebaseDatabase.getInstance().getReference();
    }
}
