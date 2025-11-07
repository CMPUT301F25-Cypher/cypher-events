package com.example.cypher_events;
import com.example.cypher_events.domain.model.Event;
import com.example.cypher_events.data.repository.fake.FakeEventRepository;
import com.example.cypher_events.domain.service.LimitEntrantsService;
import org.junit.Test;
import static org.junit.Assert.*;

public class LimitEntrantsServiceTest {

    @Test
    public void testUpdateEventCapacity() {
        FakeEventRepository repo = new FakeEventRepository();
        LimitEntrantsService service = new LimitEntrantsService(repo);

        boolean result = service.updateEventCapacity("e1", 75);
        assertTrue(result);

        Event updated = repo.getEventById("e1").data;
        assertEquals(75, updated.getEvent_capacity());
    }
}
