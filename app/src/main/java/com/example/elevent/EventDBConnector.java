package com.example.elevent;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class EventDBConnector {

    private FirebaseFirestore db;
    private FirebaseStorage storage;

    public EventDBConnector() {
        // Initialize firestore database and storage
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public FirebaseStorage getStorage() { return storage; }
}
