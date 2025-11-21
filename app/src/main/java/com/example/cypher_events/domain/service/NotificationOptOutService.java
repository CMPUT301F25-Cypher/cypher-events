package com.example.cypher_events.domain.service;

import java.util.HashMap;
import java.util.Map;

public class NotificationOptOutService {


    private final Map<String, String> deviceToUser = new HashMap<>();

    private final Map<String, Boolean> notificationsEnabled = new HashMap<>();


    public void registerDeviceUser(String deviceId, String userId) {
        if (deviceId != null && userId != null) {
            deviceToUser.put(deviceId, userId);
        }
    }

    // Disable notifications for the user mapped to this device
    public String optOut(String deviceId) {

        // Check device validity
        if (deviceId == null || deviceId.isEmpty()) {
            return "Invalid device.";
        }

        // Look up user
        String userId = deviceToUser.get(deviceId);
        if (userId == null) {
            return "No user for device.";
        }

        // Get current value (default = true if missing)
        Boolean current = notificationsEnabled.get(userId);
        boolean enabledNow = current == null || current;

        // If already disabled
        if (!enabledNow) {
            return "Already disabled.";
        }

        // Disable notifications
        notificationsEnabled.put(userId, false);
        return "Notifications disabled.";
    }

    // Query current state
    public boolean isNotificationsEnabled(String userId) {
        Boolean value = notificationsEnabled.get(userId);
        return value == null || value;
    }
}
