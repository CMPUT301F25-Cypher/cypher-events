package com.example.cypher_events.domain.model;

public class NotificationItem {
    private String id;
    private String title;
    private String message;
    private long timestamp;
    private boolean read;

    public NotificationItem() {} // Needed for Firebase

    public NotificationItem(String title, String message) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.title = title;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
        this.read = false;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
    public boolean isRead() { return read; }

    public void setRead(boolean read) { this.read = read; }
}
