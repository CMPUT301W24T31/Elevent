package com.example.elevent;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;


public class UserDB extends MainActivity {

    private FirebaseFirestore db;

    // initialize an instance of the firestore database
    public UserDB(UserDBConnector connector) {
        this.db = connector.getDb();
    }

    public void addNewUser(User user) {

        // retrieve the userID from MainActivity
        String userId = getUserIDForUserDB();
        user.setUserID(userId); // set the randomly generated userID from MainActivity as the userID in User class

        db.collection("User").document(userId).set(user.toMap())
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User added successfully"))
                .addOnFailureListener(e -> Log.d("Firestore", "Error adding user", e));


        /* before implementing count to create custom user count document name for each user
        db.collection("User").document(user.getName()).set(user.toMap())
                .addOnSuccessListener(aVoid -> System.out.println("User added successfully"))
                .addOnFailureListener(e -> System.out.println("Error adding user: " + e.getMessage()));
         */
    }

    public void updateUser(User user) {

        // if the userID does not exist(there is no user document in
        // firestore to update user info)
        if (user.getUserID() == null || user.getUserID().isEmpty()) {
            System.out.println("User document ID is missing.");
            return;
        }

        // SetOptions.merge() recommended by Openai, ChatGPT, March 6th, 2024, "how to update data in
        // firestore in an existing document

        // also, the document path using user.getUserID() hopefully should use the userID from MainActivity,
        // that was set using the setter found in the User class into the userID attribute for a user ( User user )
        db.collection("User").document(user.getUserID()).set(user.toMap(), SetOptions.merge())
                .addOnSuccessListener(aVoid -> System.out.println("User updated successfully"))
                .addOnFailureListener(e -> System.out.println("Error updating user: " + e.getMessage()));


        /* before implementing user count and userID
        db.collection("User").document(user.getName()).update(user.toMap())
                .addOnSuccessListener(aVoid -> System.out.println("User updated successfully"))
                .addOnFailureListener(e -> System.out.println("Error updating user: " + e.getMessage()));
         */
    }


    // the userID used as an argument for this method can be retrieved using the getter
    // methods getUserID which should work once we have set the UserID when a user is added
    // in addUser (since we use the setter setUserID once a user is added)
    public void readUser(String userID, final OnUserReadListener listener) {

        db.collection("User").document(userID).get()
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

    // get the userID using the getter method in MainActivity
    // getUserIDForUserDB
    public void deleteUser(String userID) {

        db.collection("User").document(userID).delete()
                .addOnSuccessListener(aVoid -> System.out.println("User successfully deleted."))
                .addOnFailureListener(e -> System.out.println("Error deleting user: " + e.getMessage()));
        /*
        db.collection("User").document(user.getName()).delete()
                .addOnSuccessListener(aVoid -> System.out.println("User deleted successfully"))
                .addOnFailureListener(e -> System.out.println("Error deleting user: " + e.getMessage()));

         */
    }

    /* retrieve userID from MainActivity to be used in this file,
        scrapped because we are inheriting from MainActivity and just use
        getUserIDForUserDB() method found in MainActivity

        public String retrieveUserIDForDocumentName() {

        String userID = getUserIDForUserDB();

        if (userID == null) {
            System.out.println("User ID does not exist");
        }
        return userID;
    }
     */

    // interface for callbacks when reading user data
    public interface OnUserReadListener {
        void onSuccess(User user);
        // handles the successfully fetched user
        // System.out.println("User Name: " + user.getName()); is what we would implement
        void onFailure(Exception e);
        // handle the error of user not being parsed
        // System.err.println("Error fetching user: " + e.getMessage()); is what we would implement
    }

}
