package com.example.cypher_events.domain.model;

/**
 * Entrant
 * 
 * Purpose: Domain model representing an event entrant/participant in the system.
 * 
 * Key Attributes:
 * - Identity: ID (device ID), name, email, phone
 * - Preferences: notification opt-in/opt-out, location permission
 * - Event participation: joined, accepted, and declined event lists
 * - Location: latitude/longitude coordinates
 * - Admin status: flag for admin privileges
 * 
 * Design Pattern: Data Transfer Object (DTO) with Firestore mapping
 * 
 * Outstanding Issues: None
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entrant {

    // Basic identity
    private String Entrant_id; // usually device ID
    private String Entrant_name;
    private String Entrant_email;
    private String Entrant_phone;

    // Preferences / permissions
    private boolean Entrant_notificationsEnabled;
    private boolean Entrant_locationPermissionGranted;

    // Event participation
    private List<Event> Entrant_joinedEvents   = new ArrayList<>();
    private List<Event> Entrant_acceptedEvents = new ArrayList<>();
    private List<Event> Entrant_declinedEvents = new ArrayList<>();

    // Location & status
    private double Entrant_latitude;
    private double Entrant_longitude;
    private String Entrant_status; // used for device ID or approval state

    // Admin flag
    private boolean Entrant_isAdmin;

    // Constructors
    public Entrant() {}

    public Entrant(String name, String email, String phone) {
        this.Entrant_name = name;
        this.Entrant_email = email;
        this.Entrant_phone = phone;
    }

    public Entrant(String id, String name, String email, String phone) {
        this(name, email, phone);
        this.Entrant_id = id;
    }

    // Getters and setters
    public String getEntrant_id() { return Entrant_id; }

    public void setEntrant_id(String entrant_id) {
        this.Entrant_id = entrant_id;
    }

    public String getEntrant_name() { return Entrant_name; }
    public void setEntrant_name(String entrant_name) { this.Entrant_name = entrant_name; }

    public String getEntrant_email() { return Entrant_email; }
    public void setEntrant_email(String entrant_email) { this.Entrant_email = entrant_email; }

    public String getEntrant_phone() { return Entrant_phone; }
    public void setEntrant_phone(String entrant_phone) { this.Entrant_phone = entrant_phone; }

    public boolean isEntrant_notificationsEnabled() { return Entrant_notificationsEnabled; }
    public void setEntrant_notificationsEnabled(boolean entrant_notificationsEnabled) {
        this.Entrant_notificationsEnabled = entrant_notificationsEnabled;
    }

    public boolean isEntrant_locationPermissionGranted() { return Entrant_locationPermissionGranted; }
    public void setEntrant_locationPermissionGranted(boolean entrant_locationPermissionGranted) {
        this.Entrant_locationPermissionGranted = entrant_locationPermissionGranted;
    }

    public List<Event> getEntrant_joinedEvents() { return Entrant_joinedEvents; }
    public void setEntrant_joinedEvents(List<Event> entrant_joinedEvents) {
        this.Entrant_joinedEvents = entrant_joinedEvents != null ? entrant_joinedEvents : new ArrayList<>();
    }

    public List<Event> getEntrant_acceptedEvents() { return Entrant_acceptedEvents; }
    public void setEntrant_acceptedEvents(List<Event> entrant_acceptedEvents) {
        this.Entrant_acceptedEvents = entrant_acceptedEvents != null ? entrant_acceptedEvents : new ArrayList<>();
    }

    public List<Event> getEntrant_declinedEvents() { return Entrant_declinedEvents; }
    public void setEntrant_declinedEvents(List<Event> entrant_declinedEvents) {
        this.Entrant_declinedEvents = entrant_declinedEvents != null ? entrant_declinedEvents : new ArrayList<>();
    }

    public double getEntrant_latitude() { return Entrant_latitude; }
    public void setEntrant_latitude(double entrant_latitude) { this.Entrant_latitude = entrant_latitude; }

    public double getEntrant_longitude() { return Entrant_longitude; }
    public void setEntrant_longitude(double entrant_longitude) { this.Entrant_longitude = entrant_longitude; }

    public String getEntrant_status() { return Entrant_status; }
    public void setEntrant_status(String entrant_status) {
        this.Entrant_status = entrant_status;
        // keep old behaviour: status doubles as id if needed
        this.Entrant_id = entrant_status;
    }

    public boolean isEntrant_isAdmin() { return Entrant_isAdmin; }
    public void setEntrant_isAdmin(boolean entrant_isAdmin) { this.Entrant_isAdmin = entrant_isAdmin; }

    // Logic for admin check
    public void updateAdminStatus(String adminDeviceId) {
        this.Entrant_isAdmin = (adminDeviceId != null && adminDeviceId.equals(this.Entrant_status));
    }

    /* ------------------------------------------------------------------
     * Helper methods for working with event lists
     * ------------------------------------------------------------------ */

    public void addJoinedEvent(Event event) {
        if (event != null && !Entrant_joinedEvents.contains(event)) {
            Entrant_joinedEvents.add(event);
        }
    }

    public void addAcceptedEvent(Event event) {
        if (event != null && !Entrant_acceptedEvents.contains(event)) {
            Entrant_acceptedEvents.add(event);
        }
    }

    public void addDeclinedEvent(Event event) {
        if (event != null && !Entrant_declinedEvents.contains(event)) {
            Entrant_declinedEvents.add(event);
        }
    }

    private List<String> extractEventIds(List<Event> events) {
        List<String> ids = new ArrayList<>();
        if (events != null) {
            for (Event e : events) {
                if (e != null && e.getEvent_id() != null) {
                    ids.add(e.getEvent_id());
                }
            }
        }
        return ids;
    }

    // Firebase mapping
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("Entrant_id", Entrant_id);
        map.put("Entrant_name", Entrant_name);
        map.put("Entrant_email", Entrant_email);
        map.put("Entrant_phone", Entrant_phone);
        map.put("Entrant_notificationsEnabled", Entrant_notificationsEnabled);
        map.put("Entrant_locationPermissionGranted", Entrant_locationPermissionGranted);
        map.put("Entrant_latitude", Entrant_latitude);
        map.put("Entrant_longitude", Entrant_longitude);
        map.put("Entrant_status", Entrant_status);
        map.put("Entrant_isAdmin", Entrant_isAdmin);

        // Only store event IDs to break circular dependency
        map.put("Entrant_joinedEventIDs",   extractEventIds(Entrant_joinedEvents));
        map.put("Entrant_acceptedEventIDs", extractEventIds(Entrant_acceptedEvents));
        map.put("Entrant_declinedEventIDs", extractEventIds(Entrant_declinedEvents));

        return map;
    }
}
