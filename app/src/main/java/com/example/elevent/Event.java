package com.example.elevent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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
    // event class constructor
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

    public String getEventName() {
        return eventName;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }


    public byte[] getPromotionalQR() {
        return promotionalQR;
    }

    public byte[] getCheckinQR() {
        return checkinQR;
    }

    public int getAttendeesCount() {
        return attendeesCount;
    }

    public byte[] getEventPoster() {
        return eventPoster;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setPromotionalQR(byte[] promotionalQR) {
        this.promotionalQR = promotionalQR;
    }


    public void setCheckinQR(byte[] checkinQR) {
        this.checkinQR = checkinQR;
    }

    public void setAttendeesCount(int attendeesCount) {
        this.attendeesCount = attendeesCount;
    }

    public void setEventPoster(byte[] eventPoster) {
        this.eventPoster = eventPoster;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getNotifications() {
        return notifications;
    }

    public void setNotifications(String[] notifications) {
        this.notifications = notifications;
    }

    public void addNotification(String newNotificationMessage) {

    }
}
