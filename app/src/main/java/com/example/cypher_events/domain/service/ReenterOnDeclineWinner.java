package com.example.cypher_events.domain.service;

import java.util.ArrayList;
import java.util.List;

public class ReenterOnDeclineWinner {

    // Callback interface for notifying each user
    public interface ReentryHandler {
        void openReentryScreen(String userId);
    }

    // Notify all entrants except the declining winner
    public List<String> reenterIfWinnerDeclines(
            List<String> entrantUserIds,
            String winnerUserId,
            ReentryHandler handler
    ) {

        // List of notified userIds
        List<String> notified = new ArrayList<>();

        // Validate entrant list
        if (entrantUserIds == null || entrantUserIds.isEmpty()) {
            return notified;
        }

        // Loop through entrants and skip the winner
        for (String uid : entrantUserIds) {

            // Skip null ids and skip winner id
            if (uid == null || uid.equals(winnerUserId)) {
                continue;
            }

            // Add this user to the notified list
            notified.add(uid);

            // Trigger callback for UI or test mock
            if (handler != null) {
                handler.openReentryScreen(uid);
            }
        }

        // Return list of notified users
        return notified;
    }
}
