package com.example.cypher_events.domain.service;

import com.example.cypher_events.domain.model.Notification;
import com.example.cypher_events.domain.model.Entrant;
import com.example.cypher_events.domain.model.Event;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;
import java.util.UUID;

/**
 * General-purpose notification helpers for organizers to send:
 * - sendToEntrants
 * - sendToAllEntrantsOfEvent
 * - sendToSelectedEntrants
 * - sendToCancelledEntrants
 */
public class NotificationService {

    private final FirebaseFirestore db;
    private final CollectionReference notificationsCol;
    private final CollectionReference logsCol;

    public NotificationService() {
        db = FirebaseFirestore.getInstance();
        notificationsCol = db.collection("notifications");
        logsCol = db.collection("notificationLogs");
    }

    public void sendToEntrants(List<Entrant> entrants, String title, String message, String eventId, String organizerId) {
        if (entrants == null || entrants.isEmpty()) return;
        long now = System.currentTimeMillis();

        for (Entrant e : entrants) {
            try {
                Boolean enabled = e.isEntrant_notificationsEnabled();
                if (enabled != null && !enabled) continue;
            } catch (Exception ex) {
                // if field absent, assume enabled
            }

            String recipientId = e.getEntrant_id();      // may be null when loaded from Event
            String recipientEmail = e.getEntrant_email(); // always present from Event.toMap()

            String id = UUID.randomUUID().toString();
            Notification n = new Notification(
                    id,
                    recipientId,
                    recipientEmail,
                    organizerId,
                    eventId,
                    title,
                    message,
                    now,
                    false
            );

            DocumentReference dr = notificationsCol.document(id);
            dr.set(n.toMap());
            logsCol.document(id).set(n.toMap());
        }
    }

    public void sendToAllEntrantsOfEvent(Event event, String title, String message, String organizerId) {
        sendToEntrants(event.getEvent_waitlistEntrants(), title, message, event.getEvent_id(), organizerId);
    }

    public void sendToSelectedEntrants(Event event, String title, String message, String organizerId) {
        sendToEntrants(event.getEvent_selectedEntrants(), title, message, event.getEvent_id(), organizerId);
    }

    public void sendToCancelledEntrants(Event event, String title, String message, String organizerId) {
        sendToEntrants(event.getEvent_declinedEntrants(), title, message, event.getEvent_id(), organizerId);
    }
}
