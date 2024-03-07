package com.example.elevent;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class EventDB {

    // initialize an instance of the firestore database
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    public EventDB(EventDBConnector connector) {
        this.db = connector.getDb();
        this.storage = connector.getStorage();
    }

    // Openai, ChatGPT, 2024, How do I convert Bitmaps to byte and then return a URL
    private CompletableFuture<String> uploadImage(Bitmap image, String path) {

        CompletableFuture<String> future = new CompletableFuture<>();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        StorageReference imageRef = storage.getReference().child("event_images");
        imageRef.putBytes(imageData)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return imageRef.getDownloadUrl();
                })
                .addOnSuccessListener(downloadUri -> future.complete(downloadUri.toString()))
                .addOnFailureListener(future::completeExceptionally);

        return future;
    }

    public CompletableFuture<Void> addEvent(Event event) {

        // TODO We need to create a folder to store QRs
        // a future task to upload both types of QRs to Firebase Cloud Storage
        // and set the URLs
        CompletableFuture<String> promoQRUrlFuture = uploadImage(event.getPromotionalQR(), "events/" + event.getEventName() + "/promo_qr.jpg");
        CompletableFuture<String> checkinQRUrlFuture = uploadImage(event.getCheckinQR(), "events/" + event.getEventName() + "/checkin_qr.jpg");

        // TODO adjust event poster upload for images instead of a bit map
        // Currently event poster is not being handled and I am losing my mind
        // finding a way online to do this
        CompletableFuture<String> posterUrlFuture = CompletableFuture.completedFuture(event.getEventPosterUrl());

        // wait for all image uploads to complete before uploading to firestore
        return CompletableFuture.allOf(promoQRUrlFuture, checkinQRUrlFuture, posterUrlFuture)
                .thenRun(() -> {
                    // Set URLs in the event object
                    event.setPromotionalQRUrl(promoQRUrlFuture.join());
                    event.setCheckinQRUrl(checkinQRUrlFuture.join());
                    event.setEventPosterUrl(posterUrlFuture.join()); // Adjust if eventPoster upload is implemented

                    // Now add to Firestore
                    Map<String, Object> eventMap = event.toMap();
                    db.collection("events").document(event.getEventName()).set(eventMap);
                });
    }

    // updateEvent takes in the eventName( which is used as the document name for an event in the
    // database) and then a map of the updates to be made to the event, creating a reference to where
    // the event info is stored, locating it, and updating the info
    public CompletableFuture<Void> updateEvent(String eventName, Map<String, Object> updates) {
        DocumentReference eventRef = db.collection("events").document(eventName);
        return CompletableFuture.runAsync(() -> eventRef.update(updates));
    }


    // delete an event by taking in the eventName, and thus its
    // document name in firestore and deleting it asynchronously (
    // returning the delete and then firestore removes it in the back
    // end)
    public CompletableFuture<Void> deleteEvent(String eventName) {
        DocumentReference eventRef = db.collection("events").document(eventName); // find event in firestore
        return CompletableFuture.runAsync(eventRef::delete); // delete that event
    }

    // remaining methods for events to be implemented
    // -----------
    // -----------
    // -----------
    // -----------
    // -----------
}
