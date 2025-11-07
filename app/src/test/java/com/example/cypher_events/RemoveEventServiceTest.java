package com.example.cypher_events;

import com.example.cypher_events.data.repository.fake.FakeEventRepository;
import com.example.cypher_events.domain.service.RemoveEventService;
import org.junit.Test;
import static org.junit.Assert.*;

public class RemoveEventServiceTest {

    @Test
    public void testRemoveExistingEvent() {
        FakeEventRepository repo = new FakeEventRepository();
        RemoveEventService service = new RemoveEventService(repo);

        assertTrue(service.removeEvent("e1"));  // event exists
        assertFalse(service.removeEvent("e1")); // already deleted
    }

    @Test
    public void testRemoveInvalidEvent() {
        FakeEventRepository repo = new FakeEventRepository();
        RemoveEventService service = new RemoveEventService(repo);

        assertFalse(service.removeEvent(null));
        assertFalse(service.removeEvent(""));
        assertFalse(service.removeEvent("nonexistent"));
    }
}