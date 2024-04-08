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
    This file contains the implementation of the NotificationArrayAdapter, which displays an event's notifications
 */
/**
 * Displays an event's notifications
 */
public class NotificationArrayAdapter extends ArrayAdapter<String> {
    private final ArrayList<String> notifications;

    /**
     * Class constructor
     * @param context Host activity
     * @param notifications List of notifications
     */
    public NotificationArrayAdapter(Context context, ArrayList<String> notifications){
        super(context, 0, notifications);
        this.notifications = notifications;
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
     * @return A View corresponding to the data at the specified position
     */
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
