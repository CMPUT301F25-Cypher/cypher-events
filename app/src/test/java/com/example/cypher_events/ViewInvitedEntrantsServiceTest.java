package com.example.cypher_events;

import com.example.cypher_events.domain.model.Entrant;
import com.example.cypher_events.domain.model.Event;
import com.example.cypher_events.data.repository.fake.FakeEventRepository;
import com.example.cypher_events.domain.service.ViewInvitedEntrantsService;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class ViewInvitedEntrantsServiceTest {

    @Test
    public void testGetInvitedEntrants() {
        FakeEventRepository repo = new FakeEventRepository();
        Event event = repo.getEventById("e1").data;

        Entrant a = new Entrant("A", "a@example.com", "111");
        Entrant b = new Entrant("B", "b@example.com", "222");
        event.setEvent_selectedEntrants(List.of(a, b));
        repo.updateEvent(event);

        ViewInvitedEntrantsService service = new ViewInvitedEntrantsService(repo);
        List<Entrant> invited = service.getInvitedEntrants("e1");

        assertEquals(2, invited.size());
        assertEquals("A", invited.get(0).getEntrant_name());
    }
}