package com.example.elevent.Admin;

import android.graphics.Bitmap;
/*
    This file contains the representation of an Image object, associated with an event or a user
 */

import org.checkerframework.checker.units.qual.A;

/**
 * Represents an image associated with an event or a user profile.
 * This class holds the image data, the name associated with the image,
 * additional information like contact info and homepage for user images,
 * and a document ID for database operations.
 */
public class Image {
    private Bitmap image;
    private String name;
    private boolean isEvent;
    private String contactInfo;
    private String homepage;
    private String documentId;

    /**
     * Constructor for event images.
     * @param image The bitmap image.
     * @param name The name of the event.
     * @param isEvent Whether the image is associated with an event (true) or a user (false).
     * @param documentId The Firestore document ID associated with the image.
     */
    public Image(Bitmap image, String name, boolean isEvent, String documentId) {
        this.image = image;
        this.name = name;
        this.isEvent = isEvent;
        this.documentId = documentId;
    }

    /**
     * Constructor for user images.
     * @param image The bitmap image.
     * @param name The name of the user.
     * @param contactInfo Contact information of the user.
     * @param homepage Homepage URL of the user.
     * @param documentId The Firestore document ID associated with the image.
     */
    public Image(Bitmap image, String name, String contactInfo, String homepage, String documentId) {
        this.image = image;
        this.name = name;
        this.isEvent = false; // this constructor is used for users, hence isEvent is false
        this.contactInfo = contactInfo;
        this.homepage = homepage;
        this.documentId = documentId;
    }

    /**
     * Getter for the bitmap representation of the image
     * @return Bitmap representation of the image
     */
    public Bitmap getImage() {
        return image;
    }
    public void setImage(Bitmap image) {
        this.image = image;
    }

    /**
     * Getter for the name of the event or the user
     * @return Name of the event or user
     */
    public String getName() {
        return name;
    }

    /**
     * Returns true if the image belongs to an event, false if it belongs to a user
     * @return True if event image, false if user image
     */
    public boolean isEvent() {
        return isEvent;
    }

    /**
     * Getter for the user's contact info
     * @return User's contact info
     */
    public String getContactInfo() {
        return contactInfo;
    }

    /**
     * Getter for the user's homepage
     * @return User's homepage
     */
    public String getHomepage() {
        return homepage;
    }

    /**
     * Gets the ID of the document to which the image belongs
     * @return ID of document to which image belongs
     */
    public String getDocumentId(){
        return documentId;
    }
}
