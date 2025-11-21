package com.example.cypher_events.domain.service;

import com.example.cypher_events.data.repository.EventRepository;
import com.example.cypher_events.domain.model.Entrant;
import com.example.cypher_events.domain.model.Event;
import com.example.cypher_events.util.Result;

import java.util.List;

public class ExportEntrantListService {

    private final EventRepository eventRepository;

    public ExportEntrantListService(EventRepository repo) {
        this.eventRepository = repo;
    }


    public List<Entrant> exportSelectedEntrants(String eventId) {

        if (eventId == null || eventId.trim().isEmpty()) {
            return null;
        }

        // Fetch event
        Result<Event> result = eventRepository.getEventById(eventId);

        // Validate result
        if (result == null || !result.isOk() || result.getData() == null) {
            return null;
        }

        Event event = result.getData();
        if (event == null) {
            return null;
        }

        // Return selected entrants list
        return event.getEvent_selectedEntrants();
    }
}
