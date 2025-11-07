package com.example.cypher_events.domain.service;

import java.util.*;

public class DefaultLotteryService implements LotteryService {
    @Override
    public List<String> draw(List<String> uids, int winners, Long seed) {
        if (uids == null) return Collections.emptyList();
        List<String> copy = new ArrayList<>(uids);
        Collections.shuffle(copy, (seed == null) ? new Random() : new Random(seed));
        int n = Math.min(winners, copy.size());
        return copy.subList(0, n);
    }
}
