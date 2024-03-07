package com.example.elevent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class EventListAdapter extends ArrayAdapter<Event> {

    private ArrayList<Event> events;

    private Context context;

    public EventListAdapter(Context context, ArrayList<Event> events ) {
        super(context,0,events);
        this.events = events;
        this.context = context;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        // Inflate the layout if the view is not reused
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.event_list_item, parent, false);
        }

        // Get the event at the current position
        Event event = events.get(position);

        // Find the TextViews for event name and event date in the layout
        TextView eventName = view.findViewById(R.id.event_name_text);
        TextView eventDate = view.findViewById(R.id.event_date_text);

        // Set the text for the event name and date TextViews
        eventName.setText(event.getEventName());
        eventDate.setText(event.getEventDate()); // Assuming Event has getEventDate() method

        return view;
    }


}
