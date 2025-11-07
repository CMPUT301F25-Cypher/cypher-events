package com.example.cypher_events.domain.model;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class EntrantTest {

    @Test
    public void testSettersAndGetters() {
        Entrant entrant = new Entrant();
        entrant.setEntrant_name("Alice");
        entrant.setEntrant_email("alice@example.com");
        entrant.setEntrant_phone("1234567890");
        entrant.setEntrant_notificationsEnabled(true);
        entrant.setEntrant_locationPermissionGranted(true);

        List<String> accepted = Arrays.asList("event1");
        List<String> declined = Arrays.asList("event2");
        entrant.setEntrant_acceptedEvents(accepted);
        entrant.setEntrant_declinedEvents(declined);

        assertEquals("Alice", entrant.getEntrant_name());
        assertEquals("alice@example.com", entrant.getEntrant_email());
        assertEquals(true, entrant.isEntrant_notificationsEnabled());
        assertEquals(true, entrant.isEntrant_locationPermissionGranted());
        assertEquals(accepted, entrant.getEntrant_acceptedEvents());
        assertEquals(declined, entrant.getEntrant_declinedEvents());
    }

    @Test
    public void testToMapIncludesAcceptedDeclinedLists() {
        Entrant entrant = new Entrant();
        entrant.setEntrant_name("Bob");
        entrant.setEntrant_email("bob@example.com");
        entrant.setEntrant_acceptedEvents(Arrays.asList("eventA"));
        entrant.setEntrant_declinedEvents(Arrays.asList("eventB"));

        Map<String, Object> map = entrant.toMap();
        assertEquals("Bob", map.get("Entrant_name"));
        assertEquals("bob@example.com", map.get("Entrant_email"));
        assertTrue(((List<String>) map.get("Entrant_acceptedEvents")).contains("eventA"));
        assertTrue(((List<String>) map.get("Entrant_declinedEvents")).contains("eventB"));
    }
}
