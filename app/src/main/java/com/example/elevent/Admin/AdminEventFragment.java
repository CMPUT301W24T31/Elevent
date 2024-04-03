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
import java.util.ArrayList;

/**
 * Fragment responsible for displaying a list of events in the admin view.
 * Allows for long-click actions on each event for deletion purposes.
 */
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

    /**
     * Shows a confirmation dialog to the user before deleting an event.
     *
     * @param eventToDelete The event to be deleted.
     */
    private void showDeleteConfirmationDialog(Event eventToDelete) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteEvent(eventToDelete))
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Deletes an event from the database and updates the UI accordingly.
     *
     * @param eventToDelete The event to be deleted.
     */
    private void deleteEvent(Event eventToDelete) {
        if (eventToDelete.getEventID() == null) {
            Toast.makeText(getContext(), "Error: Event ID is null.", Toast.LENGTH_SHORT).show();
            return;
        }

        EventDB eventDB = new EventDB();
        eventDB.deleteEvent(eventToDelete.getEventID()).thenRun(() -> {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    events.remove(eventToDelete);
                    eventAdapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Event deleted successfully.", Toast.LENGTH_SHORT).show();
                });
            }
        }).exceptionally(exception -> {
            Log.e("AdminEventFragment", "Error deleting event: ", exception);
            Toast.makeText(getContext(), "Failed to delete event.", Toast.LENGTH_SHORT).show();
            return null;
        });
    }

    /**
     * Fetches all events from the database and updates the UI.
     */
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
