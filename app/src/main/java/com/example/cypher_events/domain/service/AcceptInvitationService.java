package com.example.cypher_events.domain.service;

import com.example.cypher_events.data.repository.EventRepository;
import com.example.cypher_events.domain.model.Entrant;
import com.example.cypher_events.domain.model.Event;
import com.example.cypher_events.util.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AcceptInvitationService {

    private final Map<String, String> deviceToUser = new HashMap<>();
    private final Map<String, Map<String, Boolean>> accepted = new HashMap<>();

    public void registerDeviceUser(String deviceId, String userId) {
        deviceToUser.put(deviceId, userId);
    }

    public String acceptInvitation(String deviceId, String eventId) {
        if (deviceId == null || deviceId.isEmpty() || eventId == null || eventId.isEmpty()) {
            return "Invalid device or event.";
        }
        String userId = deviceToUser.get(deviceId);
        if (userId == null) return "No user for device.";

        Map<String, Boolean> Events = accepted.get(userId);
        if (Events == null) {
            Events = new HashMap<>();
            accepted.put(userId, Events);
        }
        if (Boolean.TRUE.equals(Events.get(eventId))) return "Already accepted.";

        Events.put(eventId, true);
        return "Invitation accepted.";
    }

    public boolean isAccepted(String userId, String eventId) {
        Map<String, Boolean> Events = accepted.get(userId);
        return Events != null && Boolean.TRUE.equals(Events.get(eventId));
    }

    public static class DrawReplacementService {
        private final EventRepository eventRepository;

        public DrawReplacementService(EventRepository eventRepository) {
            this.eventRepository = eventRepository;
        }

        public Entrant drawReplacement(String eventId) {
            Result<Event> result = eventRepository.getEventById(eventId);
            Event event = result.data;
            if (event == null) return null;

            List<Entrant> joined = event.getEvent_joinedEntrants();
            List<Entrant> selected = event.getEvent_selectedEntrants();
            List<Entrant> declined = event.getEvent_declinedEntrants();

            if (joined == null || joined.isEmpty()) return null;

            // pick from joined who aren’t selected or declined
            for (Entrant e : joined) {
                if (!selected.contains(e) && !declined.contains(e)) {
                    selected.add(e);
                    eventRepository.updateEvent(event);
                    return e;
                }
            }
            return null; // no replacement found
        }
    }
}