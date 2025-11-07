package com.example.cypher_events.domain.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Admin class
 * Each Admin wraps one Entrant object (representing the base user)
 * and adds Admin-specific fields for system control and moderation.
 */
public class Admin {

    // ────────────────────────────────────────────────
    // Core identity — the Entrant representing this Admin
    private Entrant Admin;

    // ────────────────────────────────────────────────
    // Admin-specific capabilities

    private boolean Admin_notificationsEnabled;
    private String Admin_status;

    private List<Event> Admin_deletedEvents;
    private List<Entrant> Admin_deletedProfiles;
    private List<String> Admin_deletedImages;

    private List<Event> Admin_visitedEvents;
    private List<Entrant> Admin_visitedProfiles;
    private List<String> Admin_visitedImages;

    private List<String> Admin_reviewedLogs;

    // ────────────────────────────────────────────────
    // Constructors

    public Admin() {}

    public Admin(Entrant adminEntrant) {
        this.Admin = adminEntrant;
    }

    // ────────────────────────────────────────────────
    // Entrant accessors (delegated)

    public Entrant getAdmin() {
        return Admin;
    }

    public void setAdmin(Entrant adminEntrant) {
        this.Admin = adminEntrant;
    }

    // Convenience shortcuts to Entrant data
    public String getName() {
        return Admin != null ? Admin.getEntrant_name() : null;
    }

    public String getEmail() {
        return Admin != null ? Admin.getEntrant_email() : null;
    }

    public String getPhone() {
        return Admin != null ? Admin.getEntrant_phone() : null;
    }

    public boolean isAdminUser() {
        return Admin != null && Admin.isEntrant_isAdmin();
    }

    // ────────────────────────────────────────────────
    // Admin-specific getters/setters

    public boolean isAdmin_notificationsEnabled() {
        return Admin_notificationsEnabled;
    }

    public void setAdmin_notificationsEnabled(boolean admin_notificationsEnabled) {
        this.Admin_notificationsEnabled = admin_notificationsEnabled;
    }

    public String getAdmin_status() {
        return Admin_status;
    }

    public void setAdmin_status(String admin_status) {
        this.Admin_status = admin_status;
    }

    public List<Event> getAdmin_deletedEvents() {
        return Admin_deletedEvents;
    }

    public void setAdmin_deletedEvents(List<Event> admin_deletedEvents) {
        this.Admin_deletedEvents = admin_deletedEvents;
    }

    public List<Entrant> getAdmin_deletedProfiles() {
        return Admin_deletedProfiles;
    }

    public void setAdmin_deletedProfiles(List<Entrant> admin_deletedProfiles) {
        this.Admin_deletedProfiles = admin_deletedProfiles;
    }

    public List<String> getAdmin_deletedImages() {
        return Admin_deletedImages;
    }

    public void setAdmin_deletedImages(List<String> admin_deletedImages) {
        this.Admin_deletedImages = admin_deletedImages;
    }

    public List<Event> getAdmin_visitedEvents() {
        return Admin_visitedEvents;
    }

    public void setAdmin_visitedEvents(List<Event> admin_visitedEvents) {
        this.Admin_visitedEvents = admin_visitedEvents;
    }

    public List<Entrant> getAdmin_visitedProfiles() {
        return Admin_visitedProfiles;
    }

    public void setAdmin_visitedProfiles(List<Entrant> admin_visitedProfiles) {
        this.Admin_visitedProfiles = admin_visitedProfiles;
    }

    public List<String> getAdmin_visitedImages() {
        return Admin_visitedImages;
    }

    public void setAdmin_visitedImages(List<String> admin_visitedImages) {
        this.Admin_visitedImages = admin_visitedImages;
    }

    public List<String> getAdmin_reviewedLogs() {
        return Admin_reviewedLogs;
    }

    public void setAdmin_reviewedLogs(List<String> admin_reviewedLogs) {
        this.Admin_reviewedLogs = admin_reviewedLogs;
    }

    // helper

    private List<String> extractEventIds(List<Event> events) {
        List<String> ids = new java.util.ArrayList<>();
        if (events != null) {
            for (Event e : events) {
                ids.add(e.getEvent_id());
            }
        }
        return ids;
    }

    private List<String> extractEntrantEmails(List<Entrant> entrants) {
        List<String> emails = new java.util.ArrayList<>();
        if (entrants != null) {
            for (Entrant e : entrants) {
                emails.add(e.getEntrant_email());
            }
        }
        return emails;
    }


    // Firebase Mapping
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("Admin_email", getEmail());
        map.put("Admin_notificationsEnabled", Admin_notificationsEnabled);
        map.put("Admin_status", Admin_status);

        // Reference lists by IDs or emails only
        map.put("Admin_deletedEventIDs", extractEventIds(Admin_deletedEvents));
        map.put("Admin_deletedProfileEmails", extractEntrantEmails(Admin_deletedProfiles));
        map.put("Admin_deletedImages", Admin_deletedImages);

        map.put("Admin_visitedEventIDs", extractEventIds(Admin_visitedEvents));
        map.put("Admin_visitedProfileEmails", extractEntrantEmails(Admin_visitedProfiles));
        map.put("Admin_visitedImages", Admin_visitedImages);
        map.put("Admin_reviewedLogs", Admin_reviewedLogs);
        return map;
    }
}