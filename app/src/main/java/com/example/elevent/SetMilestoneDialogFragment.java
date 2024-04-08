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


public class SetMilestoneDialogFragment extends DialogFragment {

    private Event event;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            event = (Event) getArguments().getSerializable("event");
        }
    }

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
