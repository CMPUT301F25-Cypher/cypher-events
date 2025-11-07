package com.example.cypher_events;

import com.example.cypher_events.domain.model.Entrant;
import org.junit.Test;
import static org.junit.Assert.*;
public class EntrantTest {
    @Test
    public void entrantSettersAndGetters_workProperly() {
        Entrant entrant = new Entrant();
        entrant.setEntrant_name("Alice");
        entrant.getEntrant_email("alice@example.com");
        assertEquals("Alice", entrant.getEntrant_name());
        assertEquals("alice@example.com", entrant.getEntrant_email());
    }
}
