package com.example.elevent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.Blob;

public class InspectAttendeeInformationFragment extends DialogFragment {
    private User user;
    private Event event;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            user = (User) getArguments().getSerializable("user");
            event = (Event) getArguments().getSerializable("event");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.inspect_attendee_dialog_fragment, null);
        ImageView attendeeImage = view.findViewById(R.id.attendee_image);
        TextView attendeeName = view.findViewById(R.id.attendee_name);
        TextView attendeeContact = view.findViewById(R.id.attendee_contact);
        TextView checkInCount = view.findViewById(R.id.check_in_number);
        if (user != null) {
            if (user.getProfilePic() != null){
                attendeeImage.setImageBitmap(convertBlobToBitmap(user.getProfilePic()));
            }
            if (user.getName() == null) {
                attendeeName.setText(user.getUserID());
            } else {
                attendeeName.setText(user.getName());
            }
            if (user.getContact() == null) {
                attendeeContact.setVisibility(View.INVISIBLE);
            } else {
                attendeeContact.setText(user.getContact());
            }
            if (event != null) {
                if (!event.getCheckedInAttendees().containsKey(user.getUserID())) {
                    String notCheckedIn = "Not checked in";
                    checkInCount.setText(notCheckedIn);
                } else {
                    int checkIns = event.getCheckedInAttendees().get(user.getUserID());
                    checkInCount.setText(String.format("Checked in %d times", event.getCheckedInAttendees().get(user.getUserID())));
                }
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setPositiveButton("Close", null)
                .create();
    }
    private Bitmap convertBlobToBitmap(byte[] bytes){
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
