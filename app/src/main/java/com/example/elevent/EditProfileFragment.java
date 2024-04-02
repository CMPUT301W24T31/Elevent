package com.example.elevent;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.Arrays;

public class EditProfileFragment extends Fragment {

    private EditText editProfileName;
    private EditText editProfileHomepage;
    private EditText editProfileContact;
    private ImageView editProfileImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        editProfileName = view.findViewById(R.id.edit_profile_name);
        editProfileHomepage = view.findViewById(R.id.edit_profile_homepage);
        editProfileContact = view.findViewById(R.id.edit_profile_contact);
        editProfileImage = view.findViewById(R.id.edit_profile_image);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String userID = getUserIdFromPreferences();
        UserDB db = new UserDB(new UserDBConnector());

        // Load existing user data
        db.readUser(userID, new UserDB.OnUserReadListener() {
            @Override
            public void onSuccess(User user) {

                if (user != null) {
                    editProfileName.setText(user.getName());
                    editProfileHomepage.setText(user.getHomePage());
                    editProfileContact.setText(user.getContact());

                    if ((user.getProfilePic() != null)) {
                        Glide.with(EditProfileFragment.this)
                                .load(user.getProfilePic())
                                .placeholder(R.drawable.default_profile_pic)
                                .into(editProfileImage);
                    } else {
                        editProfileImage.setImageResource(R.drawable.default_profile_pic);
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("EditProfileFragment", "Error fetching user data", e);
            }
        });


        view.findViewById(R.id.save_edited_profile_button).setOnClickListener(v -> {

            String name = editProfileName.getText().toString();
            String homepage = editProfileHomepage.getText().toString();
            String contactInputted = editProfileContact.getText().toString();
            byte[] profilePic = null;
            User updatedUser = new User(name, contactInputted, profilePic, homepage, userID);

            db.updateUser(updatedUser).thenRun(() -> {
                Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show();

            }).exceptionally(e -> {
                Toast.makeText(getActivity(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                return null;
            });
        });
    }

    // get the userID from preferences
    private String getUserIdFromPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("userID", null);
    }

}
