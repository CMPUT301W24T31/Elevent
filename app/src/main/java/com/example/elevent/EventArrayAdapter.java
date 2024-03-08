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

public class EventArrayAdapter extends ArrayAdapter<Event> {

    public EventArrayAdapter(Context context, ArrayList<Event> events ) {
        super(context,0,events);
    }

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
        //eventDate.setText(event.getDate()); // Assuming Event has getEventDate() method

        return view;
    }


}