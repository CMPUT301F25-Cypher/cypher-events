package com.example.cypher_events.domain.service;

import com.example.cypher_events.domain.model.Notification;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.UUID;

public class NotifyNotChosenService {

    private final DatabaseReference notificationsRef;

    public NotifyNotChosenService() {
        notificationsRef = getNotificationsRef();
    }

    /**
     * Send "not chosen" notifications to entrants who did not win
     * @param notChosenEntrantIds list of entrant IDs who did not win
     */
    public void sendNotChosenNotifications(List<String> notChosenEntrantIds) {
        if (notChosenEntrantIds == null || notChosenEntrantIds.isEmpty()) return;

        for (String entrantId : notChosenEntrantIds) {
            String title = "Better Luck Next Time";
            String message = "Unfortunately, you were not selected in the lottery.";

            Notification notification = new Notification(
                    UUID.randomUUID().toString(),
                    title,
                    message,
                    System.currentTimeMillis(),
                    entrantId
            );

            notificationsRef.child(notification.getId()).setValue(notification);
        }
    }

    /**
     * Protected getter for Firebase reference.
     * Can be overridden in tests to inject a mock DatabaseReference.
     */
    protected DatabaseReference getNotificationsRef() {
        return FirebaseDatabase.getInstance().getReference("notifications");
    }
}
