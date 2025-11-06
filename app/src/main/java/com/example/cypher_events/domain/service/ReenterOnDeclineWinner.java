package com.example.cypher_events.domain.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Given all entrants and the declined winner, notify everyone else.
 */
public class ReenterOnDeclineWinner {

    public interface ReentryHandler {
        void openReentryScreen(String userId);
    }

    /**
     * Use this if you already have the entrants list.
     * @param entrantUserIds all entrants (including winner)
     * @param winnerUserId   the user who declined
     * @param handler        callback to "notify" each remaining user
     * @return list of userIds that were notified
     */
    public List<String> reenterIfWinnerDeclines(
            List<String> entrantUserIds,
            String winnerUserId,
            ReentryHandler handler
    ) {
        List<String> notified = new ArrayList<>();
        if (entrantUserIds == null || entrantUserIds.isEmpty()) {
            return notified;
        }

        // Build a list without the winner
        List<String> remaining = new ArrayList<>();
        for (String uid : entrantUserIds) {
            if (uid != null && !uid.equals(winnerUserId)) {
                remaining.add(uid);
            }
        }

        // notify each remaining entrant
        for (String uid : remaining) {
            notified.add(uid);
            if (handler != null) {
                handler.openReentryScreen(uid);
            }
        }
        return notified;
    }
}
