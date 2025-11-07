package com.example.cypher_events.domain.service;

import java.util.List;

public interface LotteryService {
    List<String> draw(List<String> entrantIds, int winnersCount, Long seed);
}
