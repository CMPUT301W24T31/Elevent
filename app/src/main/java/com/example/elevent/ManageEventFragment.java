package com.example.elevent;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
/*
    This file is responsible for implementing the ManageEventFragment that displays the UI that allows the organizer to view the list of attendees
    and handle notifications
    Outstanding issues: notifications are buggy
 */
/**
 * This fragment provides the organizer to manage their event
 * Allows the organizer to view the list of attendees and push notifications to attendees
 */
public class ManageEventFragment extends Fragment {

    /**
     *
     */
    interface ManageEventListener {
        //void onCreateEvent(Event event);

        void onPositiveClick(Event event);
    }

    private ManageEventFragment.ManageEventListener listener;

    /**
     * Called when a fragment is first attached to its host activity
     * @param context Host activity
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ManageEventFragment.ManageEventListener) {
            listener = (ManageEventFragment.ManageEventListener) context;
        } else {
            throw new RuntimeException(context + " must implement ManageEventListener");
        }
    }

    private Event event;
    private TextView attendeeListTextView;
    private ListView listOfAttendees;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            event = (Event) getArguments().getSerializable("event");
        }
    }

    /**
     * Called immediately after has returned, but before any saved state has been restored in to the view.
     * Initialize list of attendees
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return View for the fragment's UI, or null
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manageevent, container, false);

        // Find views by their respective IDs
        attendeeListTextView = view.findViewById(R.id.attendee_list_textview);
        listOfAttendees = view.findViewById(R.id.list_of_attendees);
        return view;
    }

    /**
     * Called immediately after has returned, but before any saved state has been restored in to the view.
     * Initialize UI to allow organizer to handle notifications
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Spinner filterStatus = view.findViewById(R.id.attendee_spinner);

        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.attendee_filter_spinner_array,
                android.R.layout.simple_spinner_item
        );
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterStatus.setAdapter(filterAdapter);
        filterStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (Objects.equals(selection, "checked-in")){
                    fetchCheckedInAttendees();
                } else if (Objects.equals(selection, "signed-up")){
                    fetchSignedUpAttendees();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                fetchSignedUpAttendees();
            }
        });
        Button notifCentreButton = view.findViewById(R.id.notif_centre_button);
        Button mapButton = view.findViewById(R.id.map_button);
        notifCentreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //did fragment switching using fragment helper, creates instance of main to tie with the fragment to enable switching
                //(same implementation as the random floating button in all events :))
                if (getActivity() instanceof MainActivity) {
                    NotificationCentreFragment notificationCentreFragment = new NotificationCentreFragment();
                    Bundle args = new Bundle();
                    args.putSerializable("event", event);
                    notificationCentreFragment.setArguments(args);
                    MainActivity mainActivity = (MainActivity) getActivity();
                    FragmentManagerHelper helper = mainActivity.getFragmentManagerHelper();
                    helper.replaceFragment(notificationCentreFragment);
                }
                //return null;
            }
        });
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //did fragment switching using fragment helper, creates instance of main to tie with the fragment to enable switching
                //(same implementation as the random floating button in all events :))
                if (getActivity() instanceof MainActivity) {
                    MapFragment mapFragment = new MapFragment();
                    Bundle args = new Bundle();
                    args.putSerializable("event", event);
                    mapFragment.setArguments(args);
                    MainActivity mainActivity = (MainActivity) getActivity();
                    FragmentManagerHelper helper = mainActivity.getFragmentManagerHelper();
                    helper.replaceFragment(mapFragment);
                }
                //return null;
            }
        });

        fetchAttendees();
    }

        // You can also set data to your TextView and ListView
        // attendeeListTextView.setText("Attendees List");
        // Set adapter to ListView
        // Example: listOfAttendees.setAdapter(yourAdapter);
    private void fetchSignedUpAttendees(){
        EventDBConnector connector = new EventDBConnector();
        FirebaseFirestore db = connector.getDb();

        db.collection("events").document(event.getEventID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // gotta figure out the firebase cuz idk how that works rn
                    ArrayList<String> signedUpAttendees = (ArrayList<String>) documentSnapshot.get("signedUpAttendees");
                    if (signedUpAttendees != null) {
                        fetchUserObjects(signedUpAttendees);
                    }
                } else{
                    Log.d("fetchAttendees", "No such document");
                }
            }
        });
    }
    private void fetchCheckedInAttendees(){
        EventDBConnector connector = new EventDBConnector();
        FirebaseFirestore db = connector.getDb();

        db.collection("events").document(event.getEventID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Integer> checkedInAttendees = (Map<String, Integer>) documentSnapshot.get("checkedInAttendees");
                    if (checkedInAttendees != null){
                        List<String> checkedInAttendeesIDs = new ArrayList<>(checkedInAttendees.keySet());
                        fetchUserObjects(checkedInAttendeesIDs);
                    }
                } else {
                    Log.d("fetchCheckedInAttendees", "Document does not exist");
                }
            }
        });
    }
    private void fetchUserObjects(List<String> attendeeIDs){
        UserDBConnector connector = new UserDBConnector();
        FirebaseFirestore db = connector.getDb();

        ArrayList<User> attendees = new ArrayList<>();
        if (attendeeIDs.size() == 0){
            updateListView(attendees);
        }
        AtomicInteger count = new AtomicInteger(attendeeIDs.size());
        for (String userID : attendeeIDs){
            db.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()){
                        User user = documentSnapshot.toObject(User.class);
                        attendees.add(user);
                    } else {
                        Log.d("fetchUserObjects", "Document does not exist");
                    }
                    if (count.decrementAndGet() == 0){
                        updateListView(attendees);
                    }
                }
            });
        }
    }
    private void updateListView(ArrayList<User> attendees){
        AttendeeArrayAdapter attendeeArrayAdapter = new AttendeeArrayAdapter(requireActivity(), attendees);
        listOfAttendees = getView().findViewById(R.id.list_of_attendees);
        listOfAttendees.setAdapter(attendeeArrayAdapter);
    }
}