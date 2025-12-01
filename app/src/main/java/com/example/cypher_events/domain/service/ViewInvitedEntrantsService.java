package com.example.cypher_events.domain.service;

import com.example.cypher_events.domain.model.Event;
import com.example.cypher_events.domain.model.Entrant;
import com.example.cypher_events.data.repository.EventRepository;
import com.example.cypher_events.util.Result;

import java.util.Collections;
import java.util.List;

public class ViewInvitedEntrantsService {
    private final EventRepository eventRepository;

    public ViewInvitedEntrantsService(EventRepository repo) {
        this.eventRepository = repo;
    }
    /**
     * Get the list of invited entrants for an event.
     *
     * @param eventId identifier of the event to query
     * @return list of invited entrants, or an empty list if none exist or the event cannot be loaded
     *
     * returns selected/invited entrants
     */

    public List<Entrant> getInvitedEntrants(String eventId) {
        Result<Event> result = eventRepository.getEventById(eventId);
        if (result == null || !result.isOk() || result.getData() == null) {
            return Collections.emptyList();
        }

        Event event = result.getData();
        List<Entrant> invited = event.getEvent_selectedEntrants();
        return invited != null ? invited : Collections.emptyList();
    }
}
