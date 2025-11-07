package com.example.cypher_events.data.repository.fake;

import com.example.cypher_events.data.repository.EventRepository;
import com.example.cypher_events.domain.model.Entrant;
import com.example.cypher_events.domain.model.Event;
import com.example.cypher_events.domain.model.Organizer;
import com.example.cypher_events.util.Result;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Fake repository for testing events locally (no Firebase needed).
 */
public class FakeEventRepository implements EventRepository {

    private final Map<String, Event> store = new LinkedHashMap<>();

    public FakeEventRepository() {
        // Dummy Entrant + Organizer
        Entrant entrant = new Entrant("Jane Doe", "jane@example.com", "5554321");
        Organizer organizer = new Organizer(entrant);

        // Example events
        Event e1 = new Event(
                "e1",
                "Campus Tour",
                "Guided tour for first-years",
                "CCIS",
                now(-3600_000),
                now(+86_400_000),
                50,
                organizer
        );

        Event e2 = new Event(
                "e2",
                "Esports Night",
                "Smash + Valorant tournament",
                "SUB",
                now(-3600_000),
                now(+172_800_000),
                32,
                organizer
        );

        store.put(e1.getEvent_id(), e1);
        store.put(e2.getEvent_id(), e2);
    }

    private static long now(long delta) {
        return System.currentTimeMillis() + delta;
    }

    // Implementations of EventRepository interface

    @Override
    public Result<List<Event>> listOpenEvents() {
        return Result.ok(new ArrayList<>(store.values()));
    }

    @Override
    public Result<Event> getEventById(String eventId) {
        Event event = store.get(eventId);
        if (event == null) {
            return Result.err(new Exception("Event not found"));
        }
        return Result.ok(event);
    }

    @Override
    public Result<Void> updateEvent(Event event) {
        store.put(event.getEvent_id(), event);
        return Result.ok(null);
    }

    @Override
    public Result<Void> addEvent(Event event) {
        store.put(event.getEvent_id(), event);
        return Result.ok(null);
    }
}
