package com.example.cypher_events.domain.model;

public class Event {
    public String id, title, description, location;
    public long signupStartUtc, signupEndUtc;
    public int capacity;

    public Event() {}
    public Event(String id, String title, String description, String location,
                 long start, long end, int cap) {
        this.id = id; this.title = title; this.description = description;
        this.location = location; this.signupStartUtc = start;
        this.signupEndUtc = end; this.capacity = cap;
    }
}
