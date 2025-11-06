package com.example.cypher_events.domain.service;

import java.util.HashMap;
import java.util.Map;

public class AcceptInvitationService {

    private final Map<String, String> deviceToUser = new HashMap<>();
    private final Map<String, Map<String, Boolean>> accepted = new HashMap<>();

    public void registerDeviceUser(String deviceId, String userId) {
        deviceToUser.put(deviceId, userId);
    }

    public String acceptInvitation(String deviceId, String eventId) {
        if (deviceId == null || deviceId.isEmpty() || eventId == null || eventId.isEmpty()) {
            return "Invalid device or event.";
        }
        String userId = deviceToUser.get(deviceId);
        if (userId == null) return "No user for device.";

        Map<String, Boolean> Events = accepted.get(userId);
        if (Events == null) {
            Events = new HashMap<>();
            accepted.put(userId, Events);
        }
        if (Boolean.TRUE.equals(Events.get(eventId))) return "Already accepted.";

        Events.put(eventId, true);
        return "Invitation accepted.";
    }

    public boolean isAccepted(String userId, String eventId) {
        Map<String, Boolean> Events = accepted.get(userId);
        return Events != null && Boolean.TRUE.equals(Events.get(eventId));
    }
}