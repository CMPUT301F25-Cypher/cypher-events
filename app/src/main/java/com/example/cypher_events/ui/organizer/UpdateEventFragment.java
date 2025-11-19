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

import com.example.cypher_events.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpdateEventFragment extends Fragment {

    private static final String ARG_EVENT_ID = "EventId";

    private EditText etEventName, etDescription, etLocation, etCategory;
    private Button btnSaveChanges;
    private ImageButton btnBackUpdate;

    private FirebaseFirestore db;
    private String eventId;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_update_event, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        etEventName = view.findViewById(R.id.etEventName);
        etDescription = view.findViewById(R.id.etDescription);
        etLocation = view.findViewById(R.id.etLocation);
        etCategory = view.findViewById(R.id.etCategory);
        btnSaveChanges = view.findViewById(R.id.btnSaveChanges);
        btnBackUpdate = view.findViewById(R.id.btnBackUpdate);

        Bundle args = getArguments();
        eventId = (args != null) ? args.getString(ARG_EVENT_ID) : null;

        if (eventId == null || eventId.trim().isEmpty()) {
            Toast.makeText(getContext(), "No event selected.", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
            return;
        }

        loadEventDetails();

        if (btnBackUpdate != null) {
            btnBackUpdate.setOnClickListener(v ->
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.container, new MyEventsFragment())
                            .commit()
            );
        }

        if (btnSaveChanges != null) {
            btnSaveChanges.setOnClickListener(v -> saveChanges());
        }
    }

    private void loadEventDetails() {
        db.collection("Events").document(eventId).get()
                .addOnSuccessListener(this::populateFields)
                .addOnFailureListener(e ->
                        Toast.makeText(
                                getContext(),
                                "Failed to load event: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    private void populateFields(DocumentSnapshot doc) {
        if (!doc.exists()) {
            Toast.makeText(getContext(), "Event not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = doc.getString("Event_title");
        String desc = doc.getString("Event_description");
        String loc = doc.getString("Event_location");
        String cat = doc.getString("Event_category");

        etEventName.setText(name != null ? name : "");
        etDescription.setText(desc != null ? desc : "");
        etLocation.setText(loc != null ? loc : "");
        etCategory.setText(cat != null ? cat : "");
    }

    private void saveChanges() {
        String name = etEventName.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String loc = etLocation.getText().toString().trim();
        String cat = etCategory.getText().toString().trim();

        if (name.isEmpty() || desc.isEmpty() || loc.isEmpty() || cat.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all details.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("Event_title", name);
        updates.put("Event_description", desc);
        updates.put("Event_location", loc);
        updates.put("Event_category", cat);

        db.collection("Events").document(eventId)
                .update(updates)
                .addOnSuccessListener(a -> {
                    Toast.makeText(
                            getContext(),
                            "Changes saved successfully!",
                            Toast.LENGTH_SHORT
                    ).show();

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.container, new MyEventsFragment())
                            .commit();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                getContext(),
                                "Failed to save changes: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }
}
