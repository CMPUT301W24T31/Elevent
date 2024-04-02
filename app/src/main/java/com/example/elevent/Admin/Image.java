package com.example.elevent.Admin;

import android.graphics.Bitmap;

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
        this.isEvent = false; // This constructor is used for users, hence isEvent is false
        this.contactInfo = contactInfo;
        this.homepage = homepage;
        this.documentId = documentId;
    }
    public Bitmap getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public boolean isEvent() {
        return isEvent;
    }
    public String getContactInfo() {
        return contactInfo;
    }

    public String getHomepage() {
        return homepage;
    }
    public String getDocumentId(){
        return documentId;
    }
}
