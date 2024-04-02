package com.example.elevent;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Arrays;
/*
    This file contains the implementation for the ProfileFragment that is responsible for displaying the UI
    that allows a user to view and edit their personal profile
    Outstanding issues: needs to be implemented
 */
/**
 * This fragment provides UI for the user to customize their profile
 */
public class ProfileFragment extends Fragment {

    // attributes used in the profile fragment
    private EditText profileName;
    private EditText profileHomepage;
    private EditText profileContact;
    private ImageView profileImage;
    private Button saveProfileButton;
    private String getUserIdFromPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("userID", null);
    }

    /**
     * Required empty public constructor
     */
    public ProfileFragment() {
    }

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
     * @return View for the UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // create a view and inflate the layout
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileName = view.findViewById(R.id.profile_name);
        profileHomepage = view.findViewById(R.id.profile_homepage);
        profileContact = view.findViewById(R.id.profile_contact);
        profileImage = view.findViewById(R.id.profile_image);
        saveProfileButton = view.findViewById(R.id.save_profile_button);

        return view;

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get the UserID and instantiate an instance
        // of the user database
        String userID = getUserIdFromPreferences();
        UserDB db = new UserDB(new UserDBConnector());


        db.readUser(userID, new UserDB.OnUserReadListener() {
            @Override
            public void onSuccess(User user) {
                // update with user data in the UI
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    profileName.setText(user.getName()); //update the profile name with
                    profileHomepage.setText(user.getHomePage());
                    profileContact.setText(user.getContact());
                    // i don't know how to handle the profile picture yet
                });
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("ProfileFragment", "Error fetching user data", e);
            }
        });

        saveProfileButton.setOnClickListener(v -> {
            String name = profileName.getText().toString();
            String homepage = profileHomepage.getText().toString();
            String contact = profileContact.getText().toString();

            byte[] profilePic = null; // placeholder for the actual conversion logic we have to do

            User updatedUser = new User(name, contact, null, homepage, userID); // Adjust according to your User constructor
            db.updateUser(updatedUser).thenRun(() -> {
                Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
            }).exceptionally(e -> {
                Toast.makeText(getActivity(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                return null;
            });
        });
    }

    public void displayProfilePicture(byte[] profilePic) {
        if (profilePic != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(profilePic, 0, profilePic.length);
            profileImage.setImageBitmap(bitmap);
        } else {
            // Set default or placeholder image
            profileImage.setImageResource(R.drawable.default_profile_pic);
        }
    }


}