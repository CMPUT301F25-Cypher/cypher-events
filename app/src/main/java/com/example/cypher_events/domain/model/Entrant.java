package com.example.cypher_events.domain.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entrant {

    private String Entrant_id; // usually device ID
    private String Entrant_name;
    private String Entrant_email;
    private String Entrant_phone;
    private boolean Entrant_notificationsEnabled;
    private boolean Entrant_locationPermissionGranted;

    private List<Event> Entrant_joinedEvents;
    private List<Event> Entrant_acceptedEvents;
    private List<Event> Entrant_declinedEvents;

    private double Entrant_latitude;
    private double Entrant_longitude;
    private String Entrant_status; // likely holds device ID or approval state
    private boolean Entrant_isAdmin;


    // Constructors
    public Entrant() {}

    public Entrant(String name, String email, String phone) {
        this.Entrant_name = name;
        this.Entrant_email = email;
        this.Entrant_phone = phone;
    }


    // Getters and setters


    public String getEntrant_id() {return Entrant_id;}

    public String getEntrant_name() { return Entrant_name; }
    public void setEntrant_name(String Entrant_name) { this.Entrant_name = Entrant_name; }

    public String getEntrant_email() { return Entrant_email; }
    public void setEntrant_email(String Entrant_email) { this.Entrant_email = Entrant_email; }

    public String getEntrant_phone() { return Entrant_phone; }
    public void setEntrant_phone(String Entrant_phone) { this.Entrant_phone = Entrant_phone; }

    public boolean isEntrant_notificationsEnabled() { return Entrant_notificationsEnabled; }
    public void setEntrant_notificationsEnabled(boolean Entrant_notificationsEnabled) {
        this.Entrant_notificationsEnabled = Entrant_notificationsEnabled;
    }

    public boolean isEntrant_locationPermissionGranted() { return Entrant_locationPermissionGranted; }
    public void setEntrant_locationPermissionGranted(boolean Entrant_locationPermissionGranted) {
        this.Entrant_locationPermissionGranted = Entrant_locationPermissionGranted;
    }

    public List<Event> getEntrant_joinedEvents() { return Entrant_joinedEvents; }
    public void setEntrant_joinedEvents(List<Event> Entrant_joinedEvents) {
        this.Entrant_joinedEvents = Entrant_joinedEvents;
    }

    public List<Event> getEntrant_acceptedEvents() { return Entrant_acceptedEvents; }
    public void setEntrant_acceptedEvents(List<Event> Entrant_acceptedEvents) {
        this.Entrant_acceptedEvents = Entrant_acceptedEvents;
    }

    public List<Event> getEntrant_declinedEvents() { return Entrant_declinedEvents; }
    public void setEntrant_declinedEvents(List<Event> Entrant_declinedEvents) {
        this.Entrant_declinedEvents = Entrant_declinedEvents;
    }

    public double getEntrant_latitude() { return Entrant_latitude; }
    public void setEntrant_latitude(double Entrant_latitude) { this.Entrant_latitude = Entrant_latitude; }

    public double getEntrant_longitude() { return Entrant_longitude; }
    public void setEntrant_longitude(double Entrant_longitude) { this.Entrant_longitude = Entrant_longitude; }

    public String getEntrant_status() { return Entrant_status; }
    public void setEntrant_status(String Entrant_status) {
        this.Entrant_status = Entrant_status;
        this.Entrant_id = Entrant_status;
    }

    public boolean isEntrant_isAdmin() { return Entrant_isAdmin; }
    public void setEntrant_isAdmin(boolean Entrant_isAdmin) { this.Entrant_isAdmin = Entrant_isAdmin; }


    // Logic for admin check
    public void updateAdminStatus(String adminDeviceId) {
        this.Entrant_isAdmin = (adminDeviceId != null && adminDeviceId.equals(this.Entrant_status));
    }

    private List<String> extractEventIds(List<Event> events) {
        List<String> ids = new java.util.ArrayList<>();
        if (events != null) {
            for (Event e : events) {
                ids.add(e.getEvent_id());
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
        map.put("Entrant_joinedEventIDs", extractEventIds(Entrant_joinedEvents));
        map.put("Entrant_acceptedEventIDs", extractEventIds(Entrant_acceptedEvents));
        map.put("Entrant_declinedEventIDs", extractEventIds(Entrant_declinedEvents));
        return map;
    }

}