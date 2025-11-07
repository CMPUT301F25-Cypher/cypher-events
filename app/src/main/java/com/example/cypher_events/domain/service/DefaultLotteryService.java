package com.example.cypher_events.domain.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DefaultLotteryService implements LotteryService {

    @Override
    public List<String> draw(List<String> entrantIds, int winnersCount, Long seed) {
        if (entrantIds == null) return Collections.emptyList();

        List<String> copy = new ArrayList<>(entrantIds);
        Collections.shuffle(copy, (seed == null) ? new Random() : new Random(seed));
        int n = Math.min(winnersCount, copy.size());

        return copy.subList(0, n);
    }
}
