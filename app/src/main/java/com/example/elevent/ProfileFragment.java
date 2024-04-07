package com.example.elevent;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.example.elevent.Admin.AdminHomeFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Firebase;
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
    BottomNavigationView navigationView;

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
        TextView adminNav = view.findViewById(R.id.admin_navigation);
        ImageView profileImage = view.findViewById(R.id.profile_image);

        String userID = getUserIdFromPreferences();
        FirebaseFirestore db = new UserDBConnector().getDb();
        db.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    User user = documentSnapshot.toObject(User.class);
                    if (user!=null){
                        profileName.setText(user.getName());
                        profileHomepage.setText(user.getHomePage());
                        profileContact.setText(user.getContact());

                        Blob profilePic = user.getProfilePic();
                        if (profilePic != null) {
                            System.out.println("here");
                            byte[] profileBA = user.getProfilePic().toBytes();
                            Bitmap profileBitmap = BitmapFactory.decodeByteArray(profileBA, 0, profileBA.length);
                            profileImage.setImageBitmap(profileBitmap);
                        } else {
                            System.out.println("null");
                            profileImage.setImageResource(R.drawable.default_profile_pic);
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
        view.findViewById(R.id.admin_navigation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Admin Key");
                alert.setMessage("Enter the admin key:");

                // Set an EditText view to get user input
                final EditText input = new EditText(getActivity());
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String adminKey = input.getText().toString();
                        // Check if the adminKey is correct
                        if(adminKey.equals("abcd1234")) {
                            MainActivity mainActivity = (MainActivity) getActivity();
                            if (mainActivity != null) {
                                // Assuming the BottomNavigationView ID is navigationView in MainActivity
                                mainActivity.navigationView.setVisibility(View.GONE);
                                FragmentManagerHelper fragmentManagerHelper = new FragmentManagerHelper(mainActivity.getSupportFragmentManager(), R.id.activity_main_framelayout);
                                fragmentManagerHelper.replaceFragment(new AdminHomeFragment());
                                Toast.makeText(mainActivity, "Access granted", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Invalid admin key", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            }
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