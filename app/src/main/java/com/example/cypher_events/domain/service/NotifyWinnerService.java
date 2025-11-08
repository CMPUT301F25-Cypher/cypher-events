package com.example.cypher_events.domain.service;

import com.example.cypher_events.domain.model.Notification;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.UUID;

public class NotifyWinnerService {

    private final DatabaseReference notificationsRef;

    public NotifyWinnerService() {
        // Use the getter to allow test injection
        notificationsRef = getNotificationsRef();
    }

    /**
     * Send winning notifications to entrants who won
     * @param winnerEntrantIds list of entrant IDs who won
     */
    public void sendWinningNotifications(List<String> winnerEntrantIds) {
        if (winnerEntrantIds == null || winnerEntrantIds.isEmpty()) return;

        for (String entrantId : winnerEntrantIds) {
            String title = "🎉 You Won the Lottery!";
            String message = "Congratulations! You've been selected.";

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
