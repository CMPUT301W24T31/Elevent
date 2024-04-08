package com.example.elevent.Admin;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.elevent.MainActivity;
import com.example.elevent.R;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_homepage, container, false);
    }

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
