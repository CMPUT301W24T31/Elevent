package com.example.elevent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
/*
    This file contains the implementation of the UserAdapter which displays user profiles in a RecyclerView.
 */
/**
 * Adapter for displaying user profiles in a RecyclerView.
 * Supports displaying user name and contact information.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<User> userList;
    private OnUserLongClickListener longClickListener;

    /**
     * Interface for handling long clicks on a user item.
     */
    public interface OnUserLongClickListener {
        void onUserLongClick(User user, int position);
    }

    /**
     * Constructs the adapter with a list of users and a long click listener.
     *
     * @param userList The list of users to be displayed.
     * @param longClickListener The listener for long click events.
     */
    public UserAdapter(List<User> userList, OnUserLongClickListener longClickListener) {
        this.userList = userList;
        this.longClickListener = longClickListener;
    }

    /**
     * Called when RecyclerView needs a new RecyclerView
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new ViewHolder with a view of the given type
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout using the new profile_item.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);

        //set the user's name and contact information
        holder.nameTextView.setText(user.getName());
        holder.contactTextView.setText(user.getContact() != null ? user.getContact() : "N/A");

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onUserLongClick(user, position);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    /**
     * ViewHolder for user items in the RecyclerView.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView contactTextView;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.admin_profile_name_text);
            contactTextView = itemView.findViewById(R.id.admin_profile_contact_text);
        }
    }
}
