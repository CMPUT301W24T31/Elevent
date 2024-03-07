package com.example.elevent;

import android.media.Image;

import java.util.HashMap;
import java.util.Map;

public class User {

    // attributes for User class
    // (what information a user has)
    private String name;
    private String[] contact;
    private Image profilePic;
    private String homePage;

    private String userID;

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

    public Image getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Image profilePic) {
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
