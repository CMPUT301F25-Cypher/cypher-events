package com.example.cypher_events.data.repository;

import com.example.cypher_events.util.Result;
import com.example.cypher_events.domain.model.Event;
import java.util.List;

public interface EventRepository {
    Result<List<Event>> listOpenEvents();
    Result<Event> getEventById(String eventId);
    Result<Void> updateEvent(Event event);
    Result<Void> addEvent(Event event);
}
