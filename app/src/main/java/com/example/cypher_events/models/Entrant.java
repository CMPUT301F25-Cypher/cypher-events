package com.example.cypher_events.models;

import com.google.firebase.firestore.GeoPoint;
import java.util.Date;

/**
 * Entrant model representing a user's participation in an event
 * Tracks location data and lottery status
 *
 * Outstanding issues: None
 */
public class Entrant {
    private String entrantId;
    private String userId;
    private String eventId;
    private String status; // "waiting", "selected", "enrolled", "cancelled", "declined"
    private Date joinedDate;

    // US 02.02.02 - Geolocation data
    private GeoPoint joinLocation;
    private String joinLocationAddress;
    private double latitude;
    private double longitude;

    // Notification preferences
    private boolean notificationsEnabled;

    /**
     * Default constructor required for Firestore
     */
    public Entrant() {
        this.notificationsEnabled = true;
    }

    /**
     * Constructor with essential fields
     */
    public Entrant(String userId, String eventId) {
        this();
        this.userId = userId;
        this.eventId = eventId;
        this.status = "waiting";
        this.joinedDate = new Date();
    }

    // Getters and Setters
    public String getEntrantId() { return entrantId; }
    public void setEntrantId(String id) { this.entrantId = id; }

    public String getUserId() { return userId; }
    public void setUserId(String id) { this.userId = id; }

    public String getEventId() { return eventId; }
    public void setEventId(String id) { this.eventId = id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getJoinedDate() { return joinedDate; }
    public void setJoinedDate(Date date) { this.joinedDate = date; }

    public GeoPoint getJoinLocation() { return joinLocation; }
    public void setJoinLocation(GeoPoint location) { this.joinLocation = location; }

    public String getJoinLocationAddress() { return joinLocationAddress; }
    public void setJoinLocationAddress(String address) { this.joinLocationAddress = address; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public boolean isNotificationsEnabled() { return notificationsEnabled; }
    public void setNotificationsEnabled(boolean enabled) { this.notificationsEnabled = enabled; }
}