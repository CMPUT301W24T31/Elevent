package com.example.elevent;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;

import java.util.Map;
import java.util.concurrent.CompletableFuture;


/*
    This file is responsible for handling all user database functionalities required for the app
    Outstanding issues: n/a
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

    public UserDB() {
        UserDBConnector connector = new UserDBConnector();
        this.db = connector.getDb();
    }

    public CompletableFuture<Void> addUser (User user) {

        // a map of all the user information to be added into a document
        // on firestore
        Map<String, Object> userMap = user.userToMap();

        // asynchronously add the user to Firestore and name the document
        // the name of the event
        return CompletableFuture.runAsync(() -> {
            db.collection("users").document(user.getUserID()).set(userMap);
        });


        /* before implementing count to create custom user count document name for each user
        db.collection("User").document(user.getName()).set(user.toMap())
                .addOnSuccessListener(aVoid -> System.out.println("User added successfully"))
                .addOnFailureListener(e -> System.out.println("Error adding user: " + e.getMessage()));
         */
    }

    /**
     * Updates the information of an user in the database
     * @param user The updated user to be passed into the firestore
     * @return Result of the operation
     */
    public CompletableFuture<Void> updateUser(User user) {


        // create a reference to the user documented meant to be edited and updated
        DocumentReference userRef = db.collection("users").document(user.getUserID());

        // asynchronously update the user document in firestore
        return CompletableFuture.runAsync(() -> userRef.update(user.userToMap()));

    }


    // the userID used as an argument for this method can be retrieved using the getter
    // methods getUserID which should work once we have set the UserID when a user is added
    // in addUser (since we use the setter setUserID once a user is added)

    /**
     * Read a user's information in the database
     * @param userID UserID of the user whose information is to be read
     * @param listener Listener that checks if the user's information has been read
     */
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

    // method used to delete a user from the user database
    public CompletableFuture<Void> deleteUser(String userID) {

        DocumentReference userRef = db.collection("users").document(userID); // Reference to the event document

        // after getting a reference of the document, delete the document
        return CompletableFuture.runAsync(userRef::delete);
    }

    // interface for callbacks when reading user data

    /**
     * Interface for listener for callbacks when reading user data
     */
    public interface OnUserReadListener {
        void onSuccess(User user);
        // handles the successfully fetched user
        // System.out.println("User Name: " + user.getName()); is what we would implement
        void onFailure(Exception e);
        // handle the error of user not being parsed
        // System.err.println("Error fetching user: " + e.getMessage()); is what we would implement
    }

}
