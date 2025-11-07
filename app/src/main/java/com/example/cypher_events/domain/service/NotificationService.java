package com.example.cypher_events.domain.service;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class NotificationService {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void sendNotification(String userId, String title, String message) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", title);
        notification.put("message", message);
        notification.put("timestamp", System.currentTimeMillis());
        notification.put("isRead", false);

        db.collection("users")
                .document(userId)
                .collection("notifications")
                .add(notification);
    }
}
