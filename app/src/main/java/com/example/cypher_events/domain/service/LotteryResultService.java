package com.example.cypher_events.domain.service;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class LotteryResultService {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final LotteryService lotteryService = new DefaultLotteryService();
    private final NotificationService notificationService = new NotificationService();

    public void executeLottery(String eventId, List<String> entrantUids, int winnersCount, Long seed) {
        if (entrantUids == null || entrantUids.isEmpty()) return;

        // Draw random winners
        List<String> winners = lotteryService.draw(entrantUids, winnersCount, seed);

        // Notify and update Firestore
        for (String uid : entrantUids) {
            boolean isWinner = winners.contains(uid);
            updateEntrantStatus(eventId, uid, isWinner ? "winner" : "not_chosen");

            if (isWinner) {
                notificationService.sendNotification(
                        uid,
                        "🎉 You Won the Lottery!",
                        "Congratulations! You've been selected for event " + eventId + "."
                );
            } else {
                notificationService.sendNotification(
                        uid,
                        "Better Luck Next Time",
                        "Unfortunately, you were not selected for event " + eventId + "."
                );
            }
        }
    }

    private void updateEntrantStatus(String eventId, String userId, String status) {
        db.collection("events")
                .document(eventId)
                .collection("entrants")
                .document(userId)
                .update("status", status);
    }
}
