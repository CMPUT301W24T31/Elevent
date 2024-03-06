package com.example.elevent;

import android.graphics.Bitmap;
import android.location.Location;
import android.media.Image;

import java.io.Serializable;

public class Event implements Serializable {
    private static final String eventID = "placeholder";
    private String eventName;
    private Bitmap promotionalQR;
    private Bitmap checkinQR;
    private int attendeesCount;
    private Image eventPoster;
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

    public Bitmap getCheckinQR() {
        return checkinQR;
    }

    public int getAttendeesCount() {
        return attendeesCount;
    }

    public Image getEventPoster() {
        return eventPoster;
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

    public void setCheckinQR(Bitmap checkinQR) {
        this.checkinQR = checkinQR;
    }

    public void setAttendeesCount(int attendeesCount) {
        this.attendeesCount = attendeesCount;
    }

    public void setEventPoster(Image eventPoster) {
        this.eventPoster = eventPoster;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
