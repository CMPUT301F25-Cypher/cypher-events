package com.example.cypher_events.domain.model;

import org.junit.Test;
import static org.junit.Assert.*;

public class EventTest {

    @Test
    public void testEventConstructorAndFields() {
        Event event = new Event();
        event.id = "E001";
        event.title = "Yoga Class";
        event.location = "Community Center";

        assertEquals("E001", event.id);
        assertEquals("Yoga Class", event.title);
        assertEquals("Community Center", event.location);
    }
}
