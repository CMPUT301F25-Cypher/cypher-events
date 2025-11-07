package com.example.cypher_events.domain.service;

import com.example.cypher_events.domain.model.Event;
import com.example.cypher_events.domain.model.Entrant;
import com.example.cypher_events.data.repository.EventRepository;
import com.example.cypher_events.util.Result;

import java.io.*;
import java.util.List;

public class ExportEntrantListService {
    private final EventRepository eventRepository;

    public ExportEntrantListService(EventRepository repo) {
        this.eventRepository = repo;
    }

    public String exportAsCSV(String eventId) throws IOException {
        Result<Event> result = eventRepository.getEventById(eventId);
        Event event = result.data;
        if (event == null) return null;

        List<Entrant> entrants = event.getEvent_selectedEntrants();
        if (entrants == null || entrants.isEmpty()) return null;

        StringWriter writer = new StringWriter();
        writer.append("Name,Email,Phone\n");
        for (Entrant e : entrants) {
            writer.append(String.format("%s,%s,%s\n",
                    e.getEntrant_name(), e.getEntrant_email(), e.getEntrant_phone()));
        }
        return writer.toString();
    }
}