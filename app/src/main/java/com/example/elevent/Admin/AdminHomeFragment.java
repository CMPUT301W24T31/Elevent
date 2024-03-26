package com.example.elevent.Admin;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.elevent.R;

public class AdminHomeFragment extends Fragment {

    public AdminHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.admin_homepage, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button eventsButton = view.findViewById(R.id.admin_events_button);
        Button imagesButton = view.findViewById(R.id.admin_images_button);

        eventsButton.setOnClickListener(v -> navigateTo(new AdminEventFragment()));
        imagesButton.setOnClickListener(v -> navigateTo(new AdminImageFragment()));
    }

    private void navigateTo(Fragment fragment) {
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction()
                    .replace(((ViewGroup)getView().getParent()).getId(), fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}