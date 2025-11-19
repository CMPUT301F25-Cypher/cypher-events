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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDetailEntrantFragment extends Fragment {

    private static final String ARG_EVENT_ID = "EventId";

    private FirebaseFirestore db;
    private String eventId;
    private String deviceId;

    private Button btnJoinWaitlist;
    private Button btnAccept;
    private Button btnDecline;
    private LinearLayout layoutAcceptDecline;

    private Entrant currentEntrant;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_event_detail_entrant, container, false);
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

        Bundle args = getArguments();
        eventId = (args != null) ? args.getString(ARG_EVENT_ID) : null;

        btnJoinWaitlist = view.findViewById(R.id.btnJoinWaitlist);
        layoutAcceptDecline = view.findViewById(R.id.layoutAcceptDecline);
        btnAccept = view.findViewById(R.id.btnAccept);
        btnDecline = view.findViewById(R.id.btnDecline);

        if (btnJoinWaitlist != null) {
            btnJoinWaitlist.setVisibility(View.GONE);
        }
        if (layoutAcceptDecline != null) {
            layoutAcceptDecline.setVisibility(View.GONE);
        }

        ImageButton backButton = view.findViewById(R.id.btnBack);
        if (backButton != null) {
            backButton.setOnClickListener(v ->
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.container, new EntrantDashboardFragment())
                            .commit()
            );
        }

        if (eventId == null || eventId.trim().isEmpty()) {
            Toast.makeText(getContext(), "Invalid Event ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Load entrant profile first
        db.collection("Entrants").document(deviceId).get()
                .addOnSuccessListener(this::onEntrantLoaded)
                .addOnFailureListener(e ->
                        Toast.makeText(
                                getContext(),
                                "Failed to load entrant: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()
                );
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
                        Toast.makeText(
                                getContext(),
                                "Failed to load event: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    private void handleEventLoaded(DocumentSnapshot doc) {
        if (!doc.exists()) {
            Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean inWaitlist = isEntrantInList(doc, "Event_waitlistEntrants");
        boolean inJoined = isEntrantInList(doc, "Event_joinedEntrants");
        boolean inDeclined = isEntrantInList(doc, "Event_declinedEntrants");

        if (btnJoinWaitlist == null || layoutAcceptDecline == null) {
            return;
        }

        if (!inWaitlist && !inJoined && !inDeclined) {
            // Not in any list yet
            btnJoinWaitlist.setVisibility(View.VISIBLE);
            layoutAcceptDecline.setVisibility(View.GONE);
            btnJoinWaitlist.setOnClickListener(v -> onJoinWaitlistClicked());
        } else if (inWaitlist) {
            // Invited state (waiting to accept/decline)
            btnJoinWaitlist.setVisibility(View.GONE);
            layoutAcceptDecline.setVisibility(View.VISIBLE);

            if (btnAccept != null) {
                btnAccept.setVisibility(View.VISIBLE);
                btnAccept.setOnClickListener(v -> handleAccept());
            }
            if (btnDecline != null) {
                btnDecline.setVisibility(View.VISIBLE);
                btnDecline.setOnClickListener(v -> handleDecline());
            }
        } else if (inJoined) {
            // Already accepted; can still decline
            btnJoinWaitlist.setVisibility(View.GONE);
            layoutAcceptDecline.setVisibility(View.VISIBLE);

            if (btnAccept != null) {
                btnAccept.setVisibility(View.GONE);
            }
            if (btnDecline != null) {
                btnDecline.setVisibility(View.VISIBLE);
                btnDecline.setOnClickListener(v -> handleDecline());
            }
        } else if (inDeclined) {
            btnJoinWaitlist.setVisibility(View.GONE);
            layoutAcceptDecline.setVisibility(View.GONE);
            Toast.makeText(getContext(), "You declined this event.", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("unchecked")
    private boolean isEntrantInList(DocumentSnapshot doc, String fieldName) {
        Object raw = doc.get(fieldName);
        if (!(raw instanceof List)) {
            return false;
        }
        List<Map<String, Object>> entrants = (List<Map<String, Object>>) raw;
        if (entrants == null) {
            return false;
        }
        for (Map<String, Object> e : entrants) {
            if (e == null) continue;
            Object id = e.get("Entrant_id");
            Object status = e.get("Entrant_status");
            if ((id instanceof String && deviceId.equals(id))
                    || (status instanceof String && deviceId.equals(status))) {
                return true;
            }
        }
        return false;
    }

    // Join waitlist
    private void onJoinWaitlistClicked() {
        Toast.makeText(getContext(), "Joining waitlist...", Toast.LENGTH_SHORT).show();

        Map<String, Object> entrantMap = createEntrantMap();
        WriteBatch batch = db.batch();

        batch.update(
                db.collection("Events").document(eventId),
                "Event_waitlistEntrants", FieldValue.arrayUnion(entrantMap)
        );
        batch.update(
                db.collection("Entrants").document(deviceId),
                "Entrant_joinedEventIDs", FieldValue.arrayUnion(eventId)
        );

        batch.commit()
                .addOnSuccessListener(a -> {
                    Toast.makeText(getContext(), "Joined waitlist!", Toast.LENGTH_SHORT).show();
                    simulateInvite();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                getContext(),
                                "Failed: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    // Simulate invite for demo purposes
    private void simulateInvite() {
        new Handler().postDelayed(() -> {
            Toast.makeText(getContext(), "You have been invited!", Toast.LENGTH_SHORT).show();
            if (btnJoinWaitlist != null) {
                btnJoinWaitlist.setVisibility(View.GONE);
            }
            if (layoutAcceptDecline != null) {
                layoutAcceptDecline.setVisibility(View.VISIBLE);
            }
            if (btnAccept != null) {
                btnAccept.setVisibility(View.VISIBLE);
                btnAccept.setOnClickListener(v -> handleAccept());
            }
            if (btnDecline != null) {
                btnDecline.setVisibility(View.VISIBLE);
                btnDecline.setOnClickListener(v -> handleDecline());
            }
        }, 1000);
    }

    // Accept invitation
    private void handleAccept() {
        Map<String, Object> entrantMap = createEntrantMap();
        WriteBatch batch = db.batch();

        batch.update(
                db.collection("Events").document(eventId),
                "Event_waitlistEntrants", FieldValue.arrayRemove(entrantMap),
                "Event_joinedEntrants", FieldValue.arrayUnion(entrantMap)
        );

        batch.update(
                db.collection("Entrants").document(deviceId),
                "Entrant_joinedEventIDs", FieldValue.arrayRemove(eventId),
                "Entrant_acceptedEventIDs", FieldValue.arrayUnion(eventId)
        );

        batch.commit()
                .addOnSuccessListener(a -> {
                    Toast.makeText(getContext(), "Invitation accepted!", Toast.LENGTH_SHORT).show();
                    if (btnAccept != null) {
                        btnAccept.setVisibility(View.GONE);
                    }
                    if (btnDecline != null) {
                        btnDecline.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                getContext(),
                                "Failed to accept: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    // Decline invitation
    private void handleDecline() {
        Map<String, Object> entrantMap = createEntrantMap();
        WriteBatch batch = db.batch();

        batch.update(
                db.collection("Events").document(eventId),
                "Event_waitlistEntrants", FieldValue.arrayRemove(entrantMap),
                "Event_joinedEntrants", FieldValue.arrayRemove(entrantMap),
                "Event_declinedEntrants", FieldValue.arrayUnion(entrantMap)
        );

        batch.update(
                db.collection("Entrants").document(deviceId),
                "Entrant_joinedEventIDs", FieldValue.arrayRemove(eventId),
                "Entrant_acceptedEventIDs", FieldValue.arrayRemove(eventId),
                "Entrant_declinedEventIDs", FieldValue.arrayUnion(eventId)
        );

        batch.commit()
                .addOnSuccessListener(a -> {
                    Toast.makeText(getContext(), "Invitation declined.", Toast.LENGTH_SHORT).show();
                    if (layoutAcceptDecline != null) {
                        layoutAcceptDecline.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                getContext(),
                                "Failed to decline: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    // Build a map representing this entrant for embedding in Event docs
    private Map<String, Object> createEntrantMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("Entrant_id", deviceId);
        map.put(
                "Entrant_name",
                (currentEntrant != null && currentEntrant.getEntrant_name() != null)
                        ? currentEntrant.getEntrant_name()
                        : "Unknown"
        );
        map.put(
                "Entrant_email",
                (currentEntrant != null && currentEntrant.getEntrant_email() != null)
                        ? currentEntrant.getEntrant_email()
                        : "unknown@example.com"
        );
        map.put(
                "Entrant_phone",
                (currentEntrant != null && currentEntrant.getEntrant_phone() != null)
                        ? currentEntrant.getEntrant_phone()
                        : "N/A"
        );
        map.put("Entrant_status", deviceId);
        return map;
    }
}
