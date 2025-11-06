package com.example.cypher_events.domain.service;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
"As an entrant, I want to sign up for an event from details view".
 */
public class SignUpForEventService {

    // demo mapping: deviceId -> userId
    private final Map<String, String> deviceToUser = new HashMap<>();

    // demo waitlist: eventId -> unique userIds
    private final Map<String, Set<String>> eventToUsers = new HashMap<>();

    /**bind a device to a user */
    public void registerDeviceUser(String deviceId, String userId) {
        if (deviceId != null && userId != null) {
            deviceToUser.put(deviceId, userId);
        }
    }

    /**
     * Sign the device's user up for the event (adds to waitlist).
     */
    public String signUp(String deviceId, String eventId) {
        if (deviceId == null || deviceId.isEmpty() || eventId == null || eventId.isEmpty()) {
            return "Invalid device or event.";
        }

        String userId = deviceToUser.get(deviceId);
        if (userId == null) {
            return "No user for device.";
        }

        Set<String> users = eventToUsers.get(eventId);
        if (users == null) {
            users = new HashSet<>();
            eventToUsers.put(eventId, users);
        }

        if (users.contains(userId)) {
            return "Already signed up.";
        }

        users.add(userId);
        return "Signed up for event.";
    }

    /** For tests check if a user is on the waitlist. */
    public boolean isSignedUp(String eventId, String userId) {
        Set<String> users = eventToUsers.get(eventId);
        return users != null && users.contains(userId);
    }
}
