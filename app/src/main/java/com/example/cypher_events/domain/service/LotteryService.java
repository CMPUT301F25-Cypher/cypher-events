package com.example.cypher_events.domain.service;

import java.util.List;

public interface LotteryService {

    // Draw a list of winner IDs from the waitlist
    // waitlistUids  -> list of entrant IDs in the waiting list
    // winnersCount  -> how many winners to select
    // seed          -> optional seed for deterministic randomness (can be null)
    List<String> draw(List<String> waitlistUids, int winnersCount, Long seed);
}
