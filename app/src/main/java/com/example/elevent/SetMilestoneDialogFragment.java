package com.example.elevent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
/*
    This file contains the implementation of the SetMilestoneDialogFragment, which allows the organizer to input an attendance milestone
 */
/**
 * Displays a dialog fragment that allows the organizer to input an attendance milestone
 */
public class SetMilestoneDialogFragment extends DialogFragment {

    private Event event;

    /**
     * Called to do initial creation of a fragment
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            event = getArguments().getParcelable("event");
        }
    }

    /**
     * Builds a dialog fragment that allows the organizer to set am attendance milestone
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return Return a new Dialog instance to be displayed by the Fragment
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.set_milestone_dialog_fragment, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        EditText setMilestoneEditText = view.findViewById(R.id.set_milestone_edit_text);
        if (event.getMilestone() != 0){
            setMilestoneEditText.setText(String.valueOf(event.getMilestone()));
        }
        return builder
                .setView(view)
                .setTitle("Set Milestone")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", ((dialog, which) -> {
                    if (setMilestoneEditText.getText() != null) {
                        event.setMilestone(Integer.parseInt(setMilestoneEditText.getText().toString()));
                        EventDB db = new EventDB();
                        db.updateEvent(event);
                    } else {
                        Toast.makeText(requireContext(), "Milestone not entered", Toast.LENGTH_SHORT).show();
                    }

                }))
                .create();
    }
}
