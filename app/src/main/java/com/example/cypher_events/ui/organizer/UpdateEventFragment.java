package com.example.cypher_events.ui.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.cypher_events.R;

public class UpdateEventFragment extends Fragment {

    private EditText etEventName, etDescription, etLocation, etCategory;
    private Button btnSaveChanges;
    private ImageButton btnBackUpdate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_event, container, false);

        // Initialize fields
        etEventName = view.findViewById(R.id.etEventName);
        etDescription = view.findViewById(R.id.etDescription);
        etLocation = view.findViewById(R.id.etLocation);
        etCategory = view.findViewById(R.id.etCategory);
        btnSaveChanges = view.findViewById(R.id.btnSaveChanges);
        btnBackUpdate = view.findViewById(R.id.btnBackUpdate);

        // Go back
        btnBackUpdate.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        // Save logic
        btnSaveChanges.setOnClickListener(v -> {
            String name = etEventName.getText().toString().trim();
            String desc = etDescription.getText().toString().trim();
            String loc = etLocation.getText().toString().trim();
            String cat = etCategory.getText().toString().trim();

            if (name.isEmpty() || desc.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all details.", Toast.LENGTH_SHORT).show();
            } else {
                // TODO: Save to Firestore
                Toast.makeText(getContext(), "Changes saved successfully!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).popBackStack();
            }
        });

        return view;
    }
}
