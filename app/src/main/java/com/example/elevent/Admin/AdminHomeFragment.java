package com.example.elevent.Admin;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.elevent.MainActivity;
import com.example.elevent.R;
/*
    This file contains the implementation of the AdminHomeFragment, which displays the admin's home screen
 */
/**
 * A Fragment representing the admin home screen with navigation options.
 */
public class AdminHomeFragment extends Fragment {

    /**
     * Default constructor. Required for Fragment subclass.
     */
    public AdminHomeFragment() {
        //empty public constructor
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
     * @return Return the View for the fragment's UI, or null
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_homepage, container, false);
    }

    /**
     * Called immediately after onCreateView has returned, but before any saved state has been restored in to the view
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //buttons
        Button eventsButton = view.findViewById(R.id.admin_events_button);
        Button profilesButton = view.findViewById(R.id.admin_profiles_button);
        Button imagesButton = view.findViewById(R.id.admin_images_button);

        //click listeners
        eventsButton.setOnClickListener(v -> navigateTo(new AdminEventFragment(), "Remove Events"));
        imagesButton.setOnClickListener(v -> navigateTo(new AdminImageFragment(), "Remove Images"));
        profilesButton.setOnClickListener(v -> navigateTo(new AdminProfilesFragment(), "Remove Profiles"));
    }

    /**
     * Called when the fragment is visible to the user and actively running
     */
    @Override
    public void onResume() {
        super.onResume();
        // update the app bar title when navigating back to the AdminHomeFragment
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateAppBarTitle("Admin Home");
        }
    }
    /**
     * Navigates to the specified fragment, updating the app bar title.
     *
     * @param fragment The fragment to navigate to.
     * @param title    The title to set on the app bar.
     */
    private void navigateTo(Fragment fragment, String title) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateAppBarTitle(title);
        }

        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction()
                    .replace(((ViewGroup) getView().getParent()).getId(), fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
