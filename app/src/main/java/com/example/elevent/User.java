package com.example.elevent;


import java.io.Serializable;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
    This file contains the implementation of a User object
    Outstanding issues: n/a
 */
/**
 * This class represents a user of the app
 */
public class User implements Serializable {

    // attributes for User class
    // (what information a user has)
    private String name;
    private String[] contact;
    private Blob profilePic;
    private String homePage;
    private String userID;
    private List<String> signedUpEvents;

    public User(){}  // no argument constructor
    public User(String userID){
        this.userID = userID;
        this.signedUpEvents = new ArrayList<>();
    }
    public User(String name, String[] contact, Blob profilePic, String homePage, String userID) {

        this.name = name;
        this.contact = contact;
        this.profilePic = profilePic;
        this.homePage = homePage;
        this.userID = userID;
        this.signedUpEvents = new ArrayList<>();
    }

    public Map<String, Object> userToMap() {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", name);
        userMap.put("contact", contact);
        userMap.put("profile picture", profilePic);
        userMap.put("home page", homePage);
        userMap.put("userID", userID);
        userMap.put("signedUpEvents", signedUpEvents);

        return userMap;
    }

    public String getName() {
        return name;
    }

    /**
     * Setter for the name of the user
     * @param name Name of the user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the contact information of the user
     * @return Contact information of the user
     */
    public String[] getContact() {
        return contact;
    }

    /**
     * Setter for the contact information of the user
     * @param contact Contact information of the user
     */
    public void setContact(String[] contact) {
        this.contact = contact;
    }

    /**
     * Getter for the profile picture of the user
     * @return Profile picture of the user
     */
    public Blob getProfilePic() {
        return profilePic;
    }

    /**
     * Setter for the profile picture of the user
     * @param profilePic Profile picture of the user
     */
    public void setProfilePic(Blob profilePic) {
        this.profilePic = profilePic;
    }

    public List<String> getSignedUpEvents() {
        return signedUpEvents;
    }

    public void setSignedUpEvents(List<String> signedUpEvents) {
        this.signedUpEvents = signedUpEvents;
    }

    /**
     * Getter for the key of the homepage of the user
     * @return Key for the homepage of the user
     */
    public String getHomePage() {
        return homePage;
    }

    /**
     * Setter for the key of the homepage of the user
     * @param homePage Key of the homepage of the user
     */
    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    /**
     * Getter for the unique user ID
     * @return Unique user ID
     */
    public String getUserID() {
        return this.userID;
    }

    /**
     * Setter for the unique user ID
     * @param userID Unique user ID
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }


    /**
     * Create the map to be put into the user database
     * @return Map that contains the user information
     */
    public Map<String, Object> toMap() {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", name);
        userMap.put("contact", contact);
        // profile picture stored as a download link string
        userMap.put("profilePic", profilePic);
        userMap.put("homePage", homePage);
        return userMap;
    }
}
