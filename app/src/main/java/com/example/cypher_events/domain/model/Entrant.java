package com.example.cypher_events.domain.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entrant {
    private String Entrant_name;
    private String Entrant_email;
    private String Entrant_phone;
    private boolean Entrant_notificationsEnabled;
    private boolean Entrant_locationPermissionGranted;

    private List<String> Entrant_joinedEvents;
    private List<String> Entrant_acceptedEvents;
    private List<String> Entrant_declinedEvents;

    private double Entrant_latitude;
    private double Entrant_longitude;
    private String Entrant_status;

    public String getEntrant_name() {
        return Entrant_name;
    }

    public void setEntrant_name(String Entrant_name) {
        Entrant_name = Entrant_name;
    }

    public String getEntrant_email() {
        return Entrant_email;
    }

    public void setEntrant_email(String Entrant_email) {
        Entrant_email = Entrant_email;
    }

    public String getEntrant_phone() {
        return Entrant_phone;
    }

    public void setEntrant_phone(String Entrant_phone) {
        Entrant_phone = Entrant_phone;
    }

    public boolean isEntrant_notificationsEnabled() {
        return Entrant_notificationsEnabled;
    }

    public void setEntrant_notificationsEnabled(boolean Entrant_notificationsEnabled) {
        Entrant_notificationsEnabled = Entrant_notificationsEnabled;
    }

    public boolean isEntrant_locationPermissionGranted() {
        return Entrant_locationPermissionGranted;
    }

    public void setEntrant_locationPermissionGranted(boolean Entrant_locationPermissionGranted) {
        Entrant_locationPermissionGranted = Entrant_locationPermissionGranted;
    }

    public List<String> getEntrant_joinedEvents() {
        return Entrant_joinedEvents;
    }

    public void setEntrant_joinedEvents(List<String> Entrant_joinedEvents) {
        Entrant_joinedEvents = Entrant_joinedEvents;
    }

    public List<String> getEntrant_acceptedEvents() {
        return Entrant_acceptedEvents;
    }

    public void setEntrant_acceptedEvents(List<String> Entrant_acceptedEvents) {
        Entrant_acceptedEvents = Entrant_acceptedEvents;
    }

    public List<String> getEntrant_declinedEvents() {
        return Entrant_declinedEvents;
    }

    public void setEntrant_declinedEvents(List<String> Entrant_declinedEvents) {
        Entrant_declinedEvents = Entrant_declinedEvents;
    }

    public double getEntrant_latitude() {
        return Entrant_latitude;
    }

    public void setEntrant_latitude(double Entrant_latitude) {
        Entrant_latitude = Entrant_latitude;
    }

    public double getEntrant_longitude() {
        return Entrant_longitude;
    }

    public void setEntrant_longitude(double Entrant_longitude) {
        Entrant_longitude = Entrant_longitude;
    }

    public String getEntrant_status() {
        return Entrant_status;
    }

    public void setEntrant_status(String Entrant_status) {
        Entrant_status = Entrant_status;
    }

    public Entrant(){}
    // will add other attributes later
    public Map<String, Object> toMap() {
        Map<String, Object> Entrant_firebase_info = new HashMap<>();
        Entrant_firebase_info.put("Entrant_name", Entrant_name);
        Entrant_firebase_info.put("Entrant_email", Entrant_email);
        Entrant_firebase_info.put("Entrant_phone", Entrant_phone);
        Entrant_firebase_info.put("Entrant_notificationsEnabled", Entrant_notificationsEnabled);
        Entrant_firebase_info.put("Entrant_locationPermissionGranted", Entrant_locationPermissionGranted);
        Entrant_firebase_info.put("Entrant_latitude", Entrant_latitude);
        Entrant_firebase_info.put("Entrant_longitude", Entrant_longitude);
        Entrant_firebase_info.put("Entrant_status", Entrant_status);
        Entrant_firebase_info.put("Entrant_joinedEvents", Entrant_joinedEvents);
        Entrant_firebase_info.put("Entrant_acceptedEvents", Entrant_acceptedEvents);
        Entrant_firebase_info.put("Entrant_declinedEvents", Entrant_declinedEvents);
        return Entrant_firebase_info;
    }
}
