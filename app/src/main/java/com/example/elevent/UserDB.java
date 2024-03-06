package com.example.elevent;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class UserDB {


    // used for adding/deleting a custom pathsString containing
    // user info which is a child of the Users child from root
    private static int userCount = 0;

    // method that adds a new user to the UserDB
    public static void addNewUser(User user) {

        // initialize an attribute that points to the start
        // of the User database
        UserDBConnector connector = new UserDBConnector();
        DatabaseReference usersRef = connector.getRootDB();

        //create a counter for each new User added
        userCount++; // Increment the counter
        String userKey = "user" + userCount; // Create the user key

        usersRef.child(userKey).child(user.getName()).setValue(user);
    }

    // update the user info of a user
    public static void updateUser(String userKey, User user, UserDBConnector connector) {

        DatabaseReference usersRef = connector.getRootDB();

        // find a way to map a user to their user key to access the user info
    }

    // fetch and read the user info of a user
    public static void readUser(String userKey, UserDBConnector connector, ValueEventListener listener) {

        DatabaseReference usersRef = connector.getRootDB();
        // add a listener for a single event to read the data at the user's user key
        usersRef.child(userKey).addListenerForSingleValueEvent(listener);
    }

    public static void deleteUser(String userKey, UserDBConnector connector) {\

        DatabaseReference usersRef = connector.getRootDB();

        // find a way to access the user key of
        // a user to delete that user key from the
        // Users
    }



}
