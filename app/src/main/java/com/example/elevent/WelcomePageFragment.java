package com.example.elevent;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toolbar;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class WelcomePageFragment extends Fragment {
    // Required empty public constructor
    public WelcomePageFragment() {}

    public interface OnCreateProfileListener {
        void onCreateProfile();
        void onSkipStart();
    }

    private OnCreateProfileListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCreateProfileListener) {
            mListener = (OnCreateProfileListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnCreateProfileListener");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.welcome_page, container, false);

        Button button = view.findViewById(R.id.skip_start); // replace with your button id
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSkipStart();
            }
        });

        // Create Profile button functionality
        Button createProfileButton = view.findViewById(R.id.create_profile); // ID for create profile button
        createProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCreateProfile();
            }
        });

        return view;
    }
    public void navigateToMainContent() {
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();

            // Make sure you have this line to show the navigation again
            mainActivity.showNavigationAndToolbar();

            // Replace WelcomeFragment with the first fragment that you want to show after the welcome screen
            mainActivity.getFragmentManagerHelper().replaceFragment(new AllEventsFragment());
        }
    }


}
