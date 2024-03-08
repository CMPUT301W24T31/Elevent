package com.example.elevent;

import android.content.Context;
import android.os.Bundle;
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

public class ManageEventFragment extends Fragment {


    interface ManageEventListener {
        //void onCreateEvent(Event event);

        void onPositiveClick(Event event);
    }

    private ManageEventFragment.ManageEventListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ManageEventFragment.ManageEventListener) {
            listener = (ManageEventFragment.ManageEventListener) context;
        } else {
            throw new RuntimeException(context + " must implement ManageEventListener");
        }
    }


    private TextView attendeeListTextView;
    private ListView listOfAttendees;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manageevent, container, false);

        // Find views by their respective IDs
        attendeeListTextView = view.findViewById(R.id.attendee_list_textview);
        listOfAttendees = view.findViewById(R.id.list_of_attendees);
        return view;
    }

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
    }

        // You can also set data to your TextView and ListView
        // attendeeListTextView.setText("Attendees List");
        // Set adapter to ListView
        // Example: listOfAttendees.setAdapter(yourAdapter);

}