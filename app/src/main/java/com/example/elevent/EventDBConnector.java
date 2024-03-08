package com.example.elevent;

import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.storage.FirebaseStorage;
/*
    This file is responsible for connecting the Event Database to the app
    Outstanding issues: n/a
 */

/**
 * This class connects the event database to the app
 */
public class EventDBConnector {

    private FirebaseFirestore db;

    /**
     * Class constructor
     */
    public EventDBConnector() {
        // Initialize firestore database and storage
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Get the event database
     * @return Event database
     */
    public FirebaseFirestore getDb() {
        return db;
    }
}
