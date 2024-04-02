package com.example.elevent.Admin;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elevent.R;

import java.util.List;

/**
 * Adapter for displaying images in a RecyclerView.
 * It supports displaying images with a title that is either "Event" or "User"
 * and handling long clicks on each item for further actions.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private List<Image> imageItems;
    private OnImageLongClickListener longClickListener;

    /**
     * Interface for callback on long click on an image item.
     */
    public interface OnImageLongClickListener {
        void onImageLongClick(Image image, int position);
    }

    /**
     * Constructs the adapter with a list of images and a long click listener.
     *
     * @param imageItems The list of images to be displayed.
     * @param longClickListener The listener for long click events.
     */
    public ImageAdapter(List<Image> imageItems, OnImageLongClickListener longClickListener) {
        this.imageItems = imageItems;
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
        Image item = imageItems.get(position);
        holder.imageView.setImageBitmap(item.getImage());

        String header = item.isEvent() ? "Event" : "User";
        String textToShow = header + "\n" + item.getName();
        SpannableStringBuilder spannableString = new SpannableStringBuilder(textToShow);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, header.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new RelativeSizeSpan(1.5f), 0, header.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        holder.textView.setText(spannableString);

        holder.itemView.setOnLongClickListener(view -> {
            if (longClickListener != null) {
                longClickListener.onImageLongClick(item, position);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return imageItems.size();
    }

    /**
     * ViewHolder for image items in the RecyclerView.
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
