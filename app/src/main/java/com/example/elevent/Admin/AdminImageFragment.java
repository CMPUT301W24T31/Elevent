package com.example.elevent.Admin;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.avatarfirst.avatargenlib.AvatarConstants;
import com.avatarfirst.avatargenlib.AvatarGenerator;
import com.example.elevent.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
    This file contains the implementation of the AdminImageFragment, which allows an admin to browse and delete images
 */

/**
 * A fragment that uses a RecyclerView to display images and names from Firestore.
 * It handles data fetching from Firestore, converting Firestore Blobs to Bitmaps, and populating a RecyclerView with the data.
 */
public class AdminImageFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView imageRecyclerView;
    private ImageAdapter imageAdapter;
    private List<Image> imageList;

    /**
     * Required empty public constructor
     */
    public AdminImageFragment() {}

    /**
     * Called to have the fragment instantiate its user interface view
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or nul
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_image_view, container, false);

        imageList = new ArrayList<>();
        imageRecyclerView = view.findViewById(R.id.imagesRecyclerView);
        imageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        imageAdapter = new ImageAdapter(imageList, new ImageAdapter.OnImageLongClickListener() {
            @Override
            public void onImageLongClick(Image image, int position) {
                showDeleteDialog(image, position);
            }
        });
        imageRecyclerView.setAdapter(imageAdapter);

        fetchEventAndUserImages();

        return view;
    }
    /**
     * Shows a confirmation dialog before deleting an image.
     * @param image The image to be deleted.
     * @param position The position of the image in the adapter.
     */
    private void showDeleteDialog(Image image, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Image");
        builder.setMessage("Note: This only deletes the image and not the user/event. Continue?");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            deleteImage(image, position);
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    /**
     * Deletes an image from Firestore by setting its corresponding field to null.
     * @param image The image to be deleted.
     * @param position The position of the image in the adapter.
     */
    private void deleteImage(Image image, int position) {
        String collectionPath = image.isEvent() ? "events" : "users";
        String documentId = image.getDocumentId();
        String fieldToUpdate = image.isEvent() ? "eventPoster" : "profilePic";

        Map<String, Object> updates = new HashMap<>();
        if (image.isEvent()) {
            updates.put(fieldToUpdate, null);
        } else {
            BitmapDrawable generatedPFP;
            if (image.getName() != null) {
                generatedPFP = AvatarGenerator.Companion.avatarImage(
                        requireContext(),
                        200,
                        AvatarConstants.Companion.getRECTANGLE(),
                        String.valueOf(image.getName())
                );
            } else {
                generatedPFP = AvatarGenerator.Companion.avatarImage(
                        requireContext(),
                        200,
                        AvatarConstants.Companion.getRECTANGLE(),
                        "Elevent"
                );
            }
            Bitmap generatedPFPBitmap = generatedPFP.getBitmap();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            generatedPFPBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] generatedPFPBA = outputStream.toByteArray();
            Blob generatedPFPBlob = Blob.fromBytes(generatedPFPBA);
            updates.put(fieldToUpdate, generatedPFPBlob);
            updates.put("hasGeneratedPFP", true);
        }

        db.collection(collectionPath).document(documentId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d("AdminImageFragment", "Image field set to null successfully.");
                    imageList.remove(position);
                    imageAdapter.notifyItemRemoved(position);
                    // Show success toast
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Image removed successfully.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("AdminImageFragment", "Error setting image field to null", e);
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Failed to remove image.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * Fetches images from both the events and users collections in Firestore.
     */
    private void fetchEventAndUserImages() {
        fetchImages("events", true);
        fetchImages("users", false);
    }

    /**
     * Fetches images from a specified Firestore collection.
     *
     * @param collectionPath The path of the collection to fetch from.
     * @param isEvent Indicates whether the fetched images are for events (true) or users (false).
     */
    private void fetchImages(String collectionPath, boolean isEvent) {
        db.collection(collectionPath).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String blobField = isEvent ? "eventPoster" : "profilePic";
                        if (document.contains(blobField) && document.getBlob(blobField) != null) {
                            byte[] imageBytes = document.getBlob(blobField).toBytes();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                            String name = document.getString(isEvent ? "eventName" : "name");
                            String documentId = document.getId();
                            if (bitmap != null && name != null) {
                                imageList.add(new Image(bitmap, name, isEvent, documentId));
                            }
                        }
                    }
                    imageAdapter.notifyDataSetChanged();
                } else {
                    Log.e("AdminImageFragment", "Error fetching images from " + collectionPath, task.getException());
                }
            }
        });
    }
}
