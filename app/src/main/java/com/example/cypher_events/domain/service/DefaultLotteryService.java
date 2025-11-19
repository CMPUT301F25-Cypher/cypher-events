package com.example.cypher_events.domain.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DefaultLotteryService implements LotteryService {

    @Override
    public List<String> draw(List<String> uids, int winners, Long seed) {

        // If no entrants or invalid winner count, return empty list
        if (uids == null || uids.isEmpty() || winners <= 0) {
            return Collections.emptyList();
        }

        // Copy list so we never mutate the caller's list
        List<String> copy = new ArrayList<>(uids);

        // Use seeded RNG when provided, otherwise a fresh Random
        Random random = (seed == null) ? new Random() : new Random(seed);

        // Shuffle copy in-place
        Collections.shuffle(copy, random);

        // Number of winners cannot exceed list size
        int n = Math.min(winners, copy.size());

        // Return a new list containing the winners
        return new ArrayList<>(copy.subList(0, n));
    }
}
