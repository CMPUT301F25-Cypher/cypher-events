package com.example.cypher_events;

import com.example.cypher_events.domain.model.Entrant;
import com.example.cypher_events.domain.model.Event;
import com.example.cypher_events.data.repository.fake.FakeEventRepository;
import com.example.cypher_events.domain.service.AcceptInvitationService.DrawReplacementService;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class DrawReplacementServiceTest {

    @Test
    public void testDrawReplacementFindsNewEntrant() {
        FakeEventRepository repo = new FakeEventRepository();
        Event event = repo.getEventById("e1").data;

        Entrant a = new Entrant("A", "a@example.com", "111");
        Entrant b = new Entrant("B", "b@example.com", "222");
        Entrant c = new Entrant("C", "c@example.com", "333");

        event.setEvent_joinedEntrants(List.of(a, b, c));
        event.setEvent_selectedEntrants(new ArrayList<>(List.of(a)));
        event.setEvent_declinedEntrants(new ArrayList<>(List.of(a)));

        repo.updateEvent(event);

        DrawReplacementService drawService = new DrawReplacementService(repo);
        Entrant replacement = drawService.drawReplacement("e1");

        assertNotNull("Should find a new replacement entrant", replacement);
        assertTrue(replacement == b || replacement == c);
    }

    @Test
    public void testDrawReplacementReturnsNullWhenNoAvailableEntrant() {
        FakeEventRepository repo = new FakeEventRepository();
        Event event = repo.getEventById("e1").data;

        Entrant a = new Entrant("A", "a@example.com", "111");
        event.setEvent_joinedEntrants(List.of(a));
        event.setEvent_selectedEntrants(new ArrayList<>(List.of(a)));
        event.setEvent_declinedEntrants(new ArrayList<>(List.of(a)));

        repo.updateEvent(event);

        DrawReplacementService drawService = new DrawReplacementService(repo);
        Entrant replacement = drawService.drawReplacement("e1");

        assertNull("No replacement should be found", replacement);
    }
}