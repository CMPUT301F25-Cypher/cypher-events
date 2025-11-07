package com.example.cypher_events.ui.organizer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cypher_events.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class OrganizerDashboardFragment extends Fragment {

    private FirebaseFirestore db;
    private String deviceId;
    private TextView welcomeText;
    private Button btnCreateEvent, btnMyEvents;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.organiser_dashboard, container, false);
    }

    @SuppressLint("HardwareIds")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        deviceId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        welcomeText = view.findViewById(R.id.tvWelcomeName);
        btnCreateEvent = view.findViewById(R.id.btnCreateEvent);
        btnMyEvents = view.findViewById(R.id.btnMyEvents);

        // Step 1: Pull Entrant Info First
        loadEntrantAndSyncOrganizer();

        // Step 2: Navigate to CreateEventFragment
        btnCreateEvent.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new CreateEventFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Step 3: Navigate to MyEventsFragment
        btnMyEvents.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new MyEventsFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    // --- Step 1: Load Entrant Info ---
    private void loadEntrantAndSyncOrganizer() {
        db.collection("Entrants").document(deviceId).get()
                .addOnSuccessListener(entrantDoc -> {
                    if (entrantDoc.exists()) {
                        String entrantName = entrantDoc.getString("Entrant_name");
                        String entrantEmail = entrantDoc.getString("Entrant_email");
                        String entrantPhone = entrantDoc.getString("Entrant_phone");

                        welcomeText.setText("Welcome, " + (entrantName != null ? entrantName : "Organizer"));

                        // Step 2: Create or Update Organizer Doc
                        createOrUpdateOrganizer(entrantName, entrantEmail, entrantPhone);
                    } else {
                        welcomeText.setText("Welcome, Organizer");
                        Toast.makeText(getContext(), "Entrant data not found. Please register first.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load entrant info: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void createOrUpdateOrganizer(String name, String email, String phone) {
        if (email == null || email.isEmpty()) {
            email = "unknown@example.com";
        }

        Map<String, Object> organizerData = new HashMap<>();
        organizerData.put("Organizer_id", deviceId);
        organizerData.put("Organizer_name", name != null ? name : "Unnamed Organizer");
        organizerData.put("Organizer_email", email);
        organizerData.put("Organizer_phone", phone);
        organizerData.put("Organizer_notificationsEnabled", true);
        organizerData.put("Organizer_activeEventIDs", new HashMap<>()); // optional placeholder
        organizerData.put("Organizer_completedEventIDs", new HashMap<>());
        organizerData.put("Organizer_createdEventIDs", new HashMap<>());

        DocumentReference organizerRef = db.collection("Organizers").document(deviceId);

        organizerRef.get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                // Update if existing
                organizerRef.update(organizerData)
                        .addOnSuccessListener(a -> Toast.makeText(getContext(), "Organizer synced.", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Sync failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                // Create new organizer entry
                organizerRef.set(organizerData)
                        .addOnSuccessListener(a -> Toast.makeText(getContext(), "Organizer profile created.", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Creation failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }
}
