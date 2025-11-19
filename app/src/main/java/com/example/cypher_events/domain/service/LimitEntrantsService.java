package com.example.cypher_events.domain.service;

import com.example.cypher_events.domain.model.Event;
import com.example.cypher_events.data.repository.EventRepository;
import com.example.cypher_events.util.Result;

public class LimitEntrantsService {

    private final EventRepository eventRepository;

    public LimitEntrantsService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    // Update the maximum number of entrants allowed for an event
    public boolean updateEventCapacity(String eventId, int newLimit) {

        // Validate input
        if (eventId == null || eventId.trim().isEmpty()) {
            return false;
        }

        // Capacity must be positive
        if (newLimit <= 0) {
            return false;
        }

        // Fetch event from repository
        Result<Event> result = eventRepository.getEventById(eventId);
        if (result == null || !result.isOk() || result.getData() == null) {
            return false;
        }

        Event event = result.getData();
        if (event == null) {
            return false;
        }

        // Optional check: ensure waitlist is not larger than new capacity
        if (event.getEvent_waitlistEntrants() != null &&
                event.getEvent_waitlistEntrants().size() > newLimit) {
            return false;
        }

        // Update event capacity
        event.setEvent_capacity(newLimit);

        // Persist changes
        eventRepository.updateEvent(event);

        return true;
    }
}
