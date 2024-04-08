package com.example.elevent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ErrorFragment extends Fragment {

    // Constructor for the ErrorFragment class
    public ErrorFragment() {
        // Required empty public constructor
    }

    // Called to create the view hierarchy associated with the fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_errorpage, container, false);
    }

    // Called immediately after onCreateView() has returned a View
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Here you can initialize views and perform other setup if needed
        // For example, you can find views by their IDs and set listeners

        // Finding the error message TextView and setting its text
        TextView errorMessageTextView = view.findViewById(R.id.error_message);
        errorMessageTextView.setText(getString(R.string.oops));
    }
}
