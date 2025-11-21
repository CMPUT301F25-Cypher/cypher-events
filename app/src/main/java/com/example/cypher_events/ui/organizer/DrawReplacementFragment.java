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

    private void handleEventLoaded(DocumentSnapshot doc) {
        if (!doc.exists()) {
            Toast.makeText(getContext(), "Event not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("Entrants")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    java.util.ArrayList<String> waitlistIds = new java.util.ArrayList<>();
                    
                    for (DocumentSnapshot entrantDoc : querySnapshot.getDocuments()) {
                        @SuppressWarnings("unchecked")
                        List<String> joinedIds = (List<String>) entrantDoc.get("Entrant_joinedEventIDs");
                        @SuppressWarnings("unchecked")
                        List<String> selectedIds = (List<String>) entrantDoc.get("Entrant_selectedEventIDs");
                        @SuppressWarnings("unchecked")
                        List<String> acceptedIds = (List<String>) entrantDoc.get("Entrant_acceptedEventIDs");
                        @SuppressWarnings("unchecked")
                        List<String> declinedIds = (List<String>) entrantDoc.get("Entrant_declinedEventIDs");
                        
                        boolean hasJoined = joinedIds != null && joinedIds.contains(eventId);
                        boolean isSelected = selectedIds != null && selectedIds.contains(eventId);
                        boolean hasAccepted = acceptedIds != null && acceptedIds.contains(eventId);
                        boolean hasDeclined = declinedIds != null && declinedIds.contains(eventId);
                        
                        if (hasJoined && !isSelected && !hasAccepted && !hasDeclined) {
                            waitlistIds.add(entrantDoc.getId());
                        }
                    }
                    
                    if (waitlistIds.isEmpty()) {
                        tvReplacementResult.setText("No replacement available.");
                        Toast.makeText(getContext(), "No eligible replacement found.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    String replacementId = waitlistIds.get(0);
                    selectReplacement(replacementId);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load entrants: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }

    private void selectReplacement(String replacementId) {
        db.collection("Entrants").document(replacementId)
                .update("Entrant_selectedEventIDs", FieldValue.arrayUnion(eventId))
                .addOnSuccessListener(aVoid -> {
                    db.collection("Entrants").document(replacementId).get()
                            .addOnSuccessListener(doc -> {
                                String name = doc.getString("Entrant_name");
                                String email = doc.getString("Entrant_email");
                                
                                String label;
                                if (name != null && !name.isEmpty()) {
                                    label = "Replacement: " + name;
                                } else if (email != null && !email.isEmpty()) {
                                    label = "Replacement: " + email;
                                } else {
                                    label = "Replacement drawn successfully!";
                                }
                                
                                tvReplacementResult.setText(label);
                                Toast.makeText(getContext(), "Replacement drawn successfully!", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to select replacement: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }
}
