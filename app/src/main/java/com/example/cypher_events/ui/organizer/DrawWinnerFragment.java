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
import java.util.Random;

public class DrawWinnerFragment extends Fragment {

    private static final String ARG_EVENT_ID = "EventId";

    private Button btnGenerateWinner;
    private TextView tvWinnerResult;
    private ImageButton backButton;

    private FirebaseFirestore db;
    private String eventId;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_draw_winner, container, false);

        btnGenerateWinner = view.findViewById(R.id.btnGenerateWinner);
        tvWinnerResult = view.findViewById(R.id.tvWinnerResult);
        backButton = view.findViewById(R.id.btnBackWinner);

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

        if (btnGenerateWinner != null) {
            btnGenerateWinner.setOnClickListener(v -> drawWinner());
        }
    }

    private void drawWinner() {
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
                    
                    android.util.Log.d("DrawWinner", "Looking for eventId (doc): " + eventId);
                    android.util.Log.d("DrawWinner", "Looking for eventId (field): " + searchEventId);
                    android.util.Log.d("DrawWinner", "Total entrants: " + querySnapshot.size());
                    
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
                        
                        android.util.Log.d("DrawWinner", "Entrant: " + entrantDoc.getId());
                        android.util.Log.d("DrawWinner", "  joinedIds: " + joinedIds);
                        android.util.Log.d("DrawWinner", "  selectedIds: " + selectedIds);
                        android.util.Log.d("DrawWinner", "  acceptedIds: " + acceptedIds);
                        android.util.Log.d("DrawWinner", "  declinedIds: " + declinedIds);
                        
                        boolean hasJoined = !joinedIds.isEmpty() && (joinedIds.contains(eventId) || joinedIds.contains(searchEventId));
                        boolean isSelected = !selectedIds.isEmpty() && (selectedIds.contains(eventId) || selectedIds.contains(searchEventId));
                        boolean hasAccepted = !acceptedIds.isEmpty() && (acceptedIds.contains(eventId) || acceptedIds.contains(searchEventId));
                        boolean hasDeclined = !declinedIds.isEmpty() && (declinedIds.contains(eventId) || declinedIds.contains(searchEventId));
                        
                        android.util.Log.d("DrawWinner", "  hasJoined: " + hasJoined + ", isSelected: " + isSelected + ", hasAccepted: " + hasAccepted + ", hasDeclined: " + hasDeclined);
                        
                        if (hasJoined && !isSelected && !hasAccepted && !hasDeclined) {
                            waitlistIds.add(entrantDoc.getId());
                            android.util.Log.d("DrawWinner", "  -> ADDED TO WAITLIST");
                        }
                    }
                    
                    android.util.Log.d("DrawWinner", "Final waitlist size: " + waitlistIds.size());
                    
                    if (waitlistIds.isEmpty()) {
                        tvWinnerResult.setText("No entrants in waitlist.");
                        Toast.makeText(getContext(), "No entrants available to draw.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    String winnerId = pickRandom(waitlistIds);
                    selectWinner(winnerId);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load entrants: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }

    private String pickRandom(java.util.ArrayList<String> list) {
        if (list == null || list.isEmpty()) return null;
        Random rnd = new Random();
        int index = rnd.nextInt(list.size());
        return list.get(index);
    }

    private void selectWinner(String winnerId) {
        db.collection("Entrants").document(winnerId)
                .update("Entrant_selectedEventIDs", FieldValue.arrayUnion(eventId))
                .addOnSuccessListener(aVoid -> {
                    db.collection("Entrants").document(winnerId).get()
                            .addOnSuccessListener(doc -> {
                                String name = doc.getString("Entrant_name");
                                String email = doc.getString("Entrant_email");
                                
                                String label;
                                if (name != null && !name.isEmpty()) {
                                    label = "Winner: " + name;
                                } else if (email != null && !email.isEmpty()) {
                                    label = "Winner: " + email;
                                } else {
                                    label = "Winner drawn successfully!";
                                }
                                
                                tvWinnerResult.setText(label);
                                Toast.makeText(getContext(), "Winner drawn successfully!", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to select winner: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }
}
