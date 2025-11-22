package com.example.cypher_events.domain.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Organizer {

    // The Entrant profile representing this Organizer
    private Entrant Organizer;
    private String Organizer_id; // usually same as device ID / entrant id

    // Organizer-specific event and notification data
    private List<Event> Organizer_createdEvents   = new ArrayList<>();
    private List<Event> Organizer_activeEvents    = new ArrayList<>();
    private List<Event> Organizer_completedEvents = new ArrayList<>();

    private boolean Organizer_notificationsEnabled;
    private List<NotificationLog> Organizer_sentNotifications = new ArrayList<>();
    private String Organizer_removalReason;

    // Constructors
    public Organizer() {}

    public Organizer(Entrant entrant) {
        this.Organizer = entrant;
        if (entrant != null) {
            this.Organizer_id = entrant.getEntrant_id();
        }
    }

    public Organizer(String organizerId, Entrant entrant) {
        this.Organizer = entrant;
        this.Organizer_id = organizerId;
    }

    // Basic accessors
    public String getOrganizer_id() {
        return Organizer_id;
    }

    public void setOrganizer_id(String organizer_id) {
        this.Organizer_id = organizer_id;
    }

    public Entrant getOrganizer() {
        return Organizer;
    }

    public void setOrganizer(Entrant organizer) {
        this.Organizer = organizer;
        // keep ids aligned when possible
        if (organizer != null && organizer.getEntrant_id() != null) {
            this.Organizer_id = organizer.getEntrant_id();
        }
    }

    // Convenience: shortcuts to Entrant fields
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
        this.Organizer_createdEvents =
                organizer_createdEvents != null ? organizer_createdEvents : new ArrayList<>();
    }

    public List<Event> getOrganizer_activeEvents() {
        return Organizer_activeEvents;
    }

    public void setOrganizer_activeEvents(List<Event> organizer_activeEvents) {
        this.Organizer_activeEvents =
                organizer_activeEvents != null ? organizer_activeEvents : new ArrayList<>();
    }

    public List<Event> getOrganizer_completedEvents() {
        return Organizer_completedEvents;
    }

    public void setOrganizer_completedEvents(List<Event> organizer_completedEvents) {
        this.Organizer_completedEvents =
                organizer_completedEvents != null ? organizer_completedEvents : new ArrayList<>();
    }

    public boolean isOrganizer_notificationsEnabled() {
        return Organizer_notificationsEnabled;
    }

    public void setOrganizer_notificationsEnabled(boolean organizer_notificationsEnabled) {
        this.Organizer_notificationsEnabled = organizer_notificationsEnabled;
    }

    public List<NotificationLog> getOrganizer_sentNotifications() {
        return Organizer_sentNotifications;
    }
    public void setOrganizer_sentNotifications(List<NotificationLog> organizer_sentNotifications) {
        this.Organizer_sentNotifications =
                organizer_sentNotifications != null ? organizer_sentNotifications : new ArrayList<>();
    }

    public String getOrganizer_removalReason() {
        return Organizer_removalReason;
    }

    public void setOrganizer_removalReason(String organizer_removalReason) {
        this.Organizer_removalReason = organizer_removalReason;
    }
    public void addSentNotification(NotificationLog log) {
        if (log == null) return;
        if (Organizer_sentNotifications == null) Organizer_sentNotifications = new ArrayList<>();
        Organizer_sentNotifications.add(log);
    }
    public void addCreatedEvent(Event event) {
        if (event != null && !Organizer_createdEvents.contains(event)) {
            Organizer_createdEvents.add(event);
        }
    }

    public void addActiveEvent(Event event) {
        if (event != null && !Organizer_activeEvents.contains(event)) {
            Organizer_activeEvents.add(event);
        }
    }

    public void addCompletedEvent(Event event) {
        if (event != null && !Organizer_completedEvents.contains(event)) {
            Organizer_completedEvents.add(event);
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
        map.put("Organizer_id", Organizer_id);
        map.put("Organizer_email", getEmail());
        map.put("Organizer_notificationsEnabled", Organizer_notificationsEnabled);
        map.put("Organizer_removalReason", Organizer_removalReason);

        // Store event IDs instead of full event objects
        map.put("Organizer_createdEventIDs",   extractEventIds(Organizer_createdEvents));
        map.put("Organizer_activeEventIDs",    extractEventIds(Organizer_activeEvents));
        map.put("Organizer_completedEventIDs", extractEventIds(Organizer_completedEvents));

        List<Map<String, Object>> notifMaps = new ArrayList<>();
        if (Organizer_sentNotifications != null) {
            for (NotificationLog n : Organizer_sentNotifications) {
                if (n != null) notifMaps.add(n.toMap());
            }
        }
        map.put("Organizer_sentNotifications", notifMaps);


        return map;
    }
}
