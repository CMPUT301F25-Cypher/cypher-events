package com.example.cypher_events.domain.service;

import com.example.cypher_events.data.repository.EventRepository;
import com.example.cypher_events.domain.model.Entrant;
import com.example.cypher_events.domain.model.Event;
import com.example.cypher_events.util.Result;

import java.util.List;

public class DrawWinnerService {

    private final EventRepository eventRepository;

    public DrawWinnerService(EventRepository repo) {
        this.eventRepository = repo;
    }

    /**
     * Draw winner from the event waitlist.
     * @param eventId event identifier
     * @return the selected or null if no eligible entrant found
     */

    public Entrant drawWinner(String eventId) {

        Result<Event> result = eventRepository.getEventById(eventId);

        if (result == null || !result.isOk() || result.getData() == null) {
            return null;
        }

        Event event = result.getData();

        List<Entrant> waitlist = event.getEvent_waitlistEntrants();

        if (waitlist == null || waitlist.isEmpty()) {
            return null;
        }

        // Ensure organizer cannot win their own event
        String organizerEmail = event.getEvent_organizer() != null
                ? event.getEvent_organizer().getEmail()
                : null;

        for (Entrant entrant : waitlist) {

            if (organizerEmail != null &&
                    organizerEmail.equalsIgnoreCase(entrant.getEntrant_email())) {
                continue;
            }

            event.addSelectedEntrant(entrant);
            eventRepository.updateEvent(event);

            return entrant;
        }

        return null;
    }
}
