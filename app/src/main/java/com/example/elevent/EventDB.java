package com.example.elevent;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
/*
    This file contains the implementation for the Event Database, which stores event objects in a firebase
    Outstanding issues: n/a
 */

/**
 * This class handles event database functionalities
 */
public class EventDB {

    // initialize an instance of the firestore database
    private FirebaseFirestore db;

    /**
     * Class constructor
     * @param connector The connector to the database
     */
    public EventDB(EventDBConnector connector) {
        this.db = connector.getDb();
    }

    /**
     * Add an event to the database
     * @param event Event to be added
     * @return Result of the operation
     */
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

    /**
     * Updates the information of an event in the database
     * @param eventName Name of the event
     * @param updates Map of updates to be made
     * @return Result of the operation
     */
    public CompletableFuture<Void> updateEvent(String eventName, Map<String, Object> updates) {

        DocumentReference eventRef = db.collection("events").document(eventName);

        // asynchronously update the event document in firestore
        return CompletableFuture.runAsync(() -> eventRef.update(updates));
    }


    // delete an event by taking in the eventName, and thus its
    // document name in firestore and deleting it asynchronously (
    // returning the delete and then firestore removes it in the back
    // end)

    /**
     * Deletes an event from the database
     * @param eventName Name of the event
     * @return Result of the operation
     */
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
