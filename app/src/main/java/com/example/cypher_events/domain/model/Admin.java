package com.example.cypher_events.domain.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Admin class
 * Each Admin wraps one Entrant object (representing the base user)
 * and adds Admin-specific fields for system control and moderation.
 */
public class Admin {

    // Core identity — the Entrant representing this Admin
    private Entrant Admin;

    // Admin-specific capabilities
    private boolean Admin_notificationsEnabled;
    private String Admin_status;

    private List<Event>   Admin_deletedEvents   = new ArrayList<>();
    private List<Entrant> Admin_deletedProfiles = new ArrayList<>();
    private List<String>  Admin_deletedImages   = new ArrayList<>();

    private List<Event>   Admin_visitedEvents   = new ArrayList<>();
    private List<Entrant> Admin_visitedProfiles = new ArrayList<>();
    private List<String>  Admin_visitedImages   = new ArrayList<>();

    private List<String>  Admin_reviewedLogs    = new ArrayList<>();

    // Constructors
    public Admin() {}

    public Admin(Entrant adminEntrant) {
        this.Admin = adminEntrant;
    }

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
        this.Admin_deletedEvents =
                admin_deletedEvents != null ? admin_deletedEvents : new ArrayList<>();
    }

    public List<Entrant> getAdmin_deletedProfiles() {
        return Admin_deletedProfiles;
    }

    public void setAdmin_deletedProfiles(List<Entrant> admin_deletedProfiles) {
        this.Admin_deletedProfiles =
                admin_deletedProfiles != null ? admin_deletedProfiles : new ArrayList<>();
    }

    public List<String> getAdmin_deletedImages() {
        return Admin_deletedImages;
    }

    public void setAdmin_deletedImages(List<String> admin_deletedImages) {
        this.Admin_deletedImages =
                admin_deletedImages != null ? admin_deletedImages : new ArrayList<>();
    }

    public List<Event> getAdmin_visitedEvents() {
        return Admin_visitedEvents;
    }

    public void setAdmin_visitedEvents(List<Event> admin_visitedEvents) {
        this.Admin_visitedEvents =
                admin_visitedEvents != null ? admin_visitedEvents : new ArrayList<>();
    }

    public List<Entrant> getAdmin_visitedProfiles() {
        return Admin_visitedProfiles;
    }

    public void setAdmin_visitedProfiles(List<Entrant> admin_visitedProfiles) {
        this.Admin_visitedProfiles =
                admin_visitedProfiles != null ? admin_visitedProfiles : new ArrayList<>();
    }

    public List<String> getAdmin_visitedImages() {
        return Admin_visitedImages;
    }

    public void setAdmin_visitedImages(List<String> admin_visitedImages) {
        this.Admin_visitedImages =
                admin_visitedImages != null ? admin_visitedImages : new ArrayList<>();
    }

    public List<String> getAdmin_reviewedLogs() {
        return Admin_reviewedLogs;
    }

    public void setAdmin_reviewedLogs(List<String> admin_reviewedLogs) {
        this.Admin_reviewedLogs =
                admin_reviewedLogs != null ? admin_reviewedLogs : new ArrayList<>();
    }

    /* ------------------------------------------------------------------
     * Helper methods (optional but handy)
     * ------------------------------------------------------------------ */

    public void addDeletedEvent(Event event) {
        if (event != null && !Admin_deletedEvents.contains(event)) {
            Admin_deletedEvents.add(event);
        }
    }

    public void addDeletedProfile(Entrant entrant) {
        if (entrant != null && !Admin_deletedProfiles.contains(entrant)) {
            Admin_deletedProfiles.add(entrant);
        }
    }

    public void addDeletedImage(String imageId) {
        if (imageId != null && !Admin_deletedImages.contains(imageId)) {
            Admin_deletedImages.add(imageId);
        }
    }

    public void addVisitedEvent(Event event) {
        if (event != null && !Admin_visitedEvents.contains(event)) {
            Admin_visitedEvents.add(event);
        }
    }

    public void addVisitedProfile(Entrant entrant) {
        if (entrant != null && !Admin_visitedProfiles.contains(entrant)) {
            Admin_visitedProfiles.add(entrant);
        }
    }

    public void addVisitedImage(String imageId) {
        if (imageId != null && !Admin_visitedImages.contains(imageId)) {
            Admin_visitedImages.add(imageId);
        }
    }

    public void addReviewedLog(String logEntry) {
        if (logEntry != null && !Admin_reviewedLogs.contains(logEntry)) {
            Admin_reviewedLogs.add(logEntry);
        }
    }

    // helpers for Firebase mapping
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

    private List<String> extractEntrantEmails(List<Entrant> entrants) {
        List<String> emails = new ArrayList<>();
        if (entrants != null) {
            for (Entrant e : entrants) {
                if (e != null && e.getEntrant_email() != null) {
                    emails.add(e.getEntrant_email());
                }
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
        map.put("Admin_deletedEventIDs",        extractEventIds(Admin_deletedEvents));
        map.put("Admin_deletedProfileEmails",   extractEntrantEmails(Admin_deletedProfiles));
        map.put("Admin_deletedImages",          Admin_deletedImages);

        map.put("Admin_visitedEventIDs",        extractEventIds(Admin_visitedEvents));
        map.put("Admin_visitedProfileEmails",   extractEntrantEmails(Admin_visitedProfiles));
        map.put("Admin_visitedImages",          Admin_visitedImages);

        map.put("Admin_reviewedLogs",           Admin_reviewedLogs);
        return map;
    }
}
