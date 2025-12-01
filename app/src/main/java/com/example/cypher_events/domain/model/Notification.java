package com.example.cypher_events.domain.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Notification model stored in Firestore in collection "notifications"
 */
public class Notification {
    private String notificationId;
    private String recipientEntrantId;  // Entrant.Entrant_id (may be null if not known)
    private String recipientEmail;      // Entrant_email (used as primary key for display)
    private String senderOrganizerId;   // Organizer.Organizer_id (nullable)
    private String eventId;             // event id (nullable)
    private String title;
    private String message;
    private long timestampUtc;
    private boolean read;

    public Notification() {} // Firestore requires no-arg constructor

    public Notification(String notificationId,
                        String recipientEntrantId,
                        String recipientEmail,
                        String senderOrganizerId,
                        String eventId,
                        String title,
                        String message,
                        long timestampUtc,
                        boolean read) {
        this.notificationId = notificationId;
        this.recipientEntrantId = recipientEntrantId;
        this.recipientEmail = recipientEmail;
        this.senderOrganizerId = senderOrganizerId;
        this.eventId = eventId;
        this.title = title;
        this.message = message;
        this.timestampUtc = timestampUtc;
        this.read = read;
    }

    // getters / setters
    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }

    public String getRecipientEntrantId() { return recipientEntrantId; }
    public void setRecipientEntrantId(String recipientEntrantId) { this.recipientEntrantId = recipientEntrantId; }

    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }

    public String getSenderOrganizerId() { return senderOrganizerId; }
    public void setSenderOrganizerId(String senderOrganizerId) { this.senderOrganizerId = senderOrganizerId; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public long getTimestampUtc() { return timestampUtc; }
    public void setTimestampUtc(long timestampUtc) { this.timestampUtc = timestampUtc; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public Map<String, Object> toMap() {
        Map<String,Object> m = new HashMap<>();
        m.put("notificationId", notificationId);
        m.put("recipientEntrantId", recipientEntrantId);
        m.put("recipientEmail", recipientEmail);
        m.put("senderOrganizerId", senderOrganizerId);
        m.put("eventId", eventId);
        m.put("title", title);
        m.put("message", message);
        m.put("timestampUtc", timestampUtc);
        m.put("read", read);
        return m;
    }
}
