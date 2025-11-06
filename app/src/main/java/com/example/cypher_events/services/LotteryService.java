package com.example.cypher_events.services;

import com.example.cypher_events.models.Event;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Service for handling lottery drawing and replacement selection
 * Implements US 02.05.02, US 02.05.03, US 02.06.03
 *
 * Outstanding issues: Need to add notification triggering after selection
 */
public class LotteryService {

    private FirebaseFirestore db;
    private Random random;

    public LotteryService() {
        this.db = FirebaseFirestore.getInstance();
        this.random = new Random();
    }

    /**
     * Draws a specified number of entrants from the waiting list
     * US 02.05.02 - Sample specified number of attendees
     *
     * @param eventId The event ID
     * @param numberOfWinners Number of entrants to select
     * @param callback Callback with success/failure result
     */
    public void drawEntrants(String eventId, int numberOfWinners, LotteryCallback callback) {
        db.collection("events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Event event = documentSnapshot.toObject(Event.class);
                    if (event == null) {
                        callback.onFailure("Event not found");
                        return;
                    }

                    List<String> waitingList = event.getWaitingListIds();
                    if (waitingList == null || waitingList.isEmpty()) {
                        callback.onFailure("No entrants in waiting list");
                        return;
                    }

                    // Check if we have enough entrants
                    int availableEntrants = waitingList.size();
                    int actualWinners = Math.min(numberOfWinners, availableEntrants);

                    // Shuffle and select
                    List<String> shuffled = new ArrayList<>(waitingList);
                    Collections.shuffle(shuffled, random);
                    List<String> selectedEntrants = shuffled.subList(0, actualWinners);

                    // Update event with selected entrants
                    db.collection("events").document(eventId)
                            .update(
                                    "selectedEntrantIds", FieldValue.arrayUnion(selectedEntrants.toArray()),
                                    "selectedCount", FieldValue.increment(actualWinners)
                            )
                            .addOnSuccessListener(aVoid -> {
                                // Update each entrant's status
                                updateEntrantStatus(selectedEntrants, eventId, "selected");
                                callback.onSuccess(selectedEntrants);
                            })
                            .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    /**
     * Draws a replacement entrant when someone declines or cancels
     * US 02.06.03 - Draw replacement from pooling system
     *
     * @param eventId The event ID
     * @param callback Callback with the selected replacement entrant
     */
    public void drawReplacement(String eventId, ReplacementCallback callback) {
        db.collection("events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Event event = documentSnapshot.toObject(Event.class);
                    if (event == null) {
                        callback.onFailure("Event not found");
                        return;
                    }

                    // Get eligible entrants (in waiting list but not selected, declined, or cancelled)
                    List<String> waitingList = event.getWaitingListIds();
                    List<String> selectedList = event.getSelectedEntrantIds();
                    List<String> declinedList = event.getDeclinedEntrantIds();
                    List<String> cancelledList = event.getCancelledEntrantIds();

                    if (waitingList == null || waitingList.isEmpty()) {
                        callback.onFailure("No entrants in waiting list");
                        return;
                    }

                    // Filter out already selected, declined, and cancelled
                    List<String> eligible = new ArrayList<>(waitingList);
                    if (selectedList != null) eligible.removeAll(selectedList);
                    if (declinedList != null) eligible.removeAll(declinedList);
                    if (cancelledList != null) eligible.removeAll(cancelledList);

                    if (eligible.isEmpty()) {
                        callback.onFailure("No eligible entrants available for replacement");
                        return;
                    }

                    // Randomly select one
                    String replacement = eligible.get(random.nextInt(eligible.size()));

                    // Update event - add to selected list
                    db.collection("events").document(eventId)
                            .update(
                                    "selectedEntrantIds", FieldValue.arrayUnion(replacement),
                                    "selectedCount", FieldValue.increment(1)
                            )
                            .addOnSuccessListener(aVoid -> {
                                // Update entrant status
                                updateSingleEntrantStatus(replacement, eventId, "selected");
                                callback.onSuccess(replacement);
                            })
                            .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    /**
     * Updates the status of multiple entrants
     */
    private void updateEntrantStatus(List<String> entrantIds, String eventId, String newStatus) {
        for (String entrantId : entrantIds) {
            db.collection("entrants")
                    .whereEqualTo("userId", entrantId)
                    .whereEqualTo("eventId", eventId)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            String docId = querySnapshot.getDocuments().get(0).getId();
                            db.collection("entrants").document(docId)
                                    .update("status", newStatus);
                        }
                    });
        }
    }

    /**
     * Updates the status of a single entrant
     */
    private void updateSingleEntrantStatus(String entrantId, String eventId, String newStatus) {
        db.collection("entrants")
                .whereEqualTo("userId", entrantId)
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        String docId = querySnapshot.getDocuments().get(0).getId();
                        db.collection("entrants").document(docId)
                                .update("status", newStatus);
                    }
                });
    }

    /**
     * Callback interface for lottery drawing
     */
    public interface LotteryCallback {
        void onSuccess(List<String> selectedEntrants);
        void onFailure(String error);
    }

    /**
     * Callback interface for replacement drawing
     */
    public interface ReplacementCallback {
        void onSuccess(String replacementEntrantId);
        void onFailure(String error);
    }
}