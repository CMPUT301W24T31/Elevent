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

public class NotificationArrayAdapter extends ArrayAdapter<String> {
    public NotificationArrayAdapter(Context context, ArrayList<String> notifications){
        super(context, 0, notifications);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        // Inflate the layout if the view is not reused
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.notification_array_adapter_content, parent, false);
        } else{
            view = convertView;
        }
        String notificationText = getItem(position);
        TextView notificationDisplay = view.findViewById(R.id.notification_array_adapter_notif_text);
        notificationDisplay.setText(notificationText);
        return view;
    }
}
