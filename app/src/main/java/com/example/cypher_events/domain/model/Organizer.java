package com.example.cypher_events.domain.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Organizer class
 * Each Organizer wraps one Entrant object (its own user identity)
 * and adds Organizer-specific fields like event management & notifications.
 */
public class Organizer {

    // The Entrant profile representing this Organizer
    private Entrant Organizer;
    private String Organizer_id; // same as device ID

    // Organizer-specific event and notification data
    private List<Event> Organizer_createdEvents;
    private List<Event> Organizer_activeEvents;
    private List<Event> Organizer_completedEvents;

    private boolean Organizer_notificationsEnabled;
    private List<String> Organizer_sentNotifications;
    private String Organizer_removalReason;


    // Constructors

    public Organizer() {}

    public Organizer(Entrant entrant) {
        this.Organizer = entrant;
        this.Organizer_id = entrant.getEntrant_id();
    }


    // Delegation: Accessor methods to Entrant fields
    // So organizer.getName() → organizer.getOrganizer().getEntrant_name()


    public String getOrganizer_id() {
        return Organizer_id;
    }

    public Entrant getOrganizer() {
        return Organizer;
    }

    public void setOrganizer(Entrant organizer) {
        this.Organizer = organizer;
    }

    // For convenience: shortcuts to Entrant fields
    public String getName() {
        return Organizer != null ? Organizer.getEntrant_name() : null;
    }

    public String getEmail() {
        return Organizer != null ? Organizer.getEntrant_email() : null;
    }

    public String getPhone() {
        return Organizer != null ? Organizer.getEntrant_phone() : null;
    }

    public double getLatitude() {
        return Organizer != null ? Organizer.getEntrant_latitude() : 0.0;
    }

    public double getLongitude() {
        return Organizer != null ? Organizer.getEntrant_longitude() : 0.0;
    }

    public boolean isAdmin() {
        return Organizer != null && Organizer.isEntrant_isAdmin();
    }


    // Organizer-specific fields

    public List<Event> getOrganizer_createdEvents() {
        return Organizer_createdEvents;
    }

    public void setOrganizer_createdEvents(List<Event> organizer_createdEvents) {
        this.Organizer_createdEvents = organizer_createdEvents;
    }

    public List<Event> getOrganizer_activeEvents() {
        return Organizer_activeEvents;
    }

    public void setOrganizer_activeEvents(List<Event> organizer_activeEvents) {
        this.Organizer_activeEvents = organizer_activeEvents;
    }

    public List<Event> getOrganizer_completedEvents() {
        return Organizer_completedEvents;
    }

    public void setOrganizer_completedEvents(List<Event> organizer_completedEvents) {
        this.Organizer_completedEvents = organizer_completedEvents;
    }

    public boolean isOrganizer_notificationsEnabled() {
        return Organizer_notificationsEnabled;
    }

    public void setOrganizer_notificationsEnabled(boolean organizer_notificationsEnabled) {
        this.Organizer_notificationsEnabled = organizer_notificationsEnabled;
    }

    public List<String> getOrganizer_sentNotifications() {
        return Organizer_sentNotifications;
    }

    public void setOrganizer_sentNotifications(List<String> organizer_sentNotifications) {
        this.Organizer_sentNotifications = organizer_sentNotifications;
    }

    public String getOrganizer_removalReason() {
        return Organizer_removalReason;
    }

    public void setOrganizer_removalReason(String organizer_removalReason) {
        this.Organizer_removalReason = organizer_removalReason;
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
        map.put("Organizer_id", Organizer_id);
        map.put("Organizer_email", getEmail());
        map.put("Organizer_notificationsEnabled", Organizer_notificationsEnabled);
        map.put("Organizer_removalReason", Organizer_removalReason);

        // Store event IDs instead of full event objects
        map.put("Organizer_createdEventIDs", extractEventIds(Organizer_createdEvents));
        map.put("Organizer_activeEventIDs", extractEventIds(Organizer_activeEvents));
        map.put("Organizer_completedEventIDs", extractEventIds(Organizer_completedEvents));

        map.put("Organizer_sentNotifications", Organizer_sentNotifications);
        return map;
    }
}