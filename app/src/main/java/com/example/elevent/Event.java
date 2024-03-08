package com.example.elevent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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
    //private Map<String, Object> location; // Assuming conversion to a Map or GeoPoint

    /**
     * Event class constructor
     * @param eventName Name of the event
     * @param promotionalQR QR code that links to event description and poster in app
     * @param checkinQR QR code that attendee scans to check in
     * @param attendeesCount Number of attendees checked in to the event
     * @param eventPoster Image uploaded by organizer to provide visual information
     */
    public Event(String eventName, byte[] eventPoster) {
        this.eventName = eventName;
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

    /**
     * Getter for event name
     * @return Event name
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Getter for the promotional QR code
     * @return Promotional QR code
     */
    public byte[] getPromotionalQR() {
        return promotionalQR;
    }

    /**
     * Getter for the check in QR code
     * @return Check in QR code
     */
    public byte[] getCheckinQR() {
        return checkinQR;
    }

    /**
     * Getter for the number of attendees checked in
     * @return Number of attendees checked in
     */
    public int getAttendeesCount() {
        return attendeesCount;
    }

    /**
     * Getter for the event poster
     * @return Event poster
     */
    public byte[] getEventPoster() {
        return eventPoster;
    }

    /*public Map<String, Object> getLocation() {
        return location;
    }*/

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
     * Setter for the number of attendees checked in to the event
     * @param attendeesCount Number of attendees checked in to the event
     */
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
