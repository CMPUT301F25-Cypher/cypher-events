/**
 * EventRepository.java
 *
 * Purpose:
 * Defined contract for event data retrieval in the repository layer.
 * Implementations may fetch from Firebase, local cache, or test fakes
 */

package com.example.cypher_events.data.repository;

import com.example.cypher_events.Result;
import com.example.cypher_events.domain.model.Event;
import java.util.List;

public interface EventRepository {
    Result<List<Event>> listOpenEvents();
    Result<Event> getById(String eventId);
}
