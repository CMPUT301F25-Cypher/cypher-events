package com.example.cypher_events.domain.service;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Toggle Entrant_notificationsEnabled for an Entrant.
 */
public class NotificationOptOutService {

    private final FirebaseFirestore db;

    public NotificationOptOutService() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Set the Entrant_notificationsEnabled flag for the entrant with given Entrant_id.
     * Finds the Firestore document where field "Entrant_id" == entrantId and updates it
     */
    public void setEnabled(String entrantId, boolean enabled) {
        if (entrantId == null) return;
        Query q = db.collection("entrants").whereEqualTo("Entrant_id", entrantId).limit(1);
        q.get().addOnSuccessListener((QuerySnapshot snap) -> {
            if (snap != null && !snap.isEmpty()) {
                String docId = snap.getDocuments().get(0).getId();
                Map<String,Object> patch = new HashMap<>();
                patch.put("Entrant_notificationsEnabled", enabled);
                db.collection("entrants").document(docId).set(patch, SetOptions.merge());
            }
        });
    }
}