package com.example.elevent;

import android.graphics.Bitmap;
import android.location.Location;
import android.media.Image;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class Event implements Serializable {

    // attributes for the information of an event
    private static final String eventID = "test";
    private String eventName;
    private Bitmap promotionalQR;
    private String promotionalQRUrl;
    private Bitmap checkinQR;
    private String checkinQRUrl;
    private int attendeesCount;
    private Image eventPoster;
    private String eventPosterUrl;
    private Location location;

    public Event(String eventName, Bitmap promotionalQR, Bitmap checkinQR, int attendeesCount, Image eventPoster, Location location) {
        this.eventName = eventName;
        this.promotionalQR = promotionalQR;
        this.checkinQR = checkinQR;
        this.attendeesCount = attendeesCount;
        this.eventPoster = eventPoster;
        this.location = location;
    }

    public String getEventName() {
        return eventName;
    }

    public Bitmap getPromotionalQR() {
        return promotionalQR;
    }

    public String getPromotionalQRUrl() {
        return promotionalQRUrl;
    }
    public Bitmap getCheckinQR() {
        return checkinQR;
    }

    public String getCheckinQRUrl() {
        return checkinQRUrl;
    }

    public int getAttendeesCount() {
        return attendeesCount;
    }

    public Image getEventPoster() {
        return eventPoster;
    }

    public String getEventPosterUrl() {
        return eventPosterUrl;
    }

    public Location getLocation() {
        return location;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setPromotionalQR(Bitmap promotionalQR) {
        this.promotionalQR = promotionalQR;
    }

    public void setPromotionalQRUrl(String promotionalQRUrl) {
        this.promotionalQRUrl = promotionalQRUrl;
    }

    public void setCheckinQR(Bitmap checkinQR) {
        this.checkinQR = checkinQR;
    }

    public void setCheckinQRUrl(String checkinQRUrl) {
        this.checkinQRUrl = checkinQRUrl;
    }

    public void setAttendeesCount(int attendeesCount) {
        this.attendeesCount = attendeesCount;
    }

    public void setEventPoster(Image eventPoster) {
        this.eventPoster = eventPoster;
    }

    public void setEventPosterUrl(String eventPosterUrl) {
        this.eventPosterUrl = eventPosterUrl;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    // a mapping function that maps event info
    // to Firestore
    public Map<String, Object> toMap() {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("name", eventName);
        eventMap.put("promotionalQRUrl", promotionalQRUrl);
        eventMap.put("checkinQRUrl", checkinQRUrl);
        eventMap.put("attendeesCount", attendeesCount);
        eventMap.put("eventPosterUrl", eventPosterUrl);
        eventMap.put("location", location);

        return eventMap;
    }
}
