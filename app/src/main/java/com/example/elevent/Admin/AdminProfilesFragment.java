package com.example.elevent.Admin;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.elevent.R;
import com.example.elevent.User;
import com.example.elevent.UserAdapter;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for displaying and managing a list of user profiles in the admin panel.
 * Allows admins to view detailed user profiles and provides functionality to delete them.
 * Upon deletion, the user's ID is also removed from all events' attendees lists.
 */
public class AdminProfilesFragment extends Fragment {
    private static final String TAG = "AdminProfilesFragment";

    private RecyclerView profilesRecyclerView;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AdminProfilesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_profiles_view, container, false);
        setupRecyclerView(view);
        fetchUserProfiles();
        return view;
    }

    /**
     * Sets up the RecyclerView with a LinearLayoutManager and the UserAdapter.
     * Also, defines the action for long clicks on each user profile item.
     * @param view The current fragment's root view.
     */
    private void setupRecyclerView(View view) {
        profilesRecyclerView = view.findViewById(R.id.profiles_recycler_view);
        profilesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapter = new UserAdapter(userList, this::onUserLongClick);
        profilesRecyclerView.setAdapter(userAdapter);
    }

    /**
     * Displays a confirmation dialog for deleting a user profile. If confirmed, initiates
     * the profile deletion process.
     * @param user The user object associated with the selected profile.
     * @param position The position of the selected item in the RecyclerView.
     */
    private void onUserLongClick(User user, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Profile")
                .setMessage("Are you sure you want to delete this profile? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteProfile(user, position))
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Initiates the removal of the user's ID from event attendee lists and then deletes the user's profile.
     * @param user The user to be deleted.
     * @param position The position of the user in the RecyclerView.
     */
    private void deleteProfile(User user, int position) {
        final String userId = user.getUserID();
        removeFromEvents(userId, new FirestoreOperationCallback() {
            @Override
            public void onSuccess() {
                db.collection("users").document(userId)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            userList.remove(position);
                            userAdapter.notifyItemRemoved(position);
                            Toast.makeText(getContext(), "Profile deleted successfully.", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error deleting profile", e);
                            Toast.makeText(getContext(), "Error deleting profile.", Toast.LENGTH_SHORT).show();
                        });
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Failed to remove user ID from events", e);
                Toast.makeText(getContext(), "Failed to remove user from events. Deletion aborted.", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Fetches all user profiles from Firestore and updates the RecyclerView adapter.
     */
    private void fetchUserProfiles() {
        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                userList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
            } else {
                Log.e(TAG, "Error fetching user profiles", task.getException());
                Toast.makeText(getContext(), "Error fetching user profiles.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Attempts to remove the user's ID from 'signedUpAttendees' and 'checkedInAttendees' fields
     * across all events in the database.
     * @param userId The ID of the user to be removed from event lists.
     * @param callback The callback to execute upon completion or failure of the operation.
     */
    private void removeFromEvents(String userId, FirestoreOperationCallback callback) {
        List<Task<Void>> tasks = new ArrayList<>();

        Task<QuerySnapshot> signedUpTask = db.collection("events")
                .whereArrayContains("signedUpAttendees", userId)
                .get();

        signedUpTask.addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Task<Void> removeTask = doc.getReference().update("signedUpAttendees", FieldValue.arrayRemove(userId));
                tasks.add(removeTask);
            }
        });

        Task<QuerySnapshot> checkedInTask = db.collection("events")
                .whereArrayContains("checkedInAttendees", userId)
                .get();

        checkedInTask.addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Task<Void> removeTask = doc.getReference().update("checkedInAttendees", FieldValue.arrayRemove(userId));
                tasks.add(removeTask);
            }
        });

        // Ensure all tasks are completed before invoking onSuccess or onFailure
        Tasks.whenAllComplete(tasks).addOnCompleteListener(completeTask -> {
            if (completeTask.isSuccessful()) {
                callback.onSuccess();
            } else {
                callback.onFailure(completeTask.getException());
            }
        });
    }


    /**
     * Helper method to remove a user's ID from a specific list field within all event documents.
     * @param userId The ID of the user to be removed.
     * @param listName The name of the list field ('signedUpAttendees' or 'checkedInAttendees').
     * @param completion Callback to indicate success or failure.
     */
    private void removeFromEventList(String userId, String listName, FirestoreCompletionCallback completion) {
        db.collection("events").whereArrayContains(listName, userId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        documentSnapshot.getReference()
                                .update(listName, FieldValue.arrayRemove(userId))
                                .addOnSuccessListener(aVoid -> completion.onComplete(true))
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to remove user ID from " + listName, e);
                                    completion.onComplete(false);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error querying events to remove user ID", e);
                    completion.onComplete(false);
                });
    }

    interface FirestoreOperationCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    interface FirestoreCompletionCallback {
        void onComplete(boolean success);
    }
}
