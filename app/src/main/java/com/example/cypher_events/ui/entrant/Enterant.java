package com.example.cypher_events.ui.entrant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Enterant {
    private String Enterant_name;
    private String Enterant_email;
    private String Enterant_phone;
    private boolean Enterant_notificationsEnabled;
    private boolean Enterant_locationPermissionGranted;

    private List<String> Enterant_joinedEvents;
    private List<String> Enterant_acceptedEvents;
    private List<String> Enterant_declinedEvents;

    private double Enterant_latitude;
    private double Enterant_longitude;
    private String Enterant_status;

    public String getEnterant_name() {
        return Enterant_name;
    }

    public void setEnterant_name(String enterant_name) {
        Enterant_name = enterant_name;
    }

    public String getEnterant_email() {
        return Enterant_email;
    }

    public void setEnterant_email(String enterant_email) {
        Enterant_email = enterant_email;
    }

    public String getEnterant_phone() {
        return Enterant_phone;
    }

    public void setEnterant_phone(String enterant_phone) {
        Enterant_phone = enterant_phone;
    }

    public boolean isEnterant_notificationsEnabled() {
        return Enterant_notificationsEnabled;
    }

    public void setEnterant_notificationsEnabled(boolean enterant_notificationsEnabled) {
        Enterant_notificationsEnabled = enterant_notificationsEnabled;
    }

    public boolean isEnterant_locationPermissionGranted() {
        return Enterant_locationPermissionGranted;
    }

    public void setEnterant_locationPermissionGranted(boolean enterant_locationPermissionGranted) {
        Enterant_locationPermissionGranted = enterant_locationPermissionGranted;
    }

    public List<String> getEnterant_joinedEvents() {
        return Enterant_joinedEvents;
    }

    public void setEnterant_joinedEvents(List<String> enterant_joinedEvents) {
        Enterant_joinedEvents = enterant_joinedEvents;
    }

    public List<String> getEnterant_acceptedEvents() {
        return Enterant_acceptedEvents;
    }

    public void setEnterant_acceptedEvents(List<String> enterant_acceptedEvents) {
        Enterant_acceptedEvents = enterant_acceptedEvents;
    }

    public List<String> getEnterant_declinedEvents() {
        return Enterant_declinedEvents;
    }

    public void setEnterant_declinedEvents(List<String> enterant_declinedEvents) {
        Enterant_declinedEvents = enterant_declinedEvents;
    }

    public double getEnterant_latitude() {
        return Enterant_latitude;
    }

    public void setEnterant_latitude(double enterant_latitude) {
        Enterant_latitude = enterant_latitude;
    }

    public double getEnterant_longitude() {
        return Enterant_longitude;
    }

    public void setEnterant_longitude(double enterant_longitude) {
        Enterant_longitude = enterant_longitude;
    }

    public String getEnterant_status() {
        return Enterant_status;
    }

    public void setEnterant_status(String enterant_status) {
        Enterant_status = enterant_status;
    }

    public Enterant(){}
    // will add other attributes later
    public Map<String, Object> toMap() {
        Map<String, Object> Enterant_firebase_info = new HashMap<>();
        Enterant_firebase_info.put("Enterant_name", Enterant_name);
        Enterant_firebase_info.put("Enterant_email", Enterant_email);
        Enterant_firebase_info.put("Enterant_phone", Enterant_phone);
        Enterant_firebase_info.put("Enterant_notificationsEnabled", Enterant_notificationsEnabled);
        Enterant_firebase_info.put("Enterant_locationPermissionGranted", Enterant_locationPermissionGranted);
        Enterant_firebase_info.put("Enterant_latitude", Enterant_latitude);
        Enterant_firebase_info.put("Enterant_longitude", Enterant_longitude);
        Enterant_firebase_info.put("Enterant_status", Enterant_status);
        Enterant_firebase_info.put("Enterant_joinedEvents", Enterant_joinedEvents);
        Enterant_firebase_info.put("Enterant_acceptedEvents", Enterant_acceptedEvents);
        Enterant_firebase_info.put("Enterant_declinedEvents", Enterant_declinedEvents);
        return Enterant_firebase_info;
    }

}



