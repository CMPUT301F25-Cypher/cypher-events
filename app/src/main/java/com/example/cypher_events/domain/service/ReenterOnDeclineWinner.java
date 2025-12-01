package com.example.cypher_events.domain.service;

import java.util.ArrayList;
import java.util.List;

public class ReenterOnDeclineWinner {


    public interface ReentryHandler {
        /**
         * @param userId the user ID to re-enter
         */
        void openReentryScreen(String userId);
    }


    public List<String> reenterIfWinnerDeclines(
            List<String> entrantUserIds,
            String winnerUserId,
            ReentryHandler handler
    ) {
        /**
         * @param entrantUserIds list of entrant user IDs
         * @param winnerUserId the declined winner's user ID
         * @param handler callback handler for reentry
         * @return list of user IDs notified for reentry
         */


        List<String> notified = new ArrayList<>();

        if (entrantUserIds == null || entrantUserIds.isEmpty()) {
            return notified;
        }


        for (String uid : entrantUserIds) {


            if (uid == null || uid.equals(winnerUserId)) {
                continue;
            }


            notified.add(uid);


            if (handler != null) {
                handler.openReentryScreen(uid);
            }
        }


        return notified;
    }
}