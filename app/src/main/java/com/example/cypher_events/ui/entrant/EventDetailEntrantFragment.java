package com.example.cypher_events.ui.entrant;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cypher_events.R;
import com.example.cypher_events.domain.model.Entrant;
import com.google.firebase.firestore.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDetailEntrantFragment extends Fragment {

    private FirebaseFirestore db;
    private String eventId;
    private String deviceId;

    private Button btnJoinWaitlist, btnAccept, btnDecline;
    private LinearLayout layoutAcceptDecline;

    private Entrant currentEntrant; // pulled from Firestore

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_detail_entrant, container, false);
    }

    @SuppressLint("HardwareIds")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        deviceId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        eventId = getArguments() != null ? getArguments().getString("EventId") : null;

        btnJoinWaitlist = view.findViewById(R.id.btnJoinWaitlist);
        layoutAcceptDecline = view.findViewById(R.id.layoutAcceptDecline);
        btnAccept = view.findViewById(R.id.btnAccept);
        btnDecline = view.findViewById(R.id.btnDecline);

        btnJoinWaitlist.setVisibility(View.GONE);
        layoutAcceptDecline.setVisibility(View.GONE);

        ImageButton backButton = view.findViewById(R.id.btnBack);
        if (backButton != null) {
            backButton.setOnClickListener(v ->
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.container, new EntrantDashboardFragment())
                            .commit());
        }

        if (eventId == null) {
            Toast.makeText(getContext(), "Invalid Event ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Step 1: pull entrant info first
        db.collection("Entrants").document(deviceId).get()
                .addOnSuccessListener(this::onEntrantLoaded)
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load entrant: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void onEntrantLoaded(DocumentSnapshot doc) {
        if (!doc.exists()) {
            Toast.makeText(getContext(), "Entrant not registered", Toast.LENGTH_SHORT).show();
            return;
        }

        currentEntrant = doc.toObject(Entrant.class);
        if (currentEntrant == null) {
            Toast.makeText(getContext(), "Entrant parse error", Toast.LENGTH_SHORT).show();
            return;
        }

        loadEventAndConfigureUI();
    }

    private void loadEventAndConfigureUI() {
        db.collection("Events").document(eventId).get()
                .addOnSuccessListener(this::handleEventLoaded)
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void handleEventLoaded(DocumentSnapshot doc) {
        if (!doc.exists()) {
            Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean inWaitlist = isEntrantInList(doc, "Event_waitlistEntrants");
        boolean inJoined = isEntrantInList(doc, "Event_joinedEntrants");
        boolean inDeclined = isEntrantInList(doc, "Event_declinedEntrants");

        if (!inWaitlist && !inJoined && !inDeclined) {
            // not in any list
            btnJoinWaitlist.setVisibility(View.VISIBLE);
            layoutAcceptDecline.setVisibility(View.GONE);
            btnJoinWaitlist.setOnClickListener(v -> onJoinWaitlistClicked());
        } else if (inWaitlist) {
            // invited state
            btnJoinWaitlist.setVisibility(View.GONE);
            layoutAcceptDecline.setVisibility(View.VISIBLE);
            btnAccept.setVisibility(View.VISIBLE);
            btnDecline.setVisibility(View.VISIBLE);
            btnAccept.setOnClickListener(v -> handleAccept());
            btnDecline.setOnClickListener(v -> handleDecline());
        } else if (inJoined) {
            // already accepted
            btnJoinWaitlist.setVisibility(View.GONE);
            layoutAcceptDecline.setVisibility(View.VISIBLE);
            btnAccept.setVisibility(View.GONE); // only decline now
            btnDecline.setVisibility(View.VISIBLE);
            btnDecline.setOnClickListener(v -> handleDecline());
        } else if (inDeclined) {
            btnJoinWaitlist.setVisibility(View.GONE);
            layoutAcceptDecline.setVisibility(View.GONE);
            Toast.makeText(getContext(), "You declined this event.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isEntrantInList(DocumentSnapshot doc, String fieldName) {
        List<Map<String, Object>> entrants = (List<Map<String, Object>>) doc.get(fieldName);
        if (entrants == null) return false;
        for (Map<String, Object> e : entrants) {
            if (deviceId.equals(e.get("Entrant_id")) || deviceId.equals(e.get("Entrant_status"))) {
                return true;
            }
        }
        return false;
    }


    // JOIN WAITLIST
    private void onJoinWaitlistClicked() {
        Toast.makeText(getContext(), "Joining waitlist...", Toast.LENGTH_SHORT).show();

        Map<String, Object> entrantMap = createEntrantMap();
        WriteBatch batch = db.batch();

        batch.update(db.collection("Events").document(eventId),
                "Event_waitlistEntrants", FieldValue.arrayUnion(entrantMap));
        batch.update(db.collection("Entrants").document(deviceId),
                "Entrant_joinedEventIDs", FieldValue.arrayUnion(eventId));

        batch.commit().addOnSuccessListener(a -> {
            Toast.makeText(getContext(), "Joined waitlist!", Toast.LENGTH_SHORT).show();
            simulateInvite();
        }).addOnFailureListener(e ->
                Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    // SIMULATE INVITE
    private void simulateInvite() {
        new Handler().postDelayed(() -> {
            Toast.makeText(getContext(), "You have been invited!", Toast.LENGTH_SHORT).show();
            btnJoinWaitlist.setVisibility(View.GONE);
            layoutAcceptDecline.setVisibility(View.VISIBLE);
            btnAccept.setVisibility(View.VISIBLE);
            btnDecline.setVisibility(View.VISIBLE);

            btnAccept.setOnClickListener(v -> handleAccept());
            btnDecline.setOnClickListener(v -> handleDecline());
        }, 1000);
    }


    // ACCEPT
    private void handleAccept() {
        Map<String, Object> entrantMap = createEntrantMap();

        WriteBatch batch = db.batch();

        // Event side
        batch.update(db.collection("Events").document(eventId),
                "Event_waitlistEntrants", FieldValue.arrayRemove(entrantMap),
                "Event_joinedEntrants", FieldValue.arrayUnion(entrantMap));

        // Entrant side
        batch.update(db.collection("Entrants").document(deviceId),
                "Entrant_joinedEventIDs", FieldValue.arrayRemove(eventId),
                "Entrant_acceptedEventIDs", FieldValue.arrayUnion(eventId));

        batch.commit().addOnSuccessListener(a -> {
            Toast.makeText(getContext(), "Invitation accepted!", Toast.LENGTH_SHORT).show();
            // hide accept, keep decline
            btnAccept.setVisibility(View.GONE);
            btnDecline.setVisibility(View.VISIBLE);
        }).addOnFailureListener(e ->
                Toast.makeText(getContext(), "Failed to accept: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    // DECLINE
    private void handleDecline() {
        Map<String, Object> entrantMap = createEntrantMap();

        WriteBatch batch = db.batch();

        batch.update(db.collection("Events").document(eventId),
                "Event_waitlistEntrants", FieldValue.arrayRemove(entrantMap),
                "Event_joinedEntrants", FieldValue.arrayRemove(entrantMap),
                "Event_declinedEntrants", FieldValue.arrayUnion(entrantMap));

        batch.update(db.collection("Entrants").document(deviceId),
                "Entrant_joinedEventIDs", FieldValue.arrayRemove(eventId),
                "Entrant_acceptedEventIDs", FieldValue.arrayRemove(eventId),
                "Entrant_declinedEventIDs", FieldValue.arrayUnion(eventId));

        batch.commit().addOnSuccessListener(a -> {
            Toast.makeText(getContext(), "Invitation declined.", Toast.LENGTH_SHORT).show();
            layoutAcceptDecline.setVisibility(View.GONE);
        }).addOnFailureListener(e ->
                Toast.makeText(getContext(), "Failed to decline: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    // DYNAMIC ENTRANT INFO
    private Map<String, Object> createEntrantMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("Entrant_id", deviceId);
        map.put("Entrant_name", currentEntrant != null ? currentEntrant.getEntrant_name() : "Unknown");
        map.put("Entrant_email", currentEntrant != null ? currentEntrant.getEntrant_email() : "unknown@example.com");
        map.put("Entrant_phone", currentEntrant != null ? currentEntrant.getEntrant_phone() : "N/A");
        map.put("Entrant_status", deviceId);
        return map;
    }
}
