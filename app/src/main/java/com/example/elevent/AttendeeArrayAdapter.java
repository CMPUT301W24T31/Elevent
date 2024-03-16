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

public class AttendeeArrayAdapter extends ArrayAdapter<String> {

    public AttendeeArrayAdapter(Context context, ArrayList<String> attendees ) {
        super(context,0,attendees);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.attendee_array_adapter_content, parent, false);
        } else {
            view = convertView;
        }

        String attendee = getItem(position);
        TextView attendeeName = view.findViewById(R.id.attendee_array_adapter_attendee_name);

        attendeeName.setText(attendee);

        return view;
    }
}
