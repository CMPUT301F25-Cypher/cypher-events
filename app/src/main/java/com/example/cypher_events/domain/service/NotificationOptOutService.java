package com.example.cypher_events.domain.service;

import java.util.HashMap;
import java.util.Map;

/**
 * US 01.04.03: "As an entrant, I want to opt out of notifications".
 */
public class NotificationOptOutService {

    // demo mapping: deviceId -> userId
    private final Map<String, String> deviceToUser = new HashMap<>();

    // userId -> notificationsEnabled (true/false)
    private final Map<String, Boolean> notificationsEnabled = new HashMap<>();

    /** bind a device to a user */
    public void registerDeviceUser(String deviceId, String userId) {
        if (deviceId != null && userId != null) {
            deviceToUser.put(deviceId, userId);
        }
    }

    /**
     * Opt out: sets Entrant_notificationsEnabled = false for the device's user.
     * @return short status message for UI.
     */
    public String optOut(String deviceId) {
        if (deviceId == null || deviceId.isEmpty()) {
            return "Invalid device.";
        }
        String userId = deviceToUser.get(deviceId);
        if (userId == null) {
            return "No user for device.";
        }

        Boolean current = notificationsEnabled.get(userId);
        // Default behavior: if unset, treat as enabled (true)
        boolean enabledNow = (current == null) ? true : current.booleanValue();

        if (!enabledNow) {
            return "Already disabled.";
        }

        notificationsEnabled.put(userId, false);
        return "Notifications disabled.";
    }
    /** For tests check current flag */
    public boolean isNotificationsEnabled(String userId) {
        Boolean v = notificationsEnabled.get(userId);
        return (v == null) ? true : v.booleanValue();
    }
}
