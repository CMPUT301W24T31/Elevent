package com.example.elevent;

import java.util.HashMap;
import java.util.Map;

public class User {

    // attributes for User class
    // (what information a user has)
    private String name;
    private String[] contact;
    private byte[] profilePic;
    private String homePage;

    private String userID;

    // no argument constructor
    public User() {
    }

    public User(String name, String[] contact, byte[] profilePic, String homePage, String userID) {

        this.name = name;
        this.contact = contact;
        this.profilePic = profilePic;
        this.homePage = homePage;
        this.userID = userID;
    }

    public Map<String, Object> userToMap() {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("name", name);
        eventMap.put("contact", contact);
        eventMap.put("profile picture", profilePic);
        eventMap.put("home page", homePage);
        eventMap.put("userID", userID);

        return eventMap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getContact() {
        return contact;
    }

    public void setContact(String[] contact) {
        this.contact = contact;
    }

    public byte[] getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(byte[] profilePic) {
        this.profilePic = profilePic;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        userID = userID;
    }

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
