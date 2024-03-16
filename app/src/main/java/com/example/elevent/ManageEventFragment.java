package com.example.elevent;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        Button notifCentreButton = view.findViewById(R.id.notif_centre_button);
        notifCentreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //did fragment switching using fragment helper, creates instance of main to tie with the fragment to enable switching
                //(same implementation as the random floating button in all events :))
                if (getActivity() instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    FragmentManagerHelper helper = mainActivity.getFragmentManagerHelper();
                    helper.replaceFragment(new NotificationCentreFragment());
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
    private void fetchAttendees(){
        EventDBConnector connector = new EventDBConnector();
        FirebaseFirestore db = connector.getDb();

        db.collection("events").document(event.getEventName()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // gotta figure out the firebase cuz idk how that works rn
                    String[] signedUpAttendees = (String[]) documentSnapshot.get("signedUpAttendees");
                    if (signedUpAttendees != null){
                        ArrayList<String> attendees = new ArrayList<>(Arrays.asList(signedUpAttendees));
                        updateListView(attendees);
                    }
                } else{
                    Log.d("fetchAttendees", "No such document");
                }
            }
        });
    }
    private void updateListView(ArrayList<String> attendees){
        AttendeeArrayAdapter attendeeArrayAdapter = new AttendeeArrayAdapter(requireActivity(), attendees);
        listOfAttendees = getView().findViewById(R.id.list_of_attendees);
        listOfAttendees.setAdapter(attendeeArrayAdapter);
    }
}