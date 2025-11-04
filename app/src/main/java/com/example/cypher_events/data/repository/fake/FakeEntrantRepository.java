package com.example.cypher_events.data.repository.fake;

import com.example.cypher_events.Result;
import com.example.cypher_events.data.repository.EntrantRepository;
import java.util.*;

public class FakeEntrantRepository implements EntrantRepository {
    private final Map<String, Set<String>> waitlists = new HashMap<>();

    @Override public Result<Boolean> joinWaitlist(String eventId, String uid) {
        waitlists.computeIfAbsent(eventId, k -> new HashSet<>()).add(uid);
        return Result.ok(true);
    }

    @Override public Result<Boolean> leaveWaitlist(String eventId, String uid) {
        waitlists.computeIfAbsent(eventId, k -> new HashSet<>()).remove(uid);
        return Result.ok(true);
    }

    @Override public Result<Boolean> isJoined(String eventId, String uid) {
        boolean joined = waitlists.getOrDefault(eventId, Set.of()).contains(uid);
        return Result.ok(joined);
    }
}
