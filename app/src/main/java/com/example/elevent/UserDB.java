package com.example.elevent;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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

}
