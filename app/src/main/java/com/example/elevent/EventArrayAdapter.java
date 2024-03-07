package com.example.elevent;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

public class EventArrayAdapter extends ArrayAdapter<Event> {

    public EventArrayAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }
}
