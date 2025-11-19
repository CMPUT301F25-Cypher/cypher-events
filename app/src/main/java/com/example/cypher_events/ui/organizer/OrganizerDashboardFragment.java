package com.example.cypher_events.ui.organizer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cypher_events.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class OrganizerDashboardFragment extends Fragment {

    private FirebaseFirestore db;
    private String deviceId;
    private TextView welcomeText;
    private Button btnCreateEvent, btnMyEvents;
    private ImageButton btnSwitchEntrant;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.organiser_dashboard, container, false);
    }

    @SuppressLint("HardwareIds")
    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        deviceId = Settings.Secure.getString(
                requireContext().getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        welcomeText = view.findViewById(R.id.tvWelcomeName);
        btnCreateEvent = view.findViewById(R.id.btnCreateEvent);
        btnMyEvents = view.findViewById(R.id.btnMyEvents);
        btnSwitchEntrant = view.findViewById(R.id.btnAccount);

        // Sync organizer doc with Entrant info
        loadEntrantAndSyncOrganizer();

        btnCreateEvent.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new CreateEventFragment())
                        .addToBackStack(null)
                        .commit()
        );

        btnMyEvents.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new MyEventsFragment())
                        .addToBackStack(null)
                        .commit()
        );

        btnSwitchEntrant.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.container,
                                new com.example.cypher_events.ui.entrant.EntrantDashboardFragment())
                        .addToBackStack(null)
                        .commit()
        );
    }

    private void loadEntrantAndSyncOrganizer() {
        db.collection("Entrants").document(deviceId).get()
                .addOnSuccessListener(entrantDoc -> {
                    if (entrantDoc.exists()) {
                        String entrantName = entrantDoc.getString("Entrant_name");
                        String entrantEmail = entrantDoc.getString("Entrant_email");
                        String entrantPhone = entrantDoc.getString("Entrant_phone");

                        welcomeText.setText("Welcome, " +
                                (entrantName != null ? entrantName : "Organizer"));

                        createOrUpdateOrganizer(entrantName, entrantEmail, entrantPhone);
                    } else {
                        welcomeText.setText("Welcome, Organizer");
                        Toast.makeText(
                                getContext(),
                                "Entrant data not found. Please register first.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                getContext(),
                                "Failed to load entrant info: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    private void createOrUpdateOrganizer(String name, String email, String phone) {
        if (email == null || email.isEmpty()) {
            email = "unknown@example.com";
        }

        Map<String, Object> organizerData = new HashMap<>();
        organizerData.put("Organizer_id", deviceId);
        organizerData.put("Organizer_name",
                name != null ? name : "Unnamed Organizer");
        organizerData.put("Organizer_email", email);
        organizerData.put("Organizer_phone", phone);
        organizerData.put("Organizer_notificationsEnabled", true);

        // IMPORTANT:
        // Do NOT initialize Organizer_*EventIDs here as HashMaps or arrays.
        // We let Firestore create them via FieldValue.arrayUnion(...) when events are created.

        DocumentReference organizerRef =
                db.collection("Organizers").document(deviceId);

        organizerRef.set(organizerData, SetOptions.merge())
                .addOnSuccessListener(a ->
                        Toast.makeText(
                                getContext(),
                                "Organizer profile synced.",
                                Toast.LENGTH_SHORT
                        ).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(
                                getContext(),
                                "Organizer sync failed: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }
}
