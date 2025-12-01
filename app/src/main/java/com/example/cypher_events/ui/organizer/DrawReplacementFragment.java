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
                    requireActivity().getSupportFragmentManager().popBackStack()
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

        // Get the Event_id field from the document (e.g., "EVTB6E40")
        String actualEventId = doc.getString("Event_id");
        if (actualEventId == null || actualEventId.isEmpty()) {
            actualEventId = eventId; // Fallback to document ID
        }
        
        final String searchEventId = actualEventId;

        db.collection("Entrants")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    java.util.ArrayList<String> waitlistIds = new java.util.ArrayList<>();
                    
                    for (DocumentSnapshot entrantDoc : querySnapshot.getDocuments()) {
                        List<String> joinedIds = new java.util.ArrayList<>();
                        Object joinedObj = entrantDoc.get("Entrant_joinedEventIDs");
                        if (joinedObj instanceof List) {
                            List<?> tempList = (List<?>) joinedObj;
                            for (Object item : tempList) {
                                if (item != null) joinedIds.add(item.toString());
                            }
                        } else if (joinedObj instanceof Map) {
                            joinedIds.addAll(((Map<String, Object>) joinedObj).keySet());
                        }

                        List<String> selectedIds = new java.util.ArrayList<>();
                        Object selectedObj = entrantDoc.get("Entrant_selectedEventIDs");
                        if (selectedObj instanceof List) {
                            List<?> tempList = (List<?>) selectedObj;
                            for (Object item : tempList) {
                                if (item != null) selectedIds.add(item.toString());
                            }
                        } else if (selectedObj instanceof Map) {
                            selectedIds.addAll(((Map<String, Object>) selectedObj).keySet());
                        }

                        List<String> acceptedIds = new java.util.ArrayList<>();
                        Object acceptedObj = entrantDoc.get("Entrant_acceptedEventIDs");
                        if (acceptedObj instanceof List) {
                            List<?> tempList = (List<?>) acceptedObj;
                            for (Object item : tempList) {
                                if (item != null) acceptedIds.add(item.toString());
                            }
                        } else if (acceptedObj instanceof Map) {
                            acceptedIds.addAll(((Map<String, Object>) acceptedObj).keySet());
                        }

                        List<String> declinedIds = new java.util.ArrayList<>();
                        Object declinedObj = entrantDoc.get("Entrant_declinedEventIDs");
                        if (declinedObj instanceof List) {
                            List<?> tempList = (List<?>) declinedObj;
                            for (Object item : tempList) {
                                if (item != null) declinedIds.add(item.toString());
                            }
                        } else if (declinedObj instanceof Map) {
                            declinedIds.addAll(((Map<String, Object>) declinedObj).keySet());
                        }
                        
                        boolean hasJoined = !joinedIds.isEmpty() && (joinedIds.contains(eventId) || joinedIds.contains(searchEventId));
                        boolean isSelected = !selectedIds.isEmpty() && (selectedIds.contains(eventId) || selectedIds.contains(searchEventId));
                        boolean hasAccepted = !acceptedIds.isEmpty() && (acceptedIds.contains(eventId) || acceptedIds.contains(searchEventId));
                        boolean hasDeclined = !declinedIds.isEmpty() && (declinedIds.contains(eventId) || declinedIds.contains(searchEventId));
                        
                        // Eligible for replacement: on waiting list, not selected, not accepted, not declined
                        // People who declined are excluded from future draws
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
