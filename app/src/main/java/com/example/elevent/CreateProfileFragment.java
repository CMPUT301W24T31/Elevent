package com.example.elevent;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class CreateProfileFragment extends Fragment {

    // UI Components
    private EditText nameEditText;
    private EditText contactEditText;
    private EditText homepageEditText;
    private Switch locationSwitch;
    private ImageButton sendButton;
    private ImageButton profileImageButton; // Add this line for the image button

    // Add this ActivityResultLauncher as a member variable
    private ActivityResultLauncher<Intent> getContent;

    // Required empty public constructor
    public CreateProfileFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize your ActivityResultLauncher
        getContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                if (result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();

                    if (selectedImageUri != null) {
                        profileImageButton.setImageURI(selectedImageUri);
                        // Optionally, if you want to handle large images or do image processing, you may need to use a library like Glide or Picasso
                        // You can update an ImageView with this Uri or handle it as needed

                    }
                }
            }
        });
    }

    //@SuppressLint("MissingInflatedId")
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_proflie_page, container, false);

        // Initialize your UI components here
        nameEditText = view.findViewById(R.id.name);
        contactEditText = view.findViewById(R.id.contact);
        homepageEditText = view.findViewById(R.id.homepage);
        locationSwitch = view.findViewById(R.id.location_switch);
        sendButton = view.findViewById(R.id.imageButton2);
        profileImageButton = view.findViewById(R.id.imageButton); // This should be the ID of your image button

        // Handle the send button click
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // existing send button logic...
                // Here you would collect the data from the fields
                String name = nameEditText.getText().toString();
                String contact = contactEditText.getText().toString();
                String homepage = homepageEditText.getText().toString();
                boolean isLocationEnabled = locationSwitch.isChecked();

                // You can then use this data to create a profile, save it or send it to your server, etc.
                // ...


                // After saving the data, you can navigate to the next Fragment or Activity
                // For example, navigate back to the MainActivity content
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).createProfile(name,contact,homepage);
                    ((MainActivity) getActivity()).navigateToMainContent();
                }
            }

        });

        // Set the OnClickListener for your image button
        profileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to pick an image
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*"); // Set MIME Type to image
                getContent.launch(intent); // Launch the Intent
            }
        });

        return view;
    }
}
