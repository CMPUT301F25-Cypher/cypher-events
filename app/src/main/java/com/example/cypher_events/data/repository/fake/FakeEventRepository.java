package com.example.cypher_events.data.repository.fake;

import com.example.cypher_events.*;
import com.example.cypher_events.data.repository.EventRepository;
import com.example.cypher_events.domain.model.Event;
import java.util.*;

public class FakeEventRepository implements EventRepository {
    private final Map<String, Event> store = new LinkedHashMap<>();

    public FakeEventRepository() {
        Event e1 = new Event("e1","Campus Tour","Guided tour","CCIS",
                now(-3600_000), now(+86_400_000), 50);
        Event e2 = new Event("e2","Esports Night","Smash + Valorant","SUB",
                now(-3600_000), now(+172_800_000), 32);
        store.put(e1.id, e1);
        store.put(e2.id, e2);
    }

    private static long now(long delta) { return System.currentTimeMillis() + delta; }

    @Override public Result<List<Event>> listOpenEvents() {
        return Result.ok(new ArrayList<>(store.values()));
    }

    @Override public Result<Event> getById(String id) {
        Event e = store.get(id);
        return (e != null) ? Result.ok(e) : Result.err(new NoSuchElementException(id));
    }
}
