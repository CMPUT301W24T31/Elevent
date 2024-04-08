package com.example.elevent;

import java.io.Serializable;
import com.google.firebase.firestore.Blob;
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
    private String contact;
    private Blob profilePic;
    private String homePage;

    private String userID;
    private List<String> signedUpEvents;
    private Boolean hasGeneratedPFP;
    private List<String> checkedInEvents;
    private List<String> receivedNotifications;

    /**
     * No argument constructor
     */
    public User() {}

    /**
     * Class constructor with two arguments
     * @param userID ID of the user
     * @param profilePic profile picture of the user
     */
    public User(String userID, Blob profilePic, Boolean hasGeneratedPFP){
        this.userID = userID;
        this.signedUpEvents = new ArrayList<>();
        this.profilePic = profilePic;
        this.hasGeneratedPFP = hasGeneratedPFP;
        this.checkedInEvents = new ArrayList<>();
        this.receivedNotifications = new ArrayList<>();
    }

    /**
     * Class constructor
     * @param name name of the user
     * @param contact contact of the user
     * @param profilePic profile picture of the user
     * @param homePage homepage of the user
     * @param userID ID of the user
     */
    public User(String name, String contact, Blob profilePic, String homePage, String userID, Boolean hasGeneratedPFP) {

        this.name = name;
        this.contact = contact;
        this.profilePic = profilePic;
        this.homePage = homePage;
        this.userID = userID;
        this.signedUpEvents = new ArrayList<>();
        this.hasGeneratedPFP = hasGeneratedPFP;
        this.checkedInEvents = new ArrayList<>();
        this.receivedNotifications = new ArrayList<>();
    }

    /**
     * Maps the user object to be stored in the database
     * @return the map of the user object
     */
    public Map<String, Object> userToMap() {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", name);
        userMap.put("contact", contact);
        userMap.put("profilePic", profilePic);
        userMap.put("homePage", homePage);
        userMap.put("userID", userID);
        userMap.put("signedUpEvents", signedUpEvents);
        userMap.put("hasGeneratedPFP", hasGeneratedPFP);
        userMap.put("checkedInEvents", checkedInEvents);
        userMap.put("receivedNotifications", receivedNotifications);

        return userMap;
    }

    /**
     * Getter for the name of the user
     * @return Name of the user
     */
    public String getName() {
        return this.name;
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
     *
     * @return Contact information of the user
     */
    public String getContact() {
        return this.contact;
    }

    /**
     * Setter for the contact information of the user
     * @param contact Contact information of the user
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * Getter for the profile picture of the user
     * @return Profile picture of the user
     */
    public Blob getProfilePic() {
        return this.profilePic;
    }

    /**
     * Setter for the profile picture of the user
     * @param profilePic Profile picture of the user
     */
    public void setProfilePic(Blob profilePic) {
        this.profilePic = profilePic;
    }

    /**
     * Getter for the list of events that the user has signed up for
     * @return List of events that the user has signed up for
     */
    public List<String> getSignedUpEvents() {
        return this.signedUpEvents;
    }

    /**
     * Setter for the list of events that the user has signed up for
     * @param signedUpEvents List of events that the user has signed up for
     */
    public void setSignedUpEvents(List<String> signedUpEvents) {
        this.signedUpEvents = signedUpEvents;
    }

    /**
     * Getter for the key of the homepage of the user
     * @return Key for the homepage of the user
     */
    public String getHomePage() {
        return this.homePage;
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

    public Boolean getHasGeneratedPFP() {
        return hasGeneratedPFP;
    }

    public void setHasGeneratedPFP(Boolean hasGeneratedPFP) {
        this.hasGeneratedPFP = hasGeneratedPFP;
    }

    public List<String> getCheckedInEvents() {
        return checkedInEvents;
    }

    public void setCheckedInEvents(List<String> checkedInEvents) {
        this.checkedInEvents = checkedInEvents;
    }

    public List<String> getReceivedNotifications() {
        return receivedNotifications;
    }

    public void setReceivedNotifications(List<String> receivedNotifications) {
        this.receivedNotifications = receivedNotifications;
    }
}
