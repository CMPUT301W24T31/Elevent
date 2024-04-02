package com.example.elevent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * Adapter for displaying user profiles in a RecyclerView.
 * Supports displaying user information and handling long clicks on each item.
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        Bitmap bitmap = BitmapFactory.decodeByteArray(new byte[0], 0, 0); //TODO test this method when user profiles have images
        holder.imageView.setImageBitmap(bitmap);

        String userInfo = user.getName() + "\nContact: " + (user.getContact() != null ? user.getContact() : "N/A") +
                "\nHomepage: " + (user.getHomePage() != null ? user.getHomePage() : "N/A");
        holder.textView.setText(userInfo);

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
        ImageView imageView;
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.admin_item_image);
            textView = itemView.findViewById(R.id.admin_item_text);
        }
    }
}
