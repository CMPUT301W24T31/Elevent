package com.example.elevent;

import androidx.annotation.Keep;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
    This file contains the implementation of an Event object
    Outstanding Issues: figure out QR codes, location, notifications
 */
/**
 * Represents an event
 */
public class Event implements Serializable {

    // attributes for the information of an event
    private String organizerID;
    private String eventID;
    private String eventName;
    private Blob promotionalQR; //byte array
    private Blob checkinQR; // byte array
    private int attendeesCount;
    private Blob eventPoster; //byte array
    private String date;
    private String time;
    private String description;
    private String location;
    private List<String> notifications;
    private List<String> signedUpAttendees;
    private Map<String, GeoPoint> checkInLocations;
    private Map<String, Integer> checkedInAttendees;
    private String sha256ReusedQRContent;

    // No-argument constructor
    public Event() {
    }

    /**
     * Class constructor without reused QR
     * @param eventName Name of the event
     * @param promotionalQR QR code linked to event poster an description
     * @param checkinQR QR code for checking in to the event
     * @param attendeesCount Number of attendees checked in
     * @param date Date of the event
     * @param time Time of the event
     * @param description Description of the event
     * @param location Location of the event
     * @param eventPoster Uploaded poster of the event
     */
    public Event(String eventID, String organizerID, String eventName, Blob promotionalQR, Blob checkinQR, int attendeesCount,
                 String date, String time, String description, String location, Blob eventPoster) {
        this.eventID = eventID;
        this.organizerID = organizerID;
        this.eventName = eventName;
        this.promotionalQR = promotionalQR;
        this.checkinQR = checkinQR;
        this.attendeesCount = attendeesCount;
        this.date = date;
        this.time = time;
        this.description = description;
        this.eventPoster = eventPoster;
        this.location = location;
        this.notifications = new ArrayList<>();
        this.signedUpAttendees = new ArrayList<>();
        this.checkInLocations = new HashMap<>();
        this.checkedInAttendees = new HashMap<>();
    }
    /**
     * Class constructor with reused QR
     * @param eventName Name of the event
     * @param promotionalQR QR code linked to event poster an description
     * @param checkinQR QR code for checking in to the event
     * @param attendeesCount Number of attendees checked in
     * @param date Date of the event
     * @param time Time of the event
     * @param description Description of the event
     * @param location Location of the event
     * @param eventPoster Uploaded poster of the event
     * @param sha256ReusedQRContent Content of the reused QR code converted into SHA-256 encryption
     */
    public Event(String eventID, String organizerID, String eventName, Blob promotionalQR, Blob checkinQR, int attendeesCount,
                 String date, String time, String description, String location, Blob eventPoster, String sha256ReusedQRContent) {
        this.eventID = eventID;
        this.organizerID = organizerID;
        this.eventName = eventName;
        this.promotionalQR = promotionalQR;
        this.checkinQR = checkinQR;
        this.attendeesCount = attendeesCount;
        this.date = date;
        this.time = time;
        this.description = description;
        this.eventPoster = eventPoster;
        this.location = location;
        this.sha256ReusedQRContent = sha256ReusedQRContent;
        this.notifications = new ArrayList<>();
        this.signedUpAttendees = new ArrayList<>();
        this.checkInLocations = new HashMap<>();
        this.checkedInAttendees = new HashMap<>();
    }

    /**
     * Create the map to be put into the event database
     * @return Map that contains the event information
     */
    public Map<String, Object> toMap() {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("eventID", eventID);
        eventMap.put("organizerID", organizerID);
        eventMap.put("eventName", eventName);
        eventMap.put("location", location);
        eventMap.put("date", date);
        eventMap.put("time", time);
        eventMap.put("description", description);
        eventMap.put("promotionalQR", promotionalQR); // store as byte array
        eventMap.put("checkinQR", checkinQR); // store as byte array
        eventMap.put("attendeesCount", attendeesCount);
        eventMap.put("eventPoster", eventPoster); // store as byte array
        eventMap.put("notifications", notifications);
        eventMap.put("signedUpAttendees", signedUpAttendees);
        eventMap.put("checkedInAttendees", checkedInAttendees);
        eventMap.put("checkInLocations", checkInLocations);
        eventMap.put("sha256ReusedQRContent", sha256ReusedQRContent);

        return eventMap;
    }

    /**
     * Getter for ID of the organizer
     * @return ID of the organizer
     */
    public String getOrganizerID() {
        return organizerID;
    }


    /**
     * Getter for the event name
     *
     * @return Name of the event
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Getter for the event location
     * @return Location of the event
     */
    public String getLocation() {
        return location;
    }

    /**
     * Getter for the event date
     * @return Date of the event
     */
    public String getDate() {
        return date;
    }

    /**
     * Getter for the event time
     * @return Time of the event
     */
    public String getTime() {
        return time;
    }

    /**
     * Getter for the event description
     * @return Description of the event
     */
    public String getDescription() {
        return description;
    }

    /**
     * Getter for the promotional QR code
     * @return Promotional QR code for the event
     */
    public Blob getPromotionalQR() {
        return promotionalQR;
    }

    /**
     * Getter for the check in QR code
     * @return Check in QR code for the event
     */
    public Blob getCheckinQR() {
        return checkinQR;
    }

    /**
     * Getter for the number of attendees checked in to the event
     * @return Number of attendees checked in
     */
    public int getAttendeesCount() {
        return attendeesCount;
    }

    /**
     * Getter for the event poster
     * @return Poster for the event
     */
    public Blob getEventPoster() {
        return eventPoster;
    }

    /**
     * Getter for the checked in attendees
     * @return Attendees that have checked in
     */
    public Map<String, Integer> getCheckedInAttendees() {
        return checkedInAttendees;
    }

    /**
     * Getter for the signed up attendees
     * @return Attendees that have signed up
     */
    public List<String> getSignedUpAttendees() {
        return signedUpAttendees;
    }

    /**
     * Setter for the organizer ID
     * @param organizerID ID of the organizer
     */
    public void setOrganizerID(String organizerID) {
        this.organizerID = organizerID;
    }

    /**
     * Setter for the event name
     * @param eventName Name of the event
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * Setter for the promotional QR code
     * @param promotionalQR Promotional QR code for the event
     */
    public void setPromotionalQR(Blob promotionalQR) {
        this.promotionalQR = promotionalQR;
    }

    /**
     * Setter for the check in QR code
     * @param checkinQR Check in QR code for the event
     */
    public void setCheckinQR(Blob checkinQR) {
        this.checkinQR = checkinQR;
    }

    /**
     * Setter for the number of attendees checked in
     * @param attendeesCount Number of attendees checked in
     */
    public void setAttendeesCount(int attendeesCount) {
        this.attendeesCount = attendeesCount;
    }

    /**
     * Setter for the event poster
     * @param eventPoster Poster for the event
     */
    public void setEventPoster(Blob eventPoster) {
        this.eventPoster = eventPoster;
    }

    /**
     * Setter for the event location
     * @param location Location of the event
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Setter for the date of the event
     * @param date Date of the event
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Setter for the time of the event
     * @param time Time of the event
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Setter for the description of the event
     * @param description Description of the event
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter for the notifications for the event
     * @return Notifications for the event
     */
    public List<String> getNotifications() {
        return notifications;
    }

    /**
     * Setter for the notifications of the event
     * @param notifications Notifications for the event
     */
    public void setNotifications(List<String> notifications) {
        this.notifications = notifications;
    }

    /**
     * Setter for the signed up attendees
     * @param signedUpAttendees Attendees that have signed up
     */
    public void setSignedUpAttendees(List<String> signedUpAttendees) {
        this.signedUpAttendees = signedUpAttendees;
    }

    /**
     * Setter for the checked in attendees
     * @param checkedInAttendees Attendees that have checked in
     */
    public void setCheckedInAttendees(Map<String, Integer> checkedInAttendees){this.checkedInAttendees = checkedInAttendees;}

    /**
     * Getter for the event ID
     * @return ID of the event
     */
    public String getEventID() {
        return eventID;
    }

    /**
     * Getter for the locations of attendee check ins
     * @return
     */
    public Map<String, GeoPoint> getCheckInLocations() {
        return checkInLocations;
    }

    /**
     * For reused QR codes, getter for the encrypted SHA-256 content
     * @return SHA-256 encrypted content
     */
    public String getSha256ReusedQRContent() {
        return sha256ReusedQRContent;
    }

    /**
     * For reused QR codes, setter for the encrypted SHA-256 content
     * @param sha256ReusedQRContent SHA-256 encrypted content
     */
    public void setSha256ReusedQRContent(String sha256ReusedQRContent) {
        this.sha256ReusedQRContent = sha256ReusedQRContent;
    }

    /**
     * Used when user scans the QR code to update their scanned location.
     * Locations are Latlngs which are converted to Geopoints.
     * Locations are stored in a HashMap that maps Geopoints to the UserID key.
     * @param userID Pass in userID when user scans the QR code
     * @param location Get location as LatLng when user scans the QR code. Cannot be null.
     */
    public void addCheckInLocation(String userID, LatLng location) {
        GeoPoint geoPointLoc = new GeoPoint(location.latitude, location.longitude);
        checkInLocations.put(userID, geoPointLoc);
    }

    /**
     * Admin only function. Call when admin deletes a user.
     * @param userID The userID of the user to be removed.
     */

    // TODO: 2024-04-02 Make Sure Admin Uses This
    public void removeCheckInLocation(String userID) {
        if (checkInLocations.containsKey(userID)) {
            checkInLocations.remove(userID);
        }
    }
}
