package com.example.cypher_events.domain.service;

import com.google.firebase.firestore.FirebaseFirestore;
import com.example.cypher_events.domain.model.Notification;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NotificationService {
    private final FirebaseFirestore db;

    public NotificationService() {
        db = FirebaseFirestore.getInstance();
    }

    public void sendWinnerNotification(String entrantId, String eventName) {
        String title = "Congratulations!";
        String message = "You won the lottery for " + eventName;

        Notification notification = new Notification(
                UUID.randomUUID().toString(),
                title,
                message,
                System.currentTimeMillis()
        );

        db.collection("entrants")
                .document(entrantId)
                .collection("notifications")
                .document(notification.getId())
                .set(notification.toMap());
    }
}
