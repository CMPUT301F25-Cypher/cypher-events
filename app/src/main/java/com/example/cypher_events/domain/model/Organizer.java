package com.example.cypher_events.domain.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Organizer {

    private String Organizer_name;
    private String Organizer_email;
    private String Organizer_phone;

    private String Organizer_status;
    private List<String> Organizer_createdEvents;
    private List<String> Organizer_activeEvents;
    private List<String> Organizer_completedEvents;

    private boolean Organizer_notificationsEnabled;
    private List<String> Organizer_sentNotifications;
    private String Organizer_removalReason;

    public Organizer() {}

    public String getOrganizer_name() {
        return Organizer_name;
    }

    public void setOrganizer_name(String organizer_name) {
        Organizer_name = organizer_name;
    }

    public String getOrganizer_email() {
        return Organizer_email;
    }

    public void setOrganizer_email(String organizer_email) {
        Organizer_email = organizer_email;
    }

    public String getOrganizer_phone() {
        return Organizer_phone;
    }

    public void setOrganizer_phone(String organizer_phone) {
        Organizer_phone = organizer_phone;
    }

    public String getOrganizer_status() {
        return Organizer_status;
    }

    public void setOrganizer_status(String organizer_status) {
        Organizer_status = organizer_status;
    }

    public List<String> getOrganizer_createdEvents() {
        return Organizer_createdEvents;
    }

    public void setOrganizer_createdEvents(List<String> organizer_createdEvents) {
        Organizer_createdEvents = organizer_createdEvents;
    }

    public List<String> getOrganizer_activeEvents() {
        return Organizer_activeEvents;
    }

    public void setOrganizer_activeEvents(List<String> organizer_activeEvents) {
        Organizer_activeEvents = organizer_activeEvents;
    }

    public List<String> getOrganizer_completedEvents() {
        return Organizer_completedEvents;
    }

    public void setOrganizer_completedEvents(List<String> organizer_completedEvents) {
        Organizer_completedEvents = organizer_completedEvents;
    }

    public boolean isOrganizer_notificationsEnabled() {
        return Organizer_notificationsEnabled;
    }

    public void setOrganizer_notificationsEnabled(boolean organizer_notificationsEnabled) {
        Organizer_notificationsEnabled = organizer_notificationsEnabled;
    }

    public List<String> getOrganizer_sentNotifications() {
        return Organizer_sentNotifications;
    }

    public void setOrganizer_sentNotifications(List<String> organizer_sentNotifications) {
        Organizer_sentNotifications = organizer_sentNotifications;
    }

    public String getOrganizer_removalReason() {
        return Organizer_removalReason;
    }

    public void setOrganizer_removalReason(String organizer_removalReason) {
        Organizer_removalReason = organizer_removalReason;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> Organizer_firebase_info = new HashMap<>();
        Organizer_firebase_info.put("Organizer_name", Organizer_name);
        Organizer_firebase_info.put("Organizer_email", Organizer_email);
        Organizer_firebase_info.put("Organizer_phone", Organizer_phone);
        Organizer_firebase_info.put("Organizer_status", Organizer_status);
        Organizer_firebase_info.put("Organizer_createdEvents", Organizer_createdEvents);
        Organizer_firebase_info.put("Organizer_activeEvents", Organizer_activeEvents);
        Organizer_firebase_info.put("Organizer_completedEvents", Organizer_completedEvents);
        Organizer_firebase_info.put("Organizer_notificationsEnabled", Organizer_notificationsEnabled);
        Organizer_firebase_info.put("Organizer_sentNotifications", Organizer_sentNotifications);
        Organizer_firebase_info.put("Organizer_removalReason", Organizer_removalReason);
        return Organizer_firebase_info;
    }
}