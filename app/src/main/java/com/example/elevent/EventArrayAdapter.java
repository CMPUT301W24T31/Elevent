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
/*
    This file contains the implementation of the EventArrayAdapter that is responsible for displaying events
 */
/**
 * Displays the list of events
 */
public class EventArrayAdapter extends ArrayAdapter<Event> {
    /**
     * Class constructor
     * @param context Host activity
     * @param events List of event objects
     */
    public EventArrayAdapter(Context context, ArrayList<Event> events ) {
        super(context,0,events);
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

        // Inflate the layout if the view is not reused
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.event_array_adapter_content, parent, false);
        } else{
            view = convertView;
        }

        // Get the event at the current position
        Event event = getItem(position);

        // Find the TextViews for event name and event date in the layout
        TextView eventName = view.findViewById(R.id.event_array_adapter_name_text);
        TextView eventDate = view.findViewById(R.id.pevent_array_adapter_date_text);

        // Set the text for the event name and date TextViews
        eventName.setText(event.getEventName());
        // TODO: 2024-03-06 uncomment when getEventDate()
        eventDate.setText(event.getDate()); // Assuming Event has getEventDate() method

        return view;
    }


}