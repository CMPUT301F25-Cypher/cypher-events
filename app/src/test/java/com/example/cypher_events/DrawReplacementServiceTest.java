package com.example.cypher_events.domain.service;

import com.example.cypher_events.data.repository.EventRepository;
import com.example.cypher_events.domain.model.Entrant;
import com.example.cypher_events.domain.model.Event;
import com.example.cypher_events.util.Result;

import java.util.List;

public class DrawReplacementService {

    private final EventRepository eventRepository;

    public DrawReplacementService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Entrant drawReplacement(String eventId) {

        if (eventId == null || eventId.isEmpty()) {
            return null;
        }

        Result<Event> result = eventRepository.getEventById(eventId);
        if (result == null || result.data == null) {
            return null;
        }

        Event event = result.data;

        List<Entrant> waitlist = event.getEvent_waitlistEntrants();
        List<Entrant> selected = event.getEvent_selectedEntrants();
        List<Entrant> declined = event.getEvent_declinedEntrants();

        if (waitlist == null || waitlist.isEmpty()) {
            return null;
        }

        for (Entrant entrant : waitlist) {

            boolean alreadySelected = (selected != null && selected.contains(entrant));
            boolean alreadyDeclined = (declined != null && declined.contains(entrant));

            if (!alreadySelected && !alreadyDeclined) {

                if (selected != null) {
                    selected.add(entrant);
                }

                eventRepository.updateEvent(event);

                return entrant;
            }
        }

        return null;
    }
}
