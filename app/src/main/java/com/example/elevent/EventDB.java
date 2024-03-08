package com.example.elevent;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class EventDB {

    // initialize an instance of the firestore database
    private FirebaseFirestore db;

    public EventDB(EventDBConnector connector) {
        this.db = connector.getDb();
    }

    public CompletableFuture<Void> addEvent(Event event) {

        // a map of all the event information to be added into a document
        // on firestore
        Map<String, Object> eventMap = event.toMap();

        // asynchronously add the event to Firestore and name the document
        // the name of the event
        return CompletableFuture.runAsync(() -> {
            db.collection("events").document(event.getEventName()).set(eventMap);
        });
    }

    // updateEvent takes in the eventName( which is used as the document name for an event in the
    // database) and then a map of the updates to be made to the event, creating a reference to where
    // the event info is stored, locating it, and updating the info
    // args: takes in the document name (the event name) and a dictionary of updates to be made to the
    // document
    public CompletableFuture<Void> updateEvent(String eventName, Map<String, Object> updates) {

        DocumentReference eventRef = db.collection("events").document(eventName);

        // asynchronously update the event document in firestore
        return CompletableFuture.runAsync(() -> eventRef.update(updates));
    }


    // delete an event by taking in the eventName, and thus its
    // document name in firestore and deleting it asynchronously (
    // returning the delete and then firestore removes it in the back
    // end)
    public CompletableFuture<Void> deleteEvent(String eventName) {
        DocumentReference eventRef = db.collection("events").document(eventName); // Reference to the event document

        // after getting a reference of the document, delete the document
        return CompletableFuture.runAsync(eventRef::delete);
    }



    // remaining methods for events to be implemented
    // -----------
    // -----------
    // -----------
    // -----------
    // -----------
}
