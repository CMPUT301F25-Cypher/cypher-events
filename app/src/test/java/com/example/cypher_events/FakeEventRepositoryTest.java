package com.example.cypher_events;

import com.example.cypher_events.data.repository.fake.FakeEventRepository;
import com.example.cypher_events.domain.model.Event;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;
public class FakeEventRepositoryTest {
    @Test
    public void listOpenEvents_returnsNonEmptyList() {
        FakeEventRepository repo = new FakeEventRepository();
        List<Event> events = repo.listOpenEvents().value();
        assertTrue(events.size() > 0);
    }

    @Test
    public void getById_returnsCorrectEvent() {
        FakeEventRepository repo = new FakeEventRepository();
        Event event = repo.getById("e1").value();
        assertEquals("e1", event.id);
    }
}
