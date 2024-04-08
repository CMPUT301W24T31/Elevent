package com.example.elevent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.Blob;

import java.util.Objects;
/*
    This file contains the implementation for displaying a dialog fragment to the organizer that contains an
    attendee's profile information and how many times they have checked in
 */
public class InspectAttendeeInformationFragment extends DialogFragment {
    private User user;
    private Event event;

    /**
     * Called to do initial creation of a fragment
     * Gets the attendee whose information is to be displayed and the event to which the attendee is signed up/checked in
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            user = getArguments().getParcelable("user");
            event = getArguments().getParcelable("event");
        }
    }

    /**
     * Build the dialog fragment to display the attendee's information
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return Builder of the dialog fragment
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.inspect_attendee_dialog_fragment, null);
        ImageView attendeeImage = view.findViewById(R.id.attendee_image);
        TextView attendeeName = view.findViewById(R.id.attendee_name);
        TextView attendeeHomepage = view.findViewById(R.id.attendee_homepage);
        TextView attendeeContact = view.findViewById(R.id.attendee_contact);
        TextView checkInCount = view.findViewById(R.id.check_in_number);
        if (user != null) {
            if (user.getProfilePic() != null){
                attendeeImage.setImageBitmap(convertBlobToBitmap(user.getProfilePic()));
            }
            if (Objects.equals(user.getName(), "")) {
                attendeeName.setVisibility(View.GONE);
            } else {
                attendeeName.setText(user.getName());
            }
            if (Objects.equals(user.getHomePage(), "")){
                attendeeHomepage.setVisibility(View.GONE);
            } else {
                attendeeHomepage.setText(user.getHomePage());
            }
            if (Objects.equals(user.getContact(), "")) {
                attendeeContact.setVisibility(View.GONE);
            } else {
                attendeeContact.setText(user.getContact());
            }
            if (event != null) {
                if (!event.getCheckedInAttendees().containsKey(user.getUserID())) {
                    String notCheckedIn = "Not checked in";
                    checkInCount.setText(notCheckedIn);
                } else {
                    checkInCount.setText(String.format("Checked in %d time(s)", event.getCheckedInAttendees().get(user.getUserID())));
                }
            } else {
                Log.d("Inspect Attendee", "Event is null");
            }
        } else{
            Log.d("Inspect Attendee", "User is null");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setNegativeButton("Close", null)
                .create();
    }

    /**
     * Convert blob to bitmap to be displayed
     * @param blob Blob to be converted
     * @return The resulting bitmap
     */
    private Bitmap convertBlobToBitmap(Blob blob){
        byte[] bytes = blob.toBytes();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
