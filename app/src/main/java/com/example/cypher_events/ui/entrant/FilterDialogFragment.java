package com.example.cypher_events.ui.entrant;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.cypher_events.R;

public class FilterDialogFragment extends DialogFragment {

    public interface FilterListener {
        void onFilterSelected(String category);
    }

    private FilterListener listener;

    public void setFilterListener(FilterListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_filter, null);

        Spinner categorySpinner = view.findViewById(R.id.spinnerCategory);
        Button applyButton = view.findViewById(R.id.btnApplyFilter);

        String[] categories = {"All", "Sports", "Workshop", "Art", "Tech Talk"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        applyButton.setOnClickListener(v -> {
            if (listener != null) {
                String selectedCategory = categorySpinner.getSelectedItem().toString();
                listener.onFilterSelected(selectedCategory);
            }
            dismiss();
        });

        return new AlertDialog.Builder(requireContext())
                .setView(view)
                .setTitle("Filter Events")
                .create();
    }
}
