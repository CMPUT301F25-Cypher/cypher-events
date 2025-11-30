package com.example.cypher_events.domain.service;

import com.example.cypher_events.domain.model.Notification;
import com.example.cypher_events.domain.model.Entrant;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;
import java.util.UUID;

/**
 * Writes "You won" notifications to Firestore collection "notifications"
 * and also writes a short log record into "notificationLogs".
 *
 * Uses Entrant.getEntrant_id() to target recipients.
 */
public class NotifyWinnerService {

    private final FirebaseFirestore db;
    private final CollectionReference notificationsCol;
    private final CollectionReference logsCol;

    public NotifyWinnerService() {
        db = FirebaseFirestore.getInstance();
        notificationsCol = db.collection("notifications");
        logsCol = db.collection("notificationLogs");
    }

    /**
     * Send a winner notification for each Entrant in winners.
     * Skips entrants who have Entrant_notificationsEnabled == false.
     */
    public void sendWinningNotifications(List<Entrant> winners, String eventId, String organizerId) {
        if (winners == null || winners.isEmpty()) return;
        long now = System.currentTimeMillis();

        for (Entrant e : winners) {
            try {
                Boolean enabled = e.isEntrant_notificationsEnabled();
                if (enabled != null && !enabled) continue;
            } catch (Exception ex) {
                // if field absent, assume enabled
            }

            String id = UUID.randomUUID().toString();
            Notification n = new Notification(
                    id,
                    e.getEntrant_id(),
                    organizerId,
                    eventId,
                    "🎉 You Won the Lottery!",
                    "Congratulations — you've been selected for event " + eventId + ".",
                    now,
                    false
            );

            DocumentReference dr = notificationsCol.document(id);
            dr.set(n.toMap());

            // small log entry: reusing same id
            logsCol.document(id).set(n.toMap());
        }
    }
}
