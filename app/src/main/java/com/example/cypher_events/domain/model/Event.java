package com.example.cypher_events.domain.model;

import java.util.List;

/**
 * Represents an Event in Firestore.
 * Must include a no-arg constructor and public fields or getters for Firestore deserialization.
 */

public class Event {
    public String id, title, description, location;
    public long signupStartUtc, signupEndUtc;
    public int capacity;
    public List<String> interests; //tags for filtering

    /**Default constructor required for Firestore deserialization.*/
    public Event() {}

    /**
     * Constructs a full Event object.
     * @param id Unique identifier of the event.
     * @param title Name of the event.
     * @param description Textual description of the event.
     * @param location Location where the event takes place.
     * @param signupStartUtc UTC timestamp for signup start.
     * @param signupEndUtc UTC timestamp for signup end.
     * @param capacity Maximum allowed participants.
     */
    public Event(String id, String title, String description, String location,
                 long signupStartUtc, long signupEndUtc, int capacity, List<String> interests) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.signupStartUtc = signupStartUtc;
        this.signupEndUtc = signupEndUtc;
        this.capacity = capacity;
        this.interests = interests;
    }
}
