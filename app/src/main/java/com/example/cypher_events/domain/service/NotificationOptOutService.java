package com.example.cypher_events.domain.service;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Toggle Entrant_notificationsEnabled for an Entrant.
 * Uses collection "Entrants" (capital E) and documentId == Entrant_id (deviceId).
 */
public class NotificationOptOutService {

    private final FirebaseFirestore db;

    public NotificationOptOutService() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Set the Entrant_notificationsEnabled flag for the entrant with given Entrant_id.
     */
    public void setEnabled(String entrantId, boolean enabled) {
        if (entrantId == null) return;

        Map<String, Object> patch = new HashMap<>();
        patch.put("Entrant_notificationsEnabled", enabled);

        db.collection("Entrants")
                .document(entrantId)
                .set(patch, SetOptions.merge());
    }
}
