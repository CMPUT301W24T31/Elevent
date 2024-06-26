package com.example.elevent;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.util.Consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
/*
    This file contains the implementation for the Event Database, which stores event objects in a firebase
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
     * Class constructor without arguments
     */
    public EventDB() {
        EventDBConnector connector = new EventDBConnector();
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
            db.collection("events").document(event.getEventID()).set(eventMap);
        });
    }

    // updateEvent takes in the eventName( which is used as the document name for an event in the
    // database) and then a map of the updates to be made to the event, creating a reference to where
    // the event info is stored, locating it, and updating the info
    // args: takes in the document name (the event name) and a dictionary of updates to be made to the
    // document

    /**
     * Updates the information of an event in the database
     *
     * @param newEvent An event to be passed in. Can either be the old event with changes or a copy of the old event with changes.
     * @return The result of the task
     */
    public Task<Void> updateEvent(Event newEvent) {
        DocumentReference eventRef = db.collection("events").document(newEvent.getEventID());

        // Asynchronously update the event document in Firestore and return the Task
        return eventRef.update(newEvent.toMap());
    }



    // delete an event by taking in the eventName, and thus its
    // document name in firestore and deleting it asynchronously (
    // returning the delete and then firestore removes it in the back
    // end)

    /**
     * Deletes an event from the database
     * @param eventID ID of the event
     * @return Result of the operation
     */
    public CompletableFuture<Void> deleteEvent(String eventID) {
        DocumentReference eventRef = db.collection("events").document(eventID);
        return CompletableFuture.runAsync(() -> {
            try {
                Tasks.await(eventRef.delete());
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
    /**
     * Queries the Firebase for a list of all events
     * @return Event ArrayList of all events in the Firebase
     */

    public ArrayList<Event> getAllEvents() {
        ArrayList<Event> allEvents = new ArrayList<Event>();

        db.collection("events").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Event event = document.toObject(Event.class);
                    allEvents.add(event);
                }
            } else {
                Log.d("AllEventsFragment", "Error getting documents: ", task.getException());
            }
        });

        return allEvents;
    }

    public void getAllEvents(final Consumer<List<Event>> callback) {
                        db.collection("events").get().addOnCompleteListener(task -> {
                            ArrayList<Event> allEvents = new ArrayList<>();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Event event = document.toObject(Event.class);
                    allEvents.add(event);
                }
                callback.accept(allEvents); // Use the callback to return the list of events
            } else {
                Log.d("EventDB", "Error getting documents: ", task.getException());
            }
        });
    }

    /**
     * Queries the Firebase for a list of events organized by a user with a specified userID
     * @param userID Takes in a UserID string to query the firebase
     * @return Event ArrayList of all events organized by the user with the userID
     */
    // TODO: 2024-03-15 give Event organizer param and complete function
    public ArrayList<Event> getMyOrganizedEvents(String userID) {
        ArrayList<Event> myEvents = new ArrayList<Event>();


        return myEvents;
    }

    /**
     * Queries the Firebase for a list of events a user with a specified userID has signed up for
     * @param userID Takes in a UserID string to query the firebase
     * @return Event ArrayList of all events the user with the userID has signed up for
     */

    // TODO: 2024-03-15 give Event signedUpUsers param and complete function
    public ArrayList<Event> getMySignedUpEvents(String userID) {
        ArrayList<Event> myEvents = new ArrayList<Event>();


        return myEvents;
    }




    // remaining methods for events to be implemented
    // -----------
    // -----------
    // -----------
    // -----------
    // -----------
}
