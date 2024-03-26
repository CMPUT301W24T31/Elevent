package com.example.elevent.Admin;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.elevent.Event;
import com.example.elevent.EventArrayAdapter;
import com.example.elevent.EventDB;
import com.example.elevent.R;
import com.google.firebase.firestore.EventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminEventFragment extends Fragment {

    private EventArrayAdapter eventAdapter;
    private ArrayList<Event> events = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_event_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = view.findViewById(R.id.admin_event_list_view);

        eventAdapter = new EventArrayAdapter(requireActivity(), events);
        listView.setAdapter(eventAdapter);

        listView.setOnItemLongClickListener((adapterView, view1, position, id) -> {
            Event eventToDelete = events.get(position);
            showDeleteConfirmationDialog(eventToDelete);
            return true;
        });

        fetchEvents();
    }
    private void showDeleteConfirmationDialog(Event eventToDelete) {
        Log.d("AdminEventFragment", "Attempting to delete event with ID: " + eventToDelete.getEventID());
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    deleteEvent(eventToDelete);
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    private void deleteEvent(Event eventToDelete) {
        if (eventToDelete.getEventID() == null) {
            Log.e("AdminEventFragment", "Event ID is null, cannot delete event.");
            Toast.makeText(getContext(), "Error: Event ID is null.", Toast.LENGTH_SHORT).show();
            return;
        }
        EventDB eventDB = new EventDB();
        eventDB.deleteEvent(eventToDelete.getEventID()).thenRun(() -> {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    // Remove the event from the local list and refresh the list view
                    events.remove(eventToDelete);
                    eventAdapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Event deleted successfully.", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void fetchEvents() {
        EventDB eventDB = new EventDB();
        eventDB.getAllEvents(eventsList -> {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    events.clear();
                    events.addAll(eventsList);
                    eventAdapter.notifyDataSetChanged();
                });
            }
        });
    }
}

