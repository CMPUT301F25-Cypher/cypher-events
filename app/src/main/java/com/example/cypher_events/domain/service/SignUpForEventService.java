package com.example.cypher_events.domain.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SignUpForEventService {


    private final Map<String, String> deviceToUser = new HashMap<>();


    private final Map<String, Set<String>> eventToUsers = new HashMap<>();


    /**
     * @param deviceId device identifier
     * @param userId user identifier
     */
    public void registerDeviceUser(String deviceId, String userId) {
        if (deviceId != null && userId != null) {
            deviceToUser.put(deviceId, userId);
        }
    }


    /**
     * @param deviceId device identifier
     * @param eventId event identifier
     * @return status message indicating result
     */
    public String signUp(String deviceId, String eventId) {


        if (deviceId == null || deviceId.isEmpty()
                || eventId == null || eventId.isEmpty()) {
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


    /**
     * @param eventId event identifier
     * @param userId user identifier
     * @return true if user signed up for event, false otherwise
     */
    public boolean isSignedUp(String eventId, String userId) {
        Set<String> users = eventToUsers.get(eventId);
        return users != null && users.contains(userId);
    }
}
