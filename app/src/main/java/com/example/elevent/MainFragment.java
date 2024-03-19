package com.example.elevent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainFragment extends Fragment {

    BottomNavigationView navigationView;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize navigation
        initNavView(view);
    }

    private void initNavView(View view) {
        navigationView = view.findViewById(R.id.activity_main_navigation_bar);
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId() == R.id.nav_bar_allevents) {
                        Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_allEventsFragment);
                        return true;
                    }else if (item.getItemId() == R.id.nav_bar_myevents) {
                        Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_myEventsFragment);
                        return true;
                    }else if (item.getItemId() == R.id.nav_bar_scanner) {
                        Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_scannerFragment);
                        return true;
                    }else if (item.getItemId() == R.id.nav_bar_profile) {
                        Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_profileFragment);
                        return true;
                    }
                    return false;
            }
        });
    }
}
