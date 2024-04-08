package com.example.elevent;

import com.google.firebase.firestore.FirebaseFirestore;
/*
    This file is responsible for connecting the user database to the app
 */
/**
 * This class connects the user database to the app
 */
public class UserDBConnector {

    private final FirebaseFirestore db;

    /**
     * Connects the database to the app
     */
    public UserDBConnector() {
        // Initialize firestore database
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Gets the user database
     * @return User database
     */
    public FirebaseFirestore getDb() {
        return db;
    }

}
