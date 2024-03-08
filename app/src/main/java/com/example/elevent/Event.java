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
    //private Map<String, Object> location; // Assuming conversion to a Map or GeoPoint

    // event class constructor
    public Event(String eventName, byte[] promotionalQR, byte[] checkinQR, int attendeesCount, byte[] eventPoster) {
        this.eventName = eventName;
        this.promotionalQR = promotionalQR;
        this.checkinQR = checkinQR;
        this.attendeesCount = attendeesCount;
        this.eventPoster = eventPoster;
        //this.location = location;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("eventName", eventName);
        eventMap.put("promotionalQR", promotionalQR); // store as byte array
        eventMap.put("checkinQR", checkinQR); // store as byte array
        eventMap.put("attendeesCount", attendeesCount);
        eventMap.put("eventPoster", eventPoster); // store as byte array
        //eventMap.put("location", location);

        return eventMap;
    }

    public String getEventName() {
        return eventName;
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

    /*public Map<String, Object> getLocation() {
        return location;
    }*/

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

    /*public void setLocation(Map<String, Object> location) {
        this.location = location;
    }*/
}
