package com.example.cypher_events.domain.service;

import com.example.cypher_events.domain.model.Event;
import com.example.cypher_events.data.repository.EventRepository;
import com.example.cypher_events.util.Result;

public class LimitEntrantsService {
    private final EventRepository eventRepository;

    public LimitEntrantsService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public boolean updateEventCapacity(String eventId, int newLimit) {
        Result<Event> result = eventRepository.getEventById(eventId);
        Event event = result.getData();
        if (event == null) return false;

        event.setEvent_capacity(newLimit);
        eventRepository.updateEvent(event);
        return true;
    }
}