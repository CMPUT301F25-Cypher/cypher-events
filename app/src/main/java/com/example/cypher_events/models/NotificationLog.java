package com.example.cypher_events.models;

import java.util.Date;

/**
 * NotificationLog model for tracking notifications sent by organizers
 * Used for US 03.08.01 - Admin reviewing notification logs
 *
 * Outstanding issues: None
 */
public class NotificationLog {
    private String logId;
    private String eventId;
    private String organizerId;
    private String notificationType; // "selected", "not_selected", "cancelled", "general"
    private String message;
    private Date sentDate;
    private int recipientCount;
    private String recipientType; // "all_waiting", "all_selected", "all_cancelled"

    /**
     * Default constructor required for Firestore
     */
    public NotificationLog() {
        this.sentDate = new Date();
    }

    /**
     * Constructor with essential fields
     */
    public NotificationLog(String eventId, String organizerId, String notificationType, String message) {
        this();
        this.eventId = eventId;
        this.organizerId = organizerId;
        this.notificationType = notificationType;
        this.message = message;
    }

    // Getters and Setters
    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getOrganizerId() { return organizerId; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }

    public String getNotificationType() { return notificationType; }
    public void setNotificationType(String type) { this.notificationType = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Date getSentDate() { return sentDate; }
    public void setSentDate(Date date) { this.sentDate = date; }

    public int getRecipientCount() { return recipientCount; }
    public void setRecipientCount(int count) { this.recipientCount = count; }

    public String getRecipientType() { return recipientType; }
    public void setRecipientType(String type) { this.recipientType = type; }
}