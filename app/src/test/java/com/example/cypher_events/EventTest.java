package com.example.cypher_events;

import com.example.cypher_events.domain.model.Event;
import org.junit.Test;
import static org.junit.Assert.*;

public class EventTest {
    @Test
    public void eventConstructor_setsAllFields() {
        Event e = new Event("1", "Hackathon", "Coding", "Campus", 100L, 200L, 50);
        assertEquals("Hackathon", e.title);
        assertEquals("Campus", e.location);
        assertEquals(50, e.capacity);
    }
}
