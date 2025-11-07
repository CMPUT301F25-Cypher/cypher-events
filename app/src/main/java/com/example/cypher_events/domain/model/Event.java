package com.example.cypher_events.domain.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Event class
 * Represents a single community event with lottery sign-ups and status tracking.
 * Each event may have an organizer, multiple entrants, and a final list of selected participants.
 */
public class Event {

    private String event_id;
    private String event_title;
    private String event_description;
    private String event_location;
    private String event_category;  // e.g., Sports, Workshop, Art, Tech Talk
    private String event_status;    // e.g., "Draft", "Open", "LotteryPending", "Completed"

    private long event_signupStartUtc;
    private long event_signupEndUtc;
    private int event_capacity;

    private Organizer event_organizer;

    private List<Entrant> event_waitlistEntrants = new ArrayList<>();
    private List<Entrant> event_selectedEntrants = new ArrayList<>();
    private List<Entrant> event_joinedEntrants = new ArrayList<>();
    private List<Entrant> event_declinedEntrants = new ArrayList<>();

    private boolean event_isLotteryEnabled;
    private boolean event_isActive;

    // Constructors
    public Event() {}

    public Event(String id, String title, String description, String location,
                 long start, long end, int cap) {
        this.event_id = id;
        this.event_title = title;
        this.event_description = description;
        this.event_location = location;
        this.event_signupStartUtc = start;
        this.event_signupEndUtc = end;
        this.event_capacity = cap;
    }

    // Overloaded constructor (includes organizer)
    public Event(String id, String title, String description, String location,
                 long start, long end, int cap, Organizer organizer) {
        this(id, title, description, location, start, end, cap);
        this.event_organizer = organizer;
    }

    // GETTERS / SETTERS
    public String getEvent_id() { return event_id; }
    public void setEvent_id(String id) { this.event_id = id; }

    public String getEvent_title() { return event_title; }
    public void setEvent_title(String title) { this.event_title = title; }

    public String getEvent_description() { return event_description; }
    public void setEvent_description(String desc) { this.event_description = desc; }

    public String getEvent_location() { return event_location; }
    public void setEvent_location(String location) { this.event_location = location; }

    public String getEvent_category() { return event_category; }
    public void setEvent_category(String category) { this.event_category = category; }

    public String getEvent_status() { return event_status; }
    public void setEvent_status(String status) { this.event_status = status; }

    public long getEvent_signupStartUtc() { return event_signupStartUtc; }
    public void setEvent_signupStartUtc(long startUtc) { this.event_signupStartUtc = startUtc; }

    public long getEvent_signupEndUtc() { return event_signupEndUtc; }
    public void setEvent_signupEndUtc(long endUtc) { this.event_signupEndUtc = endUtc; }

    public int getEvent_capacity() { return event_capacity; }
    public void setEvent_capacity(int capacity) { this.event_capacity = capacity; }

    public Organizer getEvent_organizer() { return event_organizer; }
    public void setEvent_organizer(Organizer organizer) { this.event_organizer = organizer; }

    public List<Entrant> getEvent_selectedEntrants() { return event_selectedEntrants; }
    public void setEvent_selectedEntrants(List<Entrant> selected) { this.event_selectedEntrants = selected; }

    public List<Entrant> getEvent_joinedEntrants() { return event_joinedEntrants; }
    public void setEvent_joinedEntrants(List<Entrant> joined) { this.event_joinedEntrants = joined; }

    public List<Entrant> getEvent_declinedEntrants() { return event_declinedEntrants; }
    public void setEvent_declinedEntrants(List<Entrant> declined) { this.event_declinedEntrants = declined; }

    public boolean isEvent_isLotteryEnabled() { return event_isLotteryEnabled; }
    public void setEvent_isLotteryEnabled(boolean lotteryEnabled) { this.event_isLotteryEnabled = lotteryEnabled; }

    public boolean isEvent_isActive() { return event_isActive; }
    public void setEvent_isActive(boolean active) { this.event_isActive = active; }

    public List<Entrant> getEvent_waitlistEntrants() {
        return event_waitlistEntrants;
    }

    public void setEvent_waitlistEntrants(List<Entrant> waitlistEntrants) {
        this.event_waitlistEntrants = waitlistEntrants;
    }

    // HELPERS
    private List<Map<String, Object>> summarizeEntrants(List<Entrant> entrants) {
        List<Map<String, Object>> list = new ArrayList<>();
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

    // FIREBASE MAPPING
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("Event_id", event_id);
        map.put("Event_title", event_title);
        map.put("Event_description", event_description);
        map.put("Event_location", event_location);
        map.put("Event_category", event_category);
        map.put("Event_status", event_status);
        map.put("Event_signupStartUtc", event_signupStartUtc);
        map.put("Event_signupEndUtc", event_signupEndUtc);
        map.put("Event_capacity", event_capacity);
        map.put("Event_isLotteryEnabled", event_isLotteryEnabled);
        map.put("Event_isActive", event_isActive);

        map.put("Event_organizerEmail",
                event_organizer != null ? event_organizer.getEmail() : null);

        map.put("Event_joinedEntrants", summarizeEntrants(event_joinedEntrants));
        map.put("Event_selectedEntrants", summarizeEntrants(event_selectedEntrants));
        map.put("Event_declinedEntrants", summarizeEntrants(event_declinedEntrants));

        map.put("Event_waitlistEntrants", summarizeEntrants(event_waitlistEntrants));

        return map;
    }
}
