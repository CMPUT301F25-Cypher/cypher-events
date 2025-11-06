package com.example.cypher_events.utils;

/**
 * Constants used throughout the application
 * Centralized location for all constant values
 */
public class Constants {

    // Intent Extra Keys
    public static final String EXTRA_EVENT_ID = "eventId";
    public static final String EXTRA_EVENT_NAME = "eventName";
    public static final String EXTRA_USER_ID = "userId";

    // Entrant Status Values
    public static final String STATUS_WAITING = "waiting";
    public static final String STATUS_SELECTED = "selected";
    public static final String STATUS_ENROLLED = "enrolled";
    public static final String STATUS_CANCELLED = "cancelled";
    public static final String STATUS_DECLINED = "declined";

    // Notification Types
    public static final String NOTIFICATION_TYPE_SELECTED = "selected";
    public static final String NOTIFICATION_TYPE_NOT_SELECTED = "not_selected";
    public static final String NOTIFICATION_TYPE_CANCELLED = "cancelled";
    public static final String NOTIFICATION_TYPE_GENERAL = "general";

    // Firestore Collection Names
    public static final String COLLECTION_EVENTS = "events";
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_ENTRANTS = "entrants";
    public static final String COLLECTION_ADMINS = "admins";
    public static final String COLLECTION_NOTIFICATION_LOGS = "notificationLogs";

    // Shared Preferences Keys
    public static final String PREFS_NAME = "CypherEventsPrefs";
    public static final String PREF_DEVICE_ID = "deviceId";
    public static final String PREF_USER_NAME = "userName";

    // Request Codes
    public static final int REQUEST_LOCATION_PERMISSION = 1001;
    public static final int REQUEST_IMAGE_PICK = 1002;
    public static final int REQUEST_CAMERA = 1003;
}