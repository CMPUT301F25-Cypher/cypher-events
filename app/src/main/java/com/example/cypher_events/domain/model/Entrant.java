package com.example.cypher_events.domain.model;

public class Entrant {
    public String uid, displayName;

    public Entrant() {}
    public Entrant(String uid, String displayName) {
        this.uid = uid; this.displayName = displayName;
    }
}
