package com.example.elevent;

import java.io.Serializable;
import java.util.HashMap;
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
    private static final String eventID = "test";
    private String eventName;
    private byte[] promotionalQR; //byte array
    private byte[] checkinQR; // byte array
    private int attendeesCount;
    private byte[] eventPoster; //byte array
    private String date;
    private String time;
    private String description;
    private String location;
    private String[] notifications;

    // No-argument constructor
    public Event() {

    }

    /**
     * Class constructor
     * @param eventName Name of the event
     * @param promotionalQR QR code linked to event poster an description
     * @param checkinQR QR code for checking in to the event
     * @param attendeesCount Number of attendees checked in
     * @param date Date of the event
     * @param time Time of the event
     * @param description Description of the event
     * @param location Location of the event
     * @param eventPoster Uploaded poster of the event
     * @param notifications Notifications for the event
     */
    public Event(String eventName, byte[] promotionalQR, byte[] checkinQR, int attendeesCount,
                 String date, String time, String description, String location, byte[] eventPoster,
                 String[] notifications) {
        this.eventName = eventName;
        this.promotionalQR = promotionalQR;
        this.checkinQR = checkinQR;
        this.attendeesCount = attendeesCount;
        this.date = date;
        this.time = time;
        this.description = description;
        this.eventPoster = eventPoster;
        this.location = location;
        this.notifications = notifications;
    }

    /**
     * Create the map to be put into the event database
     * @return Map that contains the event information
     */
    public Map<String, Object> toMap() {
        Map<String, Object> eventMap = new HashMap<>();
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

        return eventMap;
    }

    /**
     * Getter for the event name
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
    public byte[] getPromotionalQR() {
        return promotionalQR;
    }

    /**
     * Getter for the check in QR code
     *
     * @return Check in QR code for the event
     */
    public byte[] getCheckinQR() {
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
    public byte[] getEventPoster() {
        return eventPoster;
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
    public void setPromotionalQR(byte[] promotionalQR) {
        this.promotionalQR = promotionalQR;
    }

    /**
     * Setter for the check in QR code
     * @param checkinQR Check in QR code for the event
     */
    public void setCheckinQR(byte[] checkinQR) {
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
    public void setEventPoster(byte[] eventPoster) {
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
    public String[] getNotifications() {
        return notifications;
    }

    /**
     * Setter for the notifications of the event
     * @param notifications Notifications for the event
     */
    public void setNotifications(String[] notifications) {
        this.notifications = notifications;
    }

    public void addNotification(String newNotificationMessage) {

    }
}
