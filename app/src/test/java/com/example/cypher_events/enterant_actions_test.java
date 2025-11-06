package com.example.cypher_events;

import com.example.cypher_events.domain.service.Enterant_actions;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Map;

public class enterant_actions_test {

    @Test
    public void add_enterant_event_test() {
        Enterant_actions actions = new Enterant_actions();
        ArrayList<Map<String, Object>> event = new ArrayList<>();
        ArrayList<String> history = new ArrayList<>();

        actions.add_enterant_to_event_array(event, "Bruce", "Bruce@gmail.com", 1234567, "301Class", history);

        // Verify entrant added
        assertEquals(1, event.size());
        assertTrue(history.contains("301Class"));

        Map<String, Object> added = event.get(0);
        assertEquals("Bruce", added.get("name"));
        assertEquals("Bruce@gmail.com", added.get("email"));
        assertEquals(1234567, added.get("phone"));
    }

    @Test
    public void remove_enterant_event_test() {
        Enterant_actions actions = new Enterant_actions();
        ArrayList<Map<String, Object>> event = new ArrayList<>();
        ArrayList<String> history = new ArrayList<>();

        actions.add_enterant_to_event_array(event, "Bruce", "Bruce@gmail.com", 12345, "301Class", history);
        actions.add_enterant_to_event_array(event, "Peter", "peter@gmail.com", 23456, "301Class", history);
        assertEquals(2, event.size());
        actions.remove_enterant_to_event_array(event, "Bruce", "Bruce@gmail.com", 12345, "301Class", history);
        assertEquals(1, event.size());
        Map<String, Object> remaining = event.get(0);
        assertEquals("Peter", remaining.get("name"));
        assertEquals("peter@gmail.com", remaining.get("email"));
        assertEquals(23456, remaining.get("phone"));
        assertTrue(history.contains("301Class"));
    }
}
