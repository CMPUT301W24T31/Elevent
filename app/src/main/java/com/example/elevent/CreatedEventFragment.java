package com.example.elevent;

import android.Manifest;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
/*
    This file contains the implementation of the CreatedEventFragment that is responsible for displaying the UI of the organizer's view
    of a created event. The organizer can manage and edit the event in this fragment.
    Outstanding issues: Need to display the QR codes,
 */
/**
 * This fragment displays the organizer's view of an event they created
 * Allows organizer to edit event details
 */
public class CreatedEventFragment extends Fragment {

    /**
     * Listener that handles event creation
     * Implemented by MainActivity
     */
    interface CreatedEventListener {
        //void onCreateEvent(Event event);

        void onPositiveClick(Event event);
    }
    //comment random. 

    private Event event;
    private CreatedEventListener listener;

    /**
     * Called when a fragment is first attached to its host activity
     * @param context Host activity
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CreatedEventListener) {
            listener = (CreatedEventListener) context;
        } else {
            throw new RuntimeException(context + " must implement CreatedEventListener (tapped on event in myEvents)");
        }
    }

    /**
     * Called to do initial creation of a fragment.
     * Gets the selected event in the array adapter
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            event = (Event) getArguments().getSerializable("selected_event");
        }
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * Initializes the UI for editing the event information
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
        View view = inflater.inflate(R.layout.fragment_createdevent, container, false);
        EditText eventName = view.findViewById(R.id.event_name_text);
        EditText eventAddress = view.findViewById(R.id.event_location_text);
        EditText eventTime = view.findViewById(R.id.event_time_text);
        EditText eventDate = view.findViewById(R.id.event_date_text);
        EditText eventDescription = view.findViewById(R.id.event_description_text);
        Button addEventImage = view.findViewById(R.id.eventPoster_image);
        ImageView checkInQR = view.findViewById(R.id.checkinQR_image);
        return view;
    }

    /**
     * Called immediately after onCreateView has returned
     * Initializes option to confirm changes and return to previous fragment
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button manageEventButton = view.findViewById(R.id.manage_the_event);
        manageEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //did fragment switching using fragment helper, creates instance of main to tie with the fragment to enable switching
                //(same implementation as the random floating button in all events :))
                if (getActivity() instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    FragmentManagerHelper helper = mainActivity.getFragmentManagerHelper();
                    helper.replaceFragment(new ManageEventFragment());
                }
                //return null;
            }
        });
    }
}
