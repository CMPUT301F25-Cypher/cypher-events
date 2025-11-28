package com.example.cypher_events.domain.service;

import java.util.HashMap;
import java.util.Map;

public class DeclineInvitationService {

    // deviceId -> userId
    private final Map<String, String> deviceToUser = new HashMap<>();

    // userId -> (eventId -> declined?)
    private final Map<String, Map<String, Boolean>> declined = new HashMap<>();

    // register a device to a user
    public void registerDeviceUser(String deviceId, String userId) {
        if (deviceId == null || userId == null) {
            return;
        }
        deviceToUser.put(deviceId, userId);
    }

    // decline an invitation for the user associated with this device
    public String declineInvitation(String deviceId, String eventId) {
        if (deviceId == null || deviceId.isEmpty()
                || eventId == null || eventId.isEmpty()) {
            return "Invalid device or event.";
        }

        String userId = deviceToUser.get(deviceId);
        if (userId == null) {
            return "No user for device.";
        }

        Map<String, Boolean> eventsForUser = declined.get(userId);
        if (eventsForUser == null) {
            eventsForUser = new HashMap<>();
            declined.put(userId, eventsForUser);
        }

        if (Boolean.TRUE.equals(eventsForUser.get(eventId))) {
            return "Already declined.";
        }

        eventsForUser.put(eventId, true);
        return "Invitation declined.";
    }

    // check if user has declined an event
    public boolean isDeclined(String userId, String eventId) {
        Map<String, Boolean> eventsForUser = declined.get(userId);
        return eventsForUser != null && Boolean.TRUE.equals(eventsForUser.get(eventId));
    }
}
