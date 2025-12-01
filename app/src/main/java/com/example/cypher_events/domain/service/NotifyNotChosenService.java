package com.example.cypher_events.domain.service;

import com.example.cypher_events.domain.model.Notification;
import com.example.cypher_events.domain.model.Entrant;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;
import java.util.UUID;

/**
 * Send "not chosen" notifications (losers of lottery)
 * Uses Entrant_email as primary routing field.
 */
public class NotifyNotChosenService {

    private final FirebaseFirestore db;
    private final CollectionReference notificationsCol;
    private final CollectionReference logsCol;

    public NotifyNotChosenService() {
        db = FirebaseFirestore.getInstance();
        notificationsCol = db.collection("notifications");
        logsCol = db.collection("notificationLogs");
    }

    public void sendNotChosenNotifications(List<Entrant> notChosen, String eventId, String organizerId) {
        if (notChosen == null || notChosen.isEmpty()) return;
        long now = System.currentTimeMillis();

        for (Entrant e : notChosen) {
            try {
                Boolean enabled = e.isEntrant_notificationsEnabled();
                if (enabled != null && !enabled) continue;
            } catch (Exception ex) {
                // if field absent, assume enabled
            }

            String recipientId = e.getEntrant_id();       // may be null
            String recipientEmail = e.getEntrant_email(); // should be non-null

            String id = UUID.randomUUID().toString();
            Notification n = new Notification(
                    id,
                    recipientId,
                    recipientEmail,
                    organizerId,
                    eventId,
                    "Lottery Result",
                    "Sorry — you were not selected for event " + eventId + ".",
                    now,
                    false
            );

            DocumentReference dr = notificationsCol.document(id);
            dr.set(n.toMap());
            logsCol.document(id).set(n.toMap());
        }
    }
}
