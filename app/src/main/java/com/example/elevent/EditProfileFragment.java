package com.example.elevent;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class EditProfileFragment extends Fragment {
    private User user;
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            getProfilePhotoImage();
        }
    });
    private ActivityResultLauncher<String> getContentLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            user = (User) getArguments().getSerializable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        EditText editProfileName = view.findViewById(R.id.edit_profile_name);
        EditText editProfileHomepage = view.findViewById(R.id.edit_profile_homepage);
        EditText editProfileContact = view.findViewById(R.id.edit_profile_contact);
        ImageView editProfileImage = view.findViewById(R.id.edit_profile_image);
        TextView deleteProfileImageText = view.findViewById(R.id.delete_profile_photo_text);

        editProfileName.setText(user.getName());
        editProfileHomepage.setText(user.getHomePage());
        editProfileContact.setText(user.getContact());
        if (user.getProfilePic() != null){
            editProfileImage.setImageBitmap(convertBlobToBitmap(user.getProfilePic()));
        } else {
            editProfileImage.setImageResource(R.drawable.default_profile_pic);
            deleteProfileImageText.setVisibility(View.INVISIBLE);
        }

        return view;
    }

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
        getContentLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                    if (uri != null) {
                        // OpenAI, 2024, ChatGPT, Convert to byte array
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                            byte[] profilePhotoByteArray = byteArrayOutputStream.toByteArray();
                            user.setProfilePic(Blob.fromBytes(profilePhotoByteArray));
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
                user.setProfilePic(null);
                editProfileImage.setImageResource(R.drawable.default_profile_pic);
                deleteProfileImageText.setVisibility(View.INVISIBLE);
            }
        });
        view.findViewById(R.id.save_edited_profile_button).setOnClickListener(v -> {

            user.setName(editProfileName.getText().toString());
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

    // get the userID from preferences
    private Bitmap convertBlobToBitmap(Blob blob){
        byte[] bytes = blob.toBytes();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
    private void getProfilePhotoImage(){getContentLauncher.launch("image/*");}
}
