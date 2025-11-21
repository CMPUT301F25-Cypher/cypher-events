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
