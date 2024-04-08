package com.example.elevent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class UserIDLoginFragment extends Fragment {

    private TextView userIdTextView;
    private ImageButton loginButton;

    public UserIDLoginFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.userid_login, container, false);

        userIdTextView = view.findViewById(R.id.typeUserID);
        loginButton = view.findViewById(R.id.nextToMain);

        loginButton.setOnClickListener(v -> {
            String userId = userIdTextView.getText().toString().trim();

            // Check for empty input
            if (userId.isEmpty()) {
                // Show a toast message asking the user to enter something
                Toast.makeText(getContext(), "No input detected. Please enter a user ID.", Toast.LENGTH_SHORT).show();
            } else {
                // Input is not empty, proceed with user login process
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).processUserLogin(userId);
                } else {
                    // Handle the unlikely case of fragment not being attached to MainActivity
                    Toast.makeText(getContext(), "Operation not supported in the current context", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
