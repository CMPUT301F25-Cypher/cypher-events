package com.example.cypher_events.domain.model;

import java.util.HashMap;
import java.util.Map;

public class NotificationLog {

    private String notificationId;
    private String recipientEmail;
    private String message;
    private String eventId;
    private long timestampUtc;

    public NotificationLog() {}
    /**
     * @param notificationId notification identifier
     * @param recipientEmail email of recipient
     * @param message notification message text
     * @param eventId associated event identifier
     * @param timestampUtc timestamp in UTC milliseconds
     */

    public NotificationLog(String notificationId,
                           String recipientEmail,
                           String message,
                           String eventId,
                           long timestampUtc) {
        this.notificationId = notificationId;
        this.recipientEmail = recipientEmail;
        this.message = message;
        this.eventId = eventId;
        this.timestampUtc = timestampUtc;
    }

    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }

    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public long getTimestampUtc() { return timestampUtc; }
    public void setTimestampUtc(long timestampUtc) { this.timestampUtc = timestampUtc; }

    public Map<String, Object> toMap() {
        Map<String,Object> m = new HashMap<>();
        m.put("notificationId", notificationId);
        m.put("recipientEmail", recipientEmail);
        m.put("message", message);
        m.put("eventId", eventId);
        m.put("timestampUtc", timestampUtc);
        return m;
    }
    /**
     * @param m map containing notification data
     * @return NotificationLog instance from map, or null
     */

    @SuppressWarnings("unchecked")
    public static NotificationLog fromMap(Map<String, Object> m) {
        if (m == null) return null;
        NotificationLog n = new NotificationLog();
        Object id = m.get("notificationId");
        Object recip = m.get("recipientEmail");
        Object msg = m.get("message");
        Object eid = m.get("eventId");
        Object ts = m.get("timestampUtc");

        n.notificationId = id != null ? id.toString() : null;
        n.recipientEmail = recip != null ? recip.toString() : null;
        n.message = msg != null ? msg.toString() : null;
        n.eventId = eid != null ? eid.toString() : null;
        n.timestampUtc = ts instanceof Number ? ((Number) ts).longValue() : 0L;
        return n;
    }
}
