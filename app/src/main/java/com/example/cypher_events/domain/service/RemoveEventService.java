package com.example.cypher_events.domain.service;

import com.example.cypher_events.data.repository.EventRepository;
import com.example.cypher_events.util.Result;

public class RemoveEventService {
    private final EventRepository eventRepository;

    public RemoveEventService(EventRepository repo) {
        this.eventRepository = repo;
    }

    public boolean removeEvent(String eventId) {
        if (eventId == null || eventId.isEmpty()) return false;

        Result<Boolean> result = eventRepository.deleteEvent(eventId);
        return result != null && result.isOk() && Boolean.TRUE.equals(result.data);
    }
}