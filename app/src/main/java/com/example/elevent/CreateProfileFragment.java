    package com.example.elevent;

    import android.annotation.SuppressLint;
    import android.app.AlertDialog;
    import android.content.ContentResolver;
    import android.content.Intent;
    import android.net.Uri;
    import android.os.Bundle;
    import android.os.Environment;
    import android.provider.MediaStore;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.EditText;
    import android.widget.ImageButton;
    import android.widget.Switch;
    import androidx.activity.result.ActivityResultLauncher;
    import androidx.activity.result.contract.ActivityResultContracts;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.content.FileProvider;
    import androidx.fragment.app.Fragment;

    import java.io.ByteArrayOutputStream;
    import java.io.File;
    import java.io.IOException;
    import java.io.InputStream;
    import java.text.SimpleDateFormat;
    import java.util.Date;
    import java.util.Locale;

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


        private Uri selectedImageUri = null;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // For selecting image from gallery
            getContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    if (result.getData() != null) {
                        selectedImageUri = result.getData().getData(); // Update the member variable
                        if (selectedImageUri != null) {
                            profileImageButton.setImageURI(selectedImageUri);
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

                    if (selectedImageUri != null) {
                        byte[] imageData = uriToByteArray(selectedImageUri);
                        // After saving the data, you can navigate to the next Fragment or Activity
                        // For example, navigate back to the MainActivity content
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).createProfile(name, contact, homepage,imageData);
                            ((MainActivity) getActivity()).navigateToMainContent();
                        }
                    }
                }

            });

            // Set the OnClickListener for your image button
            profileImageButton.setOnClickListener(v -> showImageSourceChoiceDialog());

            return view;
        }
        private void showImageSourceChoiceDialog() {
            CharSequence[] options = {"Choose from Gallery", "Cancel"};

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Choose your profile picture");

            builder.setItems(options, (dialog, which) -> {

                    // Choose from Gallery
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    getContent.launch(intent);

            });
            builder.show();
        }
        // Method to convert Uri to byte array
        private byte[] uriToByteArray(Uri uri) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ContentResolver contentResolver = getContext().getContentResolver();
            try {
                InputStream inputStream = contentResolver.openInputStream(uri);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, len);
                }
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return byteArrayOutputStream.toByteArray();
        }


    }
