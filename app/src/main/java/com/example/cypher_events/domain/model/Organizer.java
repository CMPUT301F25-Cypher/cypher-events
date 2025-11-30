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

    /**
     * @param entrant the entrant backing this organizer
     */
    public Organizer(Entrant entrant) {
        this.Organizer = entrant;
        if (entrant != null) {
            this.Organizer_id = entrant.getEntrant_id();
        }
    }

    /**
     * @param organizerId the explicit organizer id
     * @param entrant the entrant backing this organizer
     */
    public Organizer(String organizerId, Entrant entrant) {
        this.Organizer = entrant;
        this.Organizer_id = organizerId;
    }

    // Basic accessors
    /**
     * @return the organizer id
     */
    public String getOrganizer_id() {
        return Organizer_id;
    }

    /**
     * @param organizer_id the new organizer id
     */
    public void setOrganizer_id(String organizer_id) {
        this.Organizer_id = organizer_id;
    }

    /**
     * @return the backing entrant
     */
    public Entrant getOrganizer() {
        return Organizer;
    }

    /**
     * @param organizer the entrant to associate
     */
    public void setOrganizer(Entrant organizer) {
        this.Organizer = organizer;
        // keep ids aligned when possible
        if (organizer != null && organizer.getEntrant_id() != null) {
            this.Organizer_id = organizer.getEntrant_id();
        }
    }

    // Convenience: shortcuts to Entrant fields
    /**
     * @return the organizer name
     */
    public String getName() {
        return Organizer != null ? Organizer.getEntrant_name() : null;
    }

    /**
     * @return the organizer email
     */
    public String getEmail() {
        return Organizer != null ? Organizer.getEntrant_email() : null;
    }

    /**
     * @return the organizer phone
     */
    public String getPhone() {
        return Organizer != null ? Organizer.getEntrant_phone() : null;
    }

    /**
     * @return latitude in decimal degrees
     */
    public double getLatitude() {
        return Organizer != null ? Organizer.getEntrant_latitude() : 0.0;
    }

    /**
     * @return longitude in decimal degrees
     */
    public double getLongitude() {
        return Organizer != null ? Organizer.getEntrant_longitude() : 0.0;
    }

    /**
     * @return true if admin, false otherwise
     */
    public boolean isAdmin() {
        return Organizer != null && Organizer.isEntrant_isAdmin();
    }

    // Organizer-specific fields
    /**
     * @return created events list
     */
    public List<Event> getOrganizer_createdEvents() {
        return Organizer_createdEvents;
    }

    /**
     * @param organizer_createdEvents new created events list
     */
    public void setOrganizer_createdEvents(List<Event> organizer_createdEvents) {
        this.Organizer_createdEvents =
                organizer_createdEvents != null ? organizer_createdEvents : new ArrayList<>();
    }

    /**
     * @return active events list
     */
    public List<Event> getOrganizer_activeEvents() {
        return Organizer_activeEvents;
    }

    /**
     * @param organizer_activeEvents new active events list
     */
    public void setOrganizer_activeEvents(List<Event> organizer_activeEvents) {
        this.Organizer_activeEvents =
                organizer_activeEvents != null ? organizer_activeEvents : new ArrayList<>();
    }

    /**
     * @return completed events list
     */
    public List<Event> getOrganizer_completedEvents() {
        return Organizer_completedEvents;
    }

    /**
     * @param organizer_completedEvents new completed events list
     */
    public void setOrganizer_completedEvents(List<Event> organizer_completedEvents) {
        this.Organizer_completedEvents =
                organizer_completedEvents != null ? organizer_completedEvents : new ArrayList<>();
    }

    /**
     * @return true if notifications enabled
     */
    public boolean isOrganizer_notificationsEnabled() {
        return Organizer_notificationsEnabled;
    }

    /**
     * @param organizer_notificationsEnabled enable/disable notifications
     */
    public void setOrganizer_notificationsEnabled(boolean organizer_notificationsEnabled) {
        this.Organizer_notificationsEnabled = organizer_notificationsEnabled;
    }

    /**
     * @return sent notifications list
     */
    public List<NotificationLog> getOrganizer_sentNotifications() {
        return Organizer_sentNotifications;
    }
    /**
     * @param organizer_sentNotifications new sent notifications list
     */
    public void setOrganizer_sentNotifications(List<NotificationLog> organizer_sentNotifications) {
        this.Organizer_sentNotifications =
                organizer_sentNotifications != null ? organizer_sentNotifications : new ArrayList<>();
    }

    /**
     * @return removal reason string
     */
    public String getOrganizer_removalReason() {
        return Organizer_removalReason;
    }

    /**
     * @param organizer_removalReason new removal reason
     */
    public void setOrganizer_removalReason(String organizer_removalReason) {
        this.Organizer_removalReason = organizer_removalReason;
    }
    /**
     * @param log notification to add
     */
    public void addSentNotification(NotificationLog log) {
        if (log == null) return;
        if (Organizer_sentNotifications == null) Organizer_sentNotifications = new ArrayList<>();
        Organizer_sentNotifications.add(log);
    }
    /**
     * @param event event to add
     */
    public void addCreatedEvent(Event event) {
        if (event != null && !Organizer_createdEvents.contains(event)) {
            Organizer_createdEvents.add(event);
        }
    }

    /**
     * @param event event to add
     */
    public void addActiveEvent(Event event) {
        if (event != null && !Organizer_activeEvents.contains(event)) {
            Organizer_activeEvents.add(event);
        }
    }

    /**
     * @param event event to add
     */
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
    /**
     * @return a map of organizer data for persistence
     */
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
