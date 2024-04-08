package com.example.elevent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;

import java.util.Map;
/*
    This file contains the implementation of displaying the map for an organizer to see where attendees
    have checked in from
 */

/**
 * This fragment contains the UI of the map for an organizer to see where attendees have checked in from
 */
public class MapFragment extends Fragment {

    private Event event;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            createMarkers(googleMap);
//            LatLng sydney = new LatLng(-34, 151);
//            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
    };

    /**
     * Required empty public constructor
     */
    public MapFragment(){}

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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    /**
     * Called immediately after onCreateView has returned, but before any saved state has been restored in to the view
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try{
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (getArguments() != null){
            event = getArguments().getParcelable("event");
        }
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        } catch (Exception e) {
            showErrorFragment();
        }
    }

    /**
     * Catches exceptions an displays an error page
     */
    private void showErrorFragment() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_main_framelayout, new ErrorFragment())
                .commit();
    }

    /**
     * Marks the check in locations on the map
     * @param googleMap
     */
    public void createMarkers(GoogleMap googleMap) {
        UserDB db = new UserDB();
        for (Map.Entry<String, GeoPoint> e : event.getCheckInLocations().entrySet()) {
            LatLng l = new LatLng(e.getValue().getLatitude(), e.getValue().getLongitude());
            String userID = e.getKey();
            db.readUser(userID, new UserDB.OnUserReadListener() {
                @Override
                public void onSuccess(User user) {
                    // update with user data in the UI
                    if (getActivity() == null) return;
                    getActivity().runOnUiThread(() -> {
                        String t = user.getName();
                        if (!t.isEmpty()) {
                            googleMap.addMarker(new MarkerOptions().position(l).title(t));
                        } else {
                            googleMap.addMarker(new MarkerOptions().position(l));
                        }

                    });
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("ProfileFragment", "Error fetching user data", e);
                }
            });
//            googleMap.addMarker(new MarkerOptions().position(l).title(t.get()));
//            googleMap.addMarker(new MarkerOptions().position(l).title("test"));
        }
    }
}