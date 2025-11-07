package com.example.cypher_events.domain.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Event class
 * Represents a single community event with lottery sign-ups and status tracking.
 * Each event may have an organizer, multiple entrants, and a final list of selected participants.
 */
public class Event {
    private String Event_id;
    private String Event_title;
    private String Event_description;
    private String Event_location;
    private String Event_category;  // e.g., Sports, Workshop, Art, Tech Talk
    private String Event_status;    // e.g., "Draft", "Open", "LotteryPending", "Completed"

    private long Event_signupStartUtc;
    private long Event_signupEndUtc;
    private int Event_capacity;

    private Organizer Event_organizer;
    private List<Entrant> Event_joinedEntrants;      // All users who signed up
    private List<Entrant> Event_selectedEntrants;    // Winners from lottery
    private List<Entrant> Event_declinedEntrants;    // Entrants who declined after being selected

    private boolean Event_isLotteryEnabled;
    private boolean Event_isActive;


    // Constructors
    public Event() {}

    public Event(String id, String title, String description, String location,
                 long start, long end, int cap) {
        this.Event_id = id;
        this.Event_title = title;
        this.Event_description = description;
        this.Event_location = location;
        this.Event_signupStartUtc = start;
        this.Event_signupEndUtc = end;
        this.Event_capacity = cap;
    }

    // NEW overloaded constructor (includes creator)
    public Event(String id, String title, String description, String location,
                 long start, long end, int cap, Organizer creator) {
        this(id, title, description, location, start, end, cap);
        this.Event_organizer = creator;
    }


    // Getters & setters
    public String getEvent_id() { return Event_id; }
    public void setEvent_id(String event_id) { this.Event_id = event_id; }

    public String getEvent_title() { return Event_title; }
    public void setEvent_title(String event_title) { this.Event_title = event_title; }

    public String getEvent_description() { return Event_description; }
    public void setEvent_description(String event_description) { this.Event_description = event_description; }

    public String getEvent_location() { return Event_location; }
    public void setEvent_location(String event_location) { this.Event_location = event_location; }

    public String getEvent_category() { return Event_category; }
    public void setEvent_category(String event_category) { this.Event_category = event_category; }

    public String getEvent_status() { return Event_status; }
    public void setEvent_status(String event_status) { this.Event_status = event_status; }

    public long getEvent_signupStartUtc() { return Event_signupStartUtc; }
    public void setEvent_signupStartUtc(long event_signupStartUtc) { this.Event_signupStartUtc = event_signupStartUtc; }

    public long getEvent_signupEndUtc() { return Event_signupEndUtc; }
    public void setEvent_signupEndUtc(long event_signupEndUtc) { this.Event_signupEndUtc = event_signupEndUtc; }

    public int getEvent_capacity() { return Event_capacity; }
    public void setEvent_capacity(int event_capacity) { this.Event_capacity = event_capacity; }

    public Organizer getEvent_organizer() { return Event_organizer; }
    public void setEvent_organizer(Organizer event_organizer) { this.Event_organizer = event_organizer; }

    public List<Entrant> getEvent_joinedEntrants() { return Event_joinedEntrants; }
    public void setEvent_joinedEntrants(List<Entrant> event_joinedEntrants) { this.Event_joinedEntrants = event_joinedEntrants; }

    public List<Entrant> getEvent_selectedEntrants() { return Event_selectedEntrants; }
    public void setEvent_selectedEntrants(List<Entrant> event_selectedEntrants) { this.Event_selectedEntrants = event_selectedEntrants; }

    public List<Entrant> getEvent_declinedEntrants() { return Event_declinedEntrants; }
    public void setEvent_declinedEntrants(List<Entrant> event_declinedEntrants) { this.Event_declinedEntrants = event_declinedEntrants; }

    public boolean isEvent_isLotteryEnabled() { return Event_isLotteryEnabled; }
    public void setEvent_isLotteryEnabled(boolean event_isLotteryEnabled) { this.Event_isLotteryEnabled = event_isLotteryEnabled; }

    public boolean isEvent_isActive() { return Event_isActive; }
    public void setEvent_isActive(boolean event_isActive) { this.Event_isActive = event_isActive; }

    // helper
    private List<Map<String, Object>> summarizeEntrants(List<Entrant> entrants) {
        List<Map<String, Object>> list = new java.util.ArrayList<>();
        if (entrants != null) {
            for (Entrant e : entrants) {
                Map<String, Object> m = new HashMap<>();
                m.put("Entrant_name", e.getEntrant_name());
                m.put("Entrant_email", e.getEntrant_email());
                list.add(m);
            }
        }
        return list;
    }

    // Firebase mapping
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("Event_id", Event_id);
        map.put("Event_title", Event_title);
        map.put("Event_description", Event_description);
        map.put("Event_location", Event_location);
        map.put("Event_category", Event_category);
        map.put("Event_status", Event_status);
        map.put("Event_signupStartUtc", Event_signupStartUtc);
        map.put("Event_signupEndUtc", Event_signupEndUtc);
        map.put("Event_capacity", Event_capacity);
        map.put("Event_isLotteryEnabled", Event_isLotteryEnabled);
        map.put("Event_isActive", Event_isActive);

        // Avoid recursion: store only organizer’s identity
        map.put("Event_organizerEmail",
                Event_organizer != null ? Event_organizer.getEmail() : null);

        // Simplify entrant lists to avoid circular structures
        map.put("Event_joinedEntrants", summarizeEntrants(Event_joinedEntrants));
        map.put("Event_selectedEntrants", summarizeEntrants(Event_selectedEntrants));
        map.put("Event_declinedEntrants", summarizeEntrants(Event_declinedEntrants));
        return map;
    }
}