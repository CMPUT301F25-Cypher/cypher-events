package com.example.cypher_events.domain.service;

import com.example.cypher_events.domain.model.Event;
import com.example.cypher_events.domain.model.Entrant;
import com.example.cypher_events.data.repository.EventRepository;
import com.example.cypher_events.util.Result;

import java.util.List;

public class ViewInvitedEntrantsService {
    private final EventRepository eventRepository;

    public ViewInvitedEntrantsService(EventRepository repo) {
        this.eventRepository = repo;
    }

    public List<Entrant> getInvitedEntrants(String eventId) {
        Result<Event> result = eventRepository.getEventById(eventId);
        Event e = result.data;
        return e != null ? e.getEvent_selectedEntrants() : List.of();
    }
}