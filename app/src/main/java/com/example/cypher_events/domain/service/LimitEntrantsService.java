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

        // Validate input
        if (eventId == null || eventId.trim().isEmpty()) {
            return false;
        }

        if (newLimit <= 0) {
            return false;
        }

        // Fetch event correctly using getter
        Result<Event> result = eventRepository.getEventById(eventId);
        if (result == null || !result.isOk() || result.getData() == null) {
            return false;
        }

        Event event = result.getData();

        // Check existing waitlist against new limit
        if (event.getEvent_waitlistEntrants() != null &&
                event.getEvent_waitlistEntrants().size() > newLimit) {
            return false;
        }

        // Apply update
        event.setEvent_capacity(newLimit);

        // Persist to repository
        eventRepository.updateEvent(event);

        return true;
    }
}
