package com.example.cypher_events.domain.model;

import java.util.HashMap;
import java.util.Map;

public class Notification {

    private String id;
    private String title;
    private String message;
    private long timestamp;
    private String entrantId;

    public Notification() {
        // Required empty constructor for Firebase
    }

    public Notification(String id, String title, String message, long timestamp, String entrantId) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.entrantId = entrantId;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getEntrantId() { return entrantId; }
    public void setEntrantId(String entrantId) { this.entrantId = entrantId; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("title", title);
        map.put("message", message);
        map.put("timestamp", timestamp);
        map.put("entrantId", entrantId);
        return map;
    }
}
