package com.example.elevent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class UserIDLoginFragment extends Fragment {

    // UI Components
    private TextView userIdTextView;
    private ImageButton loginButton;

    // Required empty public constructor
    public UserIDLoginFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.userid_login, container, false);

        // Initialize your UI components here
        userIdTextView = view.findViewById(R.id.typeUserID);
        loginButton = view.findViewById(R.id.nextToMain);

        // Handle the login button click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Here you would capture the user ID entered by the user
                String userId = userIdTextView.getText().toString();

                // TODO: Validate the userID and perform login operation

                // After login, navigate to the next Fragment or Activity
                // For example:
                // if (getActivity() instanceof MainActivity) {
                //     ((MainActivity) getActivity()).proceedToNextSection(userId);
                // }
            }
        });

        return view;
    }

    // TODO: Optionally add a method to handle the login logic, if not handled by MainActivity
}
