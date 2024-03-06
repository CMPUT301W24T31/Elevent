package com.example.elevent;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;


public class UserDB {

    private FirebaseFirestore db;

    // initialize an instance of the firestore database
    public UserDB(UserDBConnector connector) {
        this.db = connector.getDb();
    }

    public void addNewUser(User user) {
        db.runTransaction((Transaction.Function<Void>) transaction -> {

                    // initialize database and snapshot of database
                    DocumentReference counterRef = db.collection("Counter").document("user_count");
                    DocumentSnapshot snapshot = transaction.get(counterRef);

                    long newCount = snapshot.getLong("count") + 1; // increment the count for new user

                    String userID = "user" + newCount;
                    user.setUserID(userID);

                    transaction.update(counterRef, "count", newCount); // update the count in firestore

                    // name the user document with string "user" + current count(in our case newCount)
                    DocumentReference newUserRef = db.collection("User").document("user" + newCount);
                    transaction.set(newUserRef, user.toMap());

                    // if successful, returns nothing
                    return null;
                }).addOnSuccessListener(aVoid -> System.out.println("User added successfully"))
                .addOnFailureListener(e -> System.out.println("Error adding user: " + e.getMessage()));


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

        // SetOptions.merge() recommended by ChatGPT
        db.collection("User").document(user.getUserID()).set(user.toMap(), SetOptions.merge())
                .addOnSuccessListener(aVoid -> System.out.println("User updated successfully"))
                .addOnFailureListener(e -> System.out.println("Error updating user: " + e.getMessage()));


        /* before implementing user count and userID
        db.collection("User").document(user.getName()).update(user.toMap())
                .addOnSuccessListener(aVoid -> System.out.println("User updated successfully"))
                .addOnFailureListener(e -> System.out.println("Error updating user: " + e.getMessage()));
         */
    }

    // TODO: UPDATE READUSER AND DELETE USER TO MAKE USE OF CUSTOM USERIDs
    public void readUser(String userName, final OnUserReadListener listener) {
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
    }

    public void deleteUser(User user) {

        db.collection("User").document(user.getName()).delete()
                .addOnSuccessListener(aVoid -> System.out.println("User deleted successfully"))
                .addOnFailureListener(e -> System.out.println("Error deleting user: " + e.getMessage()));
    }

    // interface for callbacks when reading user data
    public interface OnUserReadListener {
        void onSuccess(User user);
        void onFailure(Exception e);
    }

}
