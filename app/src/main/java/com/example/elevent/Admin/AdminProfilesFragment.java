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

import com.example.elevent.R;
import com.example.elevent.User;
import com.example.elevent.UserAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
/*
    This file contains the implementation of the AdminProfilesFragment, which allows admin to browse and delete profiles
 */
/**
 * AdminProfilesFragment displays a list of user profiles, allowing an admin to view and delete them.
 * It fetches user profile information from Firestore and uses a RecyclerView to display it.
 */
public class AdminProfilesFragment extends Fragment {
    private static final String TAG = "AdminProfilesFragment";
    private RecyclerView profilesRecyclerView;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Initializes the fragment.
     */
    public AdminProfilesFragment() {
        // empty public constructor
    }

    /**
     * Called to have the fragment instantiate its user interface view
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or nul
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_profiles_view, container, false);
        setupRecyclerView(view);
        fetchUserProfiles();
        return view;
    }

    /**
     * Sets up the RecyclerView with the UserAdapter and a long click listener for profile deletion.
     * @param view The root view of the fragment.
     */
    private void setupRecyclerView(View view) {
        profilesRecyclerView = view.findViewById(R.id.profiles_recycler_view);
        profilesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapter = new UserAdapter(userList, this::onUserLongClick);
        profilesRecyclerView.setAdapter(userAdapter);
    }

    /**
     * Handles long clicks on user profiles by displaying a deletion confirmation dialog.
     * @param user The user associated with the long-clicked profile.
     * @param position The position of the clicked item in the RecyclerView.
     */
    private void onUserLongClick(User user, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Profile")
                .setMessage("Are you sure you want to delete this profile? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteProfile(user, position))
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Deletes a user profile from Firestore.
     * @param user The user to be deleted.
     * @param position The position of the user in the RecyclerView.
     */
    private void deleteProfile(User user, int position) {
        db.collection("users").document(user.getUserID())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    userList.remove(position);
                    userAdapter.notifyItemRemoved(position);
                    Log.d(TAG, "Profile deleted successfully.");
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error deleting profile", e));
    }

    /**
     * Fetches user profiles from Firestore and updates the RecyclerView.
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
            }
        });
    }
}
