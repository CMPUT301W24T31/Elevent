package com.example.elevent;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.avatarfirst.avatargenlib.AvatarConstants;
import com.avatarfirst.avatargenlib.AvatarGenerator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
/*
    This file contains the implementation for the UI that allows a user to edit their profile
 */

/**
 * This fragment provides the functionalities for allowing a user to edit their profile
 */
public class EditProfileFragment extends Fragment {
    private User user;
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            getProfilePhotoImage();
        }
    });
    private ActivityResultLauncher<String> getContentLauncher;

    /**
     * Called to do initial creation of a fragment
     * Gets the user object to be edited
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            user = getArguments().getParcelable("user");
        }
    }

    /**
     * Called to have the fragment instantiate its user interface view
     * Instantiates the UI features that accept user input for editing their profile
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

        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        EditText editProfileName = view.findViewById(R.id.edit_profile_name);
        EditText editProfileHomepage = view.findViewById(R.id.edit_profile_homepage);
        EditText editProfileContact = view.findViewById(R.id.edit_profile_contact);
        ImageView editProfileImage = view.findViewById(R.id.edit_profile_image);

        editProfileName.setText(user.getName());
        editProfileHomepage.setText(user.getHomePage());
        editProfileContact.setText(user.getContact());
        editProfileImage.setImageBitmap(convertBlobToBitmap(user.getProfilePic()));

        return view;
    }

    /**
     * Called immediately after onCreateView has returned, but before any saved state has been restored in to the view
     * Initialize UI to allow user to edit their profile
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Load existing user data
        EditText editProfileName = view.findViewById(R.id.edit_profile_name);
        EditText editProfileHomepage = view.findViewById(R.id.edit_profile_homepage);
        EditText editProfileContact = view.findViewById(R.id.edit_profile_contact);
        ImageView editProfileImage = view.findViewById(R.id.edit_profile_image);
        TextView editProfileImageText = view.findViewById(R.id.edit_profile_photo_text);
        TextView deleteProfileImageText = view.findViewById(R.id.delete_profile_photo_text);
        if (user.getHasGeneratedPFP()){
            deleteProfileImageText.setVisibility(View.INVISIBLE);
        }
        getContentLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                    if (uri != null) {
                        // OpenAI, 2024, ChatGPT, Convert to byte array
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                            byte[] profilePhotoByteArray = byteArrayOutputStream.toByteArray();
                            user.setProfilePic(Blob.fromBytes(profilePhotoByteArray));
                            user.setHasGeneratedPFP(false);
                            editProfileImage.setImageBitmap(bitmap);
                            deleteProfileImageText.setVisibility(View.VISIBLE);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
        editProfileImageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        });
        deleteProfileImageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable generatedPFP;
                if (user.getName()!= null){
                    generatedPFP = AvatarGenerator.Companion.avatarImage(
                            requireContext(),
                            200,
                            AvatarConstants.Companion.getRECTANGLE(),
                            user.getName()
                    );
                } else {
                    generatedPFP = AvatarGenerator.Companion.avatarImage(
                            requireContext(),
                            50,
                            AvatarConstants.Companion.getRECTANGLE(),
                            "Elevent"
                    );
                }
                Bitmap generatedPFPBitmap = generatedPFP.getBitmap();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                generatedPFPBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                byte[] generatedPFPBA = outputStream.toByteArray();
                Blob generatedPFPBlob = Blob.fromBytes(generatedPFPBA);
                user.setProfilePic(generatedPFPBlob);
                user.setHasGeneratedPFP(true);
                editProfileImage.setImageBitmap(generatedPFPBitmap);
                deleteProfileImageText.setVisibility(View.INVISIBLE);
            }
        });
        view.findViewById(R.id.save_edited_profile_button).setOnClickListener(v -> {

            user.setName(editProfileName.getText().toString());
            if (!editProfileName.getText().toString().isEmpty()){
                if (user.getHasGeneratedPFP()){
                    BitmapDrawable generatedPFP = AvatarGenerator.Companion.avatarImage(
                            requireContext(),
                            200,
                            AvatarConstants.Companion.getRECTANGLE(),
                            editProfileName.getText().toString()
                    );
                    Bitmap generatedPFPBitmap = generatedPFP.getBitmap();
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    generatedPFPBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    byte[] generatedPFPBA = outputStream.toByteArray();
                    Blob generatedPFPBlob = Blob.fromBytes(generatedPFPBA);
                    user.setProfilePic(generatedPFPBlob);
                    editProfileImage.setImageBitmap(generatedPFPBitmap);
                }
            }
            user.setHomePage(editProfileHomepage.getText().toString());
            user.setContact(editProfileContact.getText().toString());

            UserDB userDB = new UserDB();
            userDB.updateUser(user);

            try {
                userDB.updateUser(user);
                Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
            } catch (Exception e){
                Toast.makeText(requireContext(), "Profile could not be updated", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Get the userID from preferences
     * @param blob blob ot be converted
     * @return the resulting bitmap
     */
    private Bitmap convertBlobToBitmap(Blob blob){
        byte[] bytes = blob.toBytes();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * Launches the content launcher to allow users to upload profile photos
     */
    private void getProfilePhotoImage(){getContentLauncher.launch("image/*");}
}
