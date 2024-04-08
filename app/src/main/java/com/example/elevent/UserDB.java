package com.example.elevent;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


/*
    This file is responsible for handling all user database functionalities required for the app
 */
/**
 * This class handles all user database functionalities
 */
public class UserDB extends MainActivity {

    private FirebaseFirestore db;

    /**
     * Class constructor
     * @param connector The connector to the database
     */
    public UserDB(UserDBConnector connector) {
        this.db = connector.getDb();
    }

    /**
     * No argument class constructor
     */
    public UserDB() {
        UserDBConnector connector = new UserDBConnector();
        this.db = connector.getDb();
    }

    /**
     * Adds a new user to the database
     *
     * @param user User to be added
     */
    public void addUser (User user) {

        // a map of all the user information to be added into a document
        // on firestore
        Map<String, Object> userMap = user.userToMap();

        // asynchronously add the user to Firestore and name the document
        // the name of the event
        CompletableFuture.runAsync(() -> {
            db.collection("users").document(user.getUserID()).set(userMap);
        });
    }

    /**
     * Updates the information of an user in the database
     *
     * @param user The updated user to be passed into the firestore
     */
    public void updateUser(User user) {


        // create a reference to the user documented meant to be edited and updated
        DocumentReference userRef = db.collection("users").document(user.getUserID());

        // asynchronously update the user document in firestore
        CompletableFuture.runAsync(() -> userRef.update(user.userToMap()));

    }

    // interface for callbacks when reading user data
    public void readUser(String userID, final OnUserReadListener listener) {

        db.collection("users").document(userID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        listener.onSuccess(user);
                    } else {
                        listener.onFailure(new Exception("User cannot be found"));
                    }
                })
                .addOnFailureListener(listener::onFailure);
        /* changed UserDB implementation to use userID from MainActivity that
        is randomly generated, thus below code does not work but is kept for reference

        db.collection("User").document(userID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Convert the documentSnapshot to a User object
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            user.setUserID(documentSnapshot.getId()); // Ensure the documentId is set in the User object
                            listener.onSuccess(user);
                        } else {
                            listener.onFailure(new Exception("Failed to parse user data."));
                        }
                    } else {
                        listener.onFailure(new Exception("User not found."));
                    }
                })
                .addOnFailureListener(listener::onFailure);
         */

        /* Before using userIDs to parse through database and return user info that way
        db.collection("User").document(userName).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        listener.onSuccess(user);
                    } else {
                        listener.onFailure(new Exception("User not found"));
                    }
                })
                .addOnFailureListener(e -> listener.onFailure(e));
         */
    }


    /**
     * Interface for listener for callbacks when reading user data
     */
    public interface OnUserReadListener {
        void onSuccess(User user);
        // handles the successfully fetched user
        void onFailure(Exception e);
        // handle the error of user not being parsed
    }
    public void removeEventFromUsers(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").whereArrayContains("signedUpEvents", eventId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    if (user != null) {
                        // Ensure lists are not null
                        List<String> updatedSignedUpEvents = user.getSignedUpEvents() != null ? user.getSignedUpEvents() : new ArrayList<>();
                        List<String> updatedCheckedInEvents = user.getCheckedInEvents() != null ? user.getCheckedInEvents() : new ArrayList<>();

                        // Remove eventId if present
                        boolean updated = updatedSignedUpEvents.remove(eventId) | updatedCheckedInEvents.remove(eventId);

                        if (updated) {
                            // Update the document only if changes were made
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("signedUpEvents", updatedSignedUpEvents);
                            updates.put("checkedInEvents", updatedCheckedInEvents);
                            db.collection("users").document(document.getId()).update(updates);
                        }
                    }
                }
            } else {
                Log.e("UserDB", "Error querying users by event ID: ", task.getException());
            }
        });
    }



    public void checkUserExists(String userId, OnUserReadListener listener) {
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    // User exists
                    listener.onSuccess(document.toObject(User.class)); // Assuming a User class exists that can be instantiated from a DocumentSnapshot
                } else {
                    // User does not exist
                    listener.onFailure(new Exception("User does not exist"));
                }
            } else {
                // Error occurred
                listener.onFailure(task.getException());
            }
        });
    }


}
