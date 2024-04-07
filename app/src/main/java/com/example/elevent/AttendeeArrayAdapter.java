package com.example.elevent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
/*
    This file contains the implementation of the AttendeeArrayAdapter that is responsible for displaying attendees
 */
/**
 * Displays the list of attendees
 */
public class AttendeeArrayAdapter extends ArrayAdapter<User> {
    /**
     * Class constructor
     * @param context Host activity
     * @param attendees List of attendees
     */
    public AttendeeArrayAdapter(Context context, ArrayList<User> attendees ) {
        super(context,0, attendees);
    }

    /**
     * Get a View that displays the data at the specified position in the data set
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param parent The parent that this view will eventually be attached to
     * @return The view that displays the data
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.attendee_array_adapter_content, parent, false);
        } else {
            view = convertView;
        }

        User attendee = getItem(position);
        TextView attendeeName = view.findViewById(R.id.attendee_array_adapter_attendee_name);

        if (attendee != null) {
            if (attendee.getName() == null) {
                attendeeName.setText(attendee.getUserID());
            } else {
                attendeeName.setText(attendee.getName());
            }
        }

        return view;
    }
}
