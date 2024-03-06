package com.example.elevent;

import com.google.firebase.firestore.FirebaseFirestore;

public class UserDBConnector {

    private FirebaseFirestore db;

    public UserDBConnector() {
        // Initialize firestore database
        db = FirebaseFirestore.getInstance();
    }

    public FirebaseFirestore getDb() {
        return db;
    }

}
