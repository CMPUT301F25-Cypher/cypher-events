package com.example.cypher_events.domain.service;

import com.example.cypher_events.data.repository.EventRepository;
import com.example.cypher_events.util.Result;

public class RemoveEventService {

    private final EventRepository eventRepository;

    public RemoveEventService(EventRepository repo) {
        this.eventRepository = repo;
    }

    public boolean removeEvent(String eventId) {

        if (eventId == null || eventId.trim().isEmpty()) {
            return false;
        }

        Result<Boolean> result = eventRepository.deleteEvent(eventId);

        if (result == null || !result.isOk()) {
            return false;
        }

        Boolean deleted = result.getData();
        return Boolean.TRUE.equals(deleted);
    }
}
