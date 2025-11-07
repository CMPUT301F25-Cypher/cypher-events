package com.example.cypher_events.domain.service;

import java.util.HashMap;
import java.util.Map;
/**
 * US 02.01.04: "As an organizer, I want to set a registration period for my event." */
public class RegistrationPeriodService {

    // eventId -> [startUtcMillis, endUtcMillis]
    private final Map<String, long[]> windows = new HashMap<>();

    /**
     * Set or update the registration window for an event.
     * @param eventId non-empty
     * @param startUtcMillis non-null
     * @param endUtcMillis non-null and >= startUtcMillis
     * @return status message
     */
    public String setRegistrationWindow(String eventId, Long startUtcMillis, Long endUtcMillis) {
        if (eventId == null || eventId.trim().isEmpty()
                || startUtcMillis == null || endUtcMillis == null) {
            return "Invalid event or times.";
        }
        if (endUtcMillis < startUtcMillis) {
            return "End must be after start.";
        }

        boolean existed = windows.containsKey(eventId);
        windows.put(eventId, new long[]{startUtcMillis, endUtcMillis});
        return existed ? "Window updated." : "Window set.";
    }

    public boolean isWindowSet(String eventId) {
        return windows.containsKey(eventId);
    }

    public long getStartUtc(String eventId) {
        long[] w = windows.get(eventId);
        return (w == null) ? 0L : w[0];
    }

    public long getEndUtc(String eventId) {
        long[] w = windows.get(eventId);
        return (w == null) ? 0L : w[1];
    }

    /** Is the window currently open at the given instant? */
    public boolean isOpenAt(String eventId, long nowUtcMillis) {
        long[] w = windows.get(eventId);
        if (w == null) return false;
        return w[0] <= nowUtcMillis && nowUtcMillis <= w[1];
    }
}

