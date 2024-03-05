package com.example.elevent;

import com.google.firebase.database.DatabaseReference;

public class UserDB {

    public static void addNewUser(User user, UserDBConnector connector) {
        DatabaseReference dbRef = UserDBConnector.DBConnector();

        //dbRef.child("users").child(user.getName()).setValueAsync(user);
    }
}
