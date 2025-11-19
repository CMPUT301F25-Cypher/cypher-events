package com.example.cypher_events.ui.organizer;

import android.os.Bundle;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;
import java.util.Map;

public class DrawReplacementFragment extends Fragment {

    private static final String ARG_EVENT_ID = "EventId";

    private Button btnGenerateReplacement;
    private TextView tvReplacementResult;

    private FirebaseFirestore db;
    private String eventId;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_draw_replacement, container, false);

        btnGenerateReplacement = view.findViewById(R.id.btnGenerateReplacement);
        tvReplacementResult = view.findViewById(R.id.tvReplacementResult);

        ImageButton backButton = view.findViewById(R.id.btnBackReplacement);
        if (backButton != null) {
            backButton.setOnClickListener(v ->
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.container, new OrganizerDashboardFragment())
                            .commit()
            );
        }

        return view;
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        Bundle args = getArguments();
        eventId = (args != null) ? args.getString(ARG_EVENT_ID) : null;

        if (btnGenerateReplacement != null) {
            btnGenerateReplacement.setOnClickListener(v -> drawReplacement());
        }
    }

    private void drawReplacement() {
        if (eventId == null || eventId.trim().isEmpty()) {
            Toast.makeText(getContext(), "No event selected.", Toast.LENGTH_SHORT).show();
            return;
        }

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

    @SuppressWarnings("unchecked")
    private void handleEventLoaded(DocumentSnapshot doc) {
        if (!doc.exists()) {
            Toast.makeText(getContext(), "Event not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Map<String, Object>> waitlist =
                (List<Map<String, Object>>) doc.get("Event_waitlistEntrants");
        List<Map<String, Object>> selected =
                (List<Map<String, Object>>) doc.get("Event_selectedEntrants");
        List<Map<String, Object>> declined =
                (List<Map<String, Object>>) doc.get("Event_declinedEntrants");

        Map<String, Object> replacement = findFirstEligible(waitlist, selected, declined);

        if (replacement == null) {
            tvReplacementResult.setText("No replacement available.");
            Toast.makeText(getContext(), "No eligible replacement found.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Move replacement from waitlist → selected in Firestore
        WriteBatch batch = db.batch();
        batch.update(
                db.collection("Events").document(eventId),
                "Event_waitlistEntrants", FieldValue.arrayRemove(replacement),
                "Event_selectedEntrants", FieldValue.arrayUnion(replacement)
        );

        batch.commit()
                .addOnSuccessListener(a -> {
                    String name = (String) replacement.get("Entrant_name");
                    String email = (String) replacement.get("Entrant_email");
                    String label;
                    if (name != null && !name.isEmpty()) {
                        label = "Replacement: " + name;
                    } else if (email != null && !email.isEmpty()) {
                        label = "Replacement (email): " + email;
                    } else {
                        label = "Replacement drawn.";
                    }
                    tvReplacementResult.setText(label);
                    Toast.makeText(
                            getContext(),
                            "Replacement drawn successfully!",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                getContext(),
                                "Failed to update replacement: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    private Map<String, Object> findFirstEligible(
            List<Map<String, Object>> waitlist,
            List<Map<String, Object>> selected,
            List<Map<String, Object>> declined
    ) {
        if (waitlist == null || waitlist.isEmpty()) {
            return null;
        }
        for (Map<String, Object> candidate : waitlist) {
            if (candidate == null) continue;
            if (!containsEntrant(selected, candidate) && !containsEntrant(declined, candidate)) {
                return candidate;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private boolean containsEntrant(List<Map<String, Object>> list, Map<String, Object> target) {
        if (list == null || target == null) return false;

        String targetEmail = safeString(target.get("Entrant_email"));
        String targetId = safeString(target.get("Entrant_id"));

        for (Map<String, Object> e : list) {
            if (e == null) continue;
            String email = safeString(e.get("Entrant_email"));
            String id = safeString(e.get("Entrant_id"));
            if (!targetEmail.isEmpty() && targetEmail.equals(email)) {
                return true;
            }
            if (!targetId.isEmpty() && targetId.equals(id)) {
                return true;
            }
        }
        return false;
    }

    private String safeString(Object v) {
        return v instanceof String ? (String) v : "";
    }
}
