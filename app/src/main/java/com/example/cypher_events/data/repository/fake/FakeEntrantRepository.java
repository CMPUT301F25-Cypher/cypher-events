package com.example.cypher_events.data.repository.fake;

import com.example.cypher_events.data.repository.EntrantRepository;
import com.example.cypher_events.domain.model.Entrant;
import com.example.cypher_events.util.Result;

import java.util.HashMap;
import java.util.Map;

public class FakeEntrantRepository implements EntrantRepository {

    private final Map<String, Entrant> store = new HashMap<>();
    private final Map<String, Map<String, Boolean>> joined = new HashMap<>();
    // joined[entrantId][eventId] = true if joined

    public FakeEntrantRepository() {
        // Mock data
        store.put("jane@example.com", new Entrant("Jane", "jane@example.com", "111"));
        store.put("bob@example.com", new Entrant("Bob", "bob@example.com", "222"));
    }

    @Override
    public Result<Entrant> getEntrantByEmail(String email) {
        Entrant entrant = store.get(email);
        if (entrant != null) return Result.ok(entrant);
        return Result.err(new Exception("Entrant not found"));
    }

    @Override
    public Result<Boolean> deleteEntrant(String email) {
        if (store.remove(email) != null) return Result.ok(true);
        return Result.err(new Exception("Entrant not found"));
    }

    @Override
    public Result<Boolean> joinWaitlist(String entrantId, String eventId) {
        if (entrantId == null || eventId == null || entrantId.isEmpty() || eventId.isEmpty())
            return Result.err(new Exception("Invalid args"));

        joined.computeIfAbsent(entrantId, k -> new HashMap<>()).put(eventId, true);
        return Result.ok(true);
    }

    @Override
    public Result<Boolean> leaveWaitlist(String entrantId, String eventId) {
        if (entrantId == null || eventId == null || entrantId.isEmpty() || eventId.isEmpty())
            return Result.err(new Exception("Invalid args"));

        Map<String, Boolean> events = joined.get(entrantId);
        if (events != null) {
            events.remove(eventId);
            return Result.ok(true);
        }
        return Result.err(new Exception("Not found in waitlist"));
    }

    @Override
    public Result<Boolean> isJoined(String entrantId, String eventId) {
        Map<String, Boolean> events = joined.get(entrantId);
        if (events != null && Boolean.TRUE.equals(events.get(eventId))) {
            return Result.ok(true);
        }
        return Result.ok(false);
    }

    // Optional helper for tests
    public void addEntrant(Entrant entrant) {
        store.put(entrant.getEntrant_email(), entrant);
    }
}