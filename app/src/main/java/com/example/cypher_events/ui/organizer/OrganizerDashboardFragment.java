package com.example.cypher_events.ui.organizer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cypher_events.R;

import com.example.cypher_events.ui.organizer.CreateEventFragment;
import com.example.cypher_events.ui.organizer.MyEventsFragment;
import com.google.firebase.firestore.FirebaseFirestore;

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

        // Load Organizer name/email
        db.collection("Events").document(deviceId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String name = doc.getString("Entrant_name");
                        welcomeText.setText("Welcome, " + (name != null ? name : "Organizer"));
                    } else {
                        welcomeText.setText("Welcome, Organizer");
                    }
                });

        // Navigate to CreateEventFragment
        btnCreateEvent.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new CreateEventFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Navigate to MyEventsFragment (you can design it similarly to Browse Events)
        btnMyEvents.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new MyEventsFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }
}
