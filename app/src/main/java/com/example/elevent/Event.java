package com.example.elevent;

import android.graphics.Bitmap;
import android.location.Location;
import android.media.Image;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;


public class Event implements Serializable {
    private static final String eventID = "test";
    private String eventName;
    private Bitmap promotionalQR;
    private Bitmap checkinQR;
    private int attendeesCount;
    private Image eventPoster;
    private Location location;
    private ZonedDateTime eventTimeCreatorZone;


    public Event(String eventName, Bitmap promotionalQR, Bitmap checkinQR, int attendeesCount, Image eventPoster, Location location, ZonedDateTime eventTimeCreatorZone) {
        this.eventName = eventName;
        this.promotionalQR = promotionalQR;
        this.checkinQR = checkinQR;
        this.attendeesCount = attendeesCount;
        this.eventPoster = eventPoster;
        this.location = location;
        this.eventTimeCreatorZone = eventTimeCreatorZone;

    }

    public String getEventName() {
        return eventName;
    }
    public ZonedDateTime getEventTimeCreatorZonePure() {
        return eventTimeCreatorZone;
    }

    /**
     * Converts the time zone to the user's time zone and returns the event date and time as a formatted string.
     * Examples: March 8, 2023 @ 17:03 becomes "Mar 7, 2023|5:03 pm" and September 16, 2021 @11:00 becomes "Sept 16, 2021|11:00 am"
     * To use the string, String[] s = event.getEventTimeUserZoneString().split("|") will put the date in s[0] and time in s[1]
     * @return Date as String of form "MMM d, yyyy|h:mm a"
     */
    public String getEventTimeUserZoneString() {
        String eventTimeString = new String();
//        TimeZone currentZone = TimeZone.getDefault();
//        ZonedDateTime zoneConvertedTime = eventTimeCreatorZone.withZoneSameInstant(currentZone.toZoneId());
//        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMM d, yyyy|h:mm a");
//
//        String eventTimeString = zoneConvertedTime.format(dateFormat);

        return eventTimeString;
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
    public void setEventTimeCreatorZone(ZonedDateTime eventTimeCreatorZone) {
        this.eventTimeCreatorZone = eventTimeCreatorZone;
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
