package com.example.cypher_events.domain.service;

import java.util.ArrayList;
import java.util.List;

public class LotteryResultService {

    private final DefaultLotteryService lotteryService = new DefaultLotteryService();
    private final NotifyWinnerService winningService = new NotifyWinnerService();
    private final NotifyNotChosenService notChosenService = new NotifyNotChosenService();

    /**
     * Execute lottery and notify entrants.
     * @param allEntrants list of all entrant IDs
     * @param numberOfWinners number of winners to select
     */
    public void executeLottery(List<String> allEntrants, int numberOfWinners) {
        if (allEntrants == null || allEntrants.isEmpty()) return;

        // Draw winners
        List<String> winners = lotteryService.draw(allEntrants, numberOfWinners, null);

        // Notify winners
        winningService.sendWinningNotifications(winners);

        // Notify not chosen entrants
        List<String> notChosen = new ArrayList<>(allEntrants);
        notChosen.removeAll(winners);
        notChosenService.sendNotChosenNotifications(notChosen);
    }
}
