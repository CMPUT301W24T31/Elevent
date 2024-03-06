package com.example.elevent;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;

public class UserDBConnector {
    private DatabaseReference rootDB;

    public UserDBConnector() {
        // Initialize the rootDB variable to point to the "Users" child node in your Firebase database
        rootDB = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    public DatabaseReference getRootDB() {
        return rootDB;
    }
}
