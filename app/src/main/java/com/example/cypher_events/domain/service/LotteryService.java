package com.example.cypher_events.domain.service;

import java.util.List;

public interface LotteryService {
    List<String> draw(List<String> waitlistUids, int winnersCount, Long seed);
}
