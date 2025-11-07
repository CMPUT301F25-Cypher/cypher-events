package com.example.cypher_events.domain.services;

import com.example.cypher_events.domain.model.Event;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to filter a list of events by keyword, category, or date range.
 */
public class FilterEventsService {

    /**
     * Filters events based on search query and/or category.
     * @param allEvents full list of events
     * @param query search text entered by user (nullable)
     * @param category category filter (nullable)
     * @return filtered list
     */
    public List<Event> filterEvents(List<Event> allEvents, String query, String category) {
        List<Event> filtered = new ArrayList<>();
        if (allEvents == null) return filtered;

        String lowerQuery = query != null ? query.toLowerCase() : "";

        for (Event event : allEvents) {
            boolean matchesQuery = lowerQuery.isEmpty() ||
                    (event.getEvent_title() != null &&
                            event.getEvent_title().toLowerCase().contains(lowerQuery)) ||
                    (event.getEvent_location() != null &&
                            event.getEvent_location().toLowerCase().contains(lowerQuery));

            boolean matchesCategory = (category == null || category.equals("All") ||
                    (event.getEvent_category() != null &&
                            event.getEvent_category().equalsIgnoreCase(category)));

            if (matchesQuery && matchesCategory) {
                filtered.add(event);
            }
        }

        return filtered;
    }
}
