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
import com.example.cypher_events.domain.model.Notification;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

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

        // Get the Event_id field and capacity
        String actualEventId = doc.getString("Event_id");
        if (actualEventId == null || actualEventId.isEmpty()) {
            actualEventId = eventId; // Fallback to document ID
        }

        // Get event capacity - this is the number of winners to draw
        Object capacityObj = doc.get("Event_capacity");
        int capacity = 0;
        if (capacityObj instanceof Number) {
            capacity = ((Number) capacityObj).intValue();
        }
        
        if (capacity <= 0) {
            Toast.makeText(getContext(), "Event has no capacity set", Toast.LENGTH_SHORT).show();
            return;
        }

        final String searchEventId = actualEventId;
        final int numberOfWinners = capacity;

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

                    // Determine how many winners to select
                    int winnersToSelect = Math.min(numberOfWinners, waitlistIds.size());
                    
                    // Select multiple winners
                    java.util.ArrayList<String> selectedWinners = pickMultipleRandom(waitlistIds, winnersToSelect);
                    selectMultipleWinners(selectedWinners, winnersToSelect, waitlistIds.size());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load entrants: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }

    private java.util.ArrayList<String> pickMultipleRandom(java.util.ArrayList<String> list, int count) {
        if (list == null || list.isEmpty()) return new java.util.ArrayList<>();
        
        java.util.ArrayList<String> shuffled = new java.util.ArrayList<>(list);
        java.util.Collections.shuffle(shuffled);
        
        int actualCount = Math.min(count, shuffled.size());
        return new java.util.ArrayList<>(shuffled.subList(0, actualCount));
    }

    private void selectMultipleWinners(java.util.ArrayList<String> winnerIds, int selected, int totalWaitlist) {
        if (winnerIds == null || winnerIds.isEmpty()) {
            Toast.makeText(getContext(), "No winners could be selected.", Toast.LENGTH_SHORT).show();
            return;
        }

        WriteBatch batch = db.batch();
        
        // Mark all selected entrants as winners
        for (String winnerId : winnerIds) {
            batch.update(
                db.collection("Entrants").document(winnerId),
                "Entrant_selectedEventIDs", 
                FieldValue.arrayUnion(eventId)
            );
        }

        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    // Show result
                    String resultText = "Drew " + selected + " winner(s) from " + totalWaitlist + " entrants";
                    tvWinnerResult.setText(resultText);
                    Toast.makeText(getContext(), resultText, Toast.LENGTH_LONG).show();

                    // Send notifications to all winners
                    for (String winnerId : winnerIds) {
                        sendWinnerNotificationToEntrant(winnerId);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to select winners: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }

    private void sendWinnerNotificationToEntrant(String winnerId) {
        db.collection("Entrants").document(winnerId).get()
                .addOnSuccessListener(doc -> {
                    String email = doc.getString("Entrant_email");
                    Boolean enabledObj = doc.getBoolean("Entrant_notificationsEnabled");
                    boolean notificationsEnabled = (enabledObj == null) || enabledObj;
                    
                    sendWinnerNotification(winnerId, email, notificationsEnabled);
                });
    }

    /**
     * NEW: Writes a "You won" notification (and log) into Firestore.
     * This will auto-create the "notifications" and "notificationLogs" collections.
     */
    private void sendWinnerNotification(String winnerId, String winnerEmail, boolean notificationsEnabled) {
        if (!notificationsEnabled) return;
        if (winnerEmail == null || winnerEmail.isEmpty()) return;

        String id = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();

        // senderOrganizerId is left null here; you can set it if you track organizer IDs.
        Notification n = new Notification(
                id,
                winnerId,                     // recipientEntrantId
                winnerEmail,                  // recipientEmail
                null,                         // senderOrganizerId (optional)
                eventId,                      // eventId
                "🎉 You Won the Lottery!",
                "Congratulations — you've been selected for the event.",
                now,
                false
        );

        db.collection("notifications").document(id).set(n.toMap());
        db.collection("notificationLogs").document(id).set(n.toMap());
    }
}
