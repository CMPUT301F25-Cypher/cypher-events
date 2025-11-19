package com.example.cypher_events.domain.service;

import com.example.cypher_events.data.repository.EventRepository;
import com.example.cypher_events.util.Result;

public class RemoveEventService {

    private final EventRepository eventRepository;

    // Inject repository dependency
    public RemoveEventService(EventRepository repo) {
        this.eventRepository = repo;
    }

    /**
     * Removes an event if the eventId is valid and deletion is successful.
     *
     * @param eventId the ID of the event to delete
     * @return true if deleted successfully, false otherwise
     */
    public boolean removeEvent(String eventId) {

        // Validate event ID
        if (eventId == null || eventId.trim().isEmpty()) {
            return false;
        }

        // Ask repository to delete
        Result<Boolean> result = eventRepository.deleteEvent(eventId);

        // Validate result
        if (result == null || !result.isOk() || result.getData() == null) {
            return false;
        }

        // Return actual deletion status
        return Boolean.TRUE.equals(result.getData());
    }
}
