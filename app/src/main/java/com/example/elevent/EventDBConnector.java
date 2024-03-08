package com.example.elevent;

import com.google.firebase.firestore.FirebaseFirestore;


public class EventDBConnector {

    private FirebaseFirestore db;

    public EventDBConnector() {
        // Initialize firestore database and storage
        db = FirebaseFirestore.getInstance();
    }

    public FirebaseFirestore getDb() {
        return db;
    }
}
