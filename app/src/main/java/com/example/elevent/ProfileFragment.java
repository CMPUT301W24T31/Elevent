package com.example.elevent;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

import com.avatarfirst.avatargenlib.AvatarConstants;
import com.avatarfirst.avatargenlib.AvatarGenerator;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

        TextView profileName = view.findViewById(R.id.profile_name);
        TextView profileHomepage = view.findViewById(R.id.profile_homepage);
        TextView profileContact = view.findViewById(R.id.profile_contact);
        ImageView profileImage = view.findViewById(R.id.profile_image);
        String userID = getUserIdFromPreferences();
        FirebaseFirestore db = new UserDBConnector().getDb();
        db.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    User user = documentSnapshot.toObject(User.class);
                    if (user!=null){
                        if (user.getName() != null) {
                            profileName.setText(user.getName());
                        } else {
                            profileName.setVisibility(View.GONE);
                        }
                        if (user.getHomePage() != null){
                            profileHomepage.setText(user.getHomePage());
                        } else {
                            profileHomepage.setVisibility(View.GONE);
                        }
                        if (user.getContact() != null) {
                            profileContact.setText(user.getContact());
                        } else {
                            profileContact.setVisibility(View.GONE);
                        }
                        Blob profilePic = user.getProfilePic();
                        if (profilePic != null) {
                            byte[] profileBA = user.getProfilePic().toBytes();
                            Bitmap profileBitmap = BitmapFactory.decodeByteArray(profileBA, 0, profileBA.length);
                            profileImage.setImageBitmap(profileBitmap);
                        }
                    }
                }
            }
        });


        return view;

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String userID = getUserIdFromPreferences();
        FirebaseFirestore userDB = new UserDBConnector().getDb();
        view.findViewById(R.id.edit_profile_button).setOnClickListener(v -> {
            userDB.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        // navigate to EditProfileFragment
                        MainActivity mainActivity = (MainActivity) getActivity();
                        assert mainActivity != null;
                        FragmentManagerHelper helper = mainActivity.getFragmentManagerHelper();
                        EditProfileFragment editProfileFragment = new EditProfileFragment();
                        Bundle args = new Bundle();
                        args.putSerializable("user", user);
                        editProfileFragment.setArguments(args);
                        helper.replaceFragment(editProfileFragment);
                    }
                }
            });
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