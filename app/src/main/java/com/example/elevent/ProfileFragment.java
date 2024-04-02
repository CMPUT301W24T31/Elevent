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
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Objects;
/*
    This file contains the implementation for the ProfileFragment that is responsible for displaying the UI
    that allows a user to view their personal profile
 */
/**
 * This fragment provides UI for the user to view their profile
 */
public class ProfileFragment extends Fragment {

    // attributes used in the profile fragment
    private TextView profileName;
    private TextView profileHomepage;
    private TextView profileContact;
    private ImageView profileImage;
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

        return view;

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String userID = getUserIdFromPreferences();
        UserDB db = new UserDB(new UserDBConnector());

        db.readUser(userID, new UserDB.OnUserReadListener() {
            @Override
            public void onSuccess(User user) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {

                    // set the user info in the text views
                    profileName.setText(user.getName());
                    profileHomepage.setText(user.getHomePage());
                    profileContact.setText(user.getContact());

                    if (user.getProfilePic() != null) {
                        Bitmap profileBitmap = BitmapFactory.decodeByteArray(user.getProfilePic(), 0, user.getProfilePic().length);
                        profileImage.setImageBitmap(profileBitmap);
                    } else {
                        profileImage.setImageResource(R.drawable.default_profile_pic);
                    }
                    // i don't know how to handle the profile picture yet
                });
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("ProfileFragment", "Error fetching user data", e);
            }
        });

        view.findViewById(R.id.edit_profile_button).setOnClickListener(v -> {
            // navigate to EditProfileFragment
            MainActivity mainActivity = (MainActivity)getActivity();
            assert mainActivity != null;
            FragmentManagerHelper helper = mainActivity.getFragmentManagerHelper();
            EditProfileFragment editProfileFragment = new EditProfileFragment();
            helper.replaceFragment(editProfileFragment);
        });
    }


    /*
    public void displayProfilePicture(byte[] profilePic) {
        if (profilePic != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(profilePic, 0, profilePic.length);
            profileImage.setImageBitmap(bitmap);
        } else {
            // Set default or placeholder image
            profileImage.setImageResource(R.drawable.default_profile_pic);
        }
    }
     */


}