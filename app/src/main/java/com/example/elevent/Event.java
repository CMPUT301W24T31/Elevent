package com.example.elevent;

import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;

import java.io.Serializable;
import java.lang.String;

public class Event implements Serializable {
    private String eventName;
    private Bitmap promotionalQR;
    private Bitmap checkinQR;
    private int attendeesCount;
    private Uri eventPoster;
    private Location geolocation;
    private String address;
    private String description;
    private String date;
    private String time;



    public Event(String eventName, Uri eventPoster, String address, String description, String date, String time) {
        this.eventName = eventName;
        this.eventPoster = eventPoster;
        this.address = address;
        this.description = description;
        this.date = date;
        this.time = time;
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

    public Uri getEventPoster() {
        return eventPoster;
    }

    public Location getGeolocation() {
        return geolocation;
    }
    public String getDescription(){return description;}
    public String getAddress(){return address;}

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
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

    public void setEventPoster(Uri eventPoster) {
        this.eventPoster = eventPoster;
    }

    public void setGeolocation(Location geolocation) {
        this.geolocation = geolocation;
    }
    public void setDescription(String description){this.description = description;}

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
