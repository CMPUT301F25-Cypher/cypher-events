package com.example.cypher_events.domain.service;

import com.example.cypher_events.data.repository.EventRepository;
import com.example.cypher_events.domain.model.Entrant;
import com.example.cypher_events.domain.model.Event;
import com.example.cypher_events.util.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AcceptInvitationService {

    // deviceId -> userId
    private final Map<String, String> deviceToUser = new HashMap<>();

    // userId -> (eventId -> accepted?)
    private final Map<String, Map<String, Boolean>> accepted = new HashMap<>();

    // register a device to a user
    public void registerDeviceUser(String deviceId, String userId) {
        if (deviceId == null || userId == null) {
            return;
        }
        deviceToUser.put(deviceId, userId);
    }

    // accept an invitation for the user associated with this device
    public String acceptInvitation(String deviceId, String eventId) {
        if (deviceId == null || deviceId.isEmpty()
                || eventId == null || eventId.isEmpty()) {
            return "Invalid device or event.";
        }

        String userId = deviceToUser.get(deviceId);
        if (userId == null) {
            return "No user for device.";
        }

        Map<String, Boolean> eventsForUser = accepted.get(userId);
        if (eventsForUser == null) {
            eventsForUser = new HashMap<>();
            accepted.put(userId, eventsForUser);
        }

        if (Boolean.TRUE.equals(eventsForUser.get(eventId))) {
            return "Already accepted.";
        }

        eventsForUser.put(eventId, true);
        return "Invitation accepted.";
    }

    // check if user has accepted an event
    public boolean isAccepted(String userId, String eventId) {
        Map<String, Boolean> eventsForUser = accepted.get(userId);
        return eventsForUser != null && Boolean.TRUE.equals(eventsForUser.get(eventId));
    }

    // ───────────────────────────────────────────────────────────────
    // Replacement draw logic (for organizer side)
    // ───────────────────────────────────────────────────────────────
    public static class DrawReplacementService {

        private final EventRepository eventRepository;

        public DrawReplacementService(EventRepository eventRepository) {
            this.eventRepository = eventRepository;
        }

        /**
         * Draw a replacement entrant for a given eventId.
         * Returns the chosen Entrant or null if none can be drawn.
         */
        public Entrant drawReplacement(String eventId) {
            if (eventId == null || eventId.isEmpty()) {
                return null;
            }

            // get event from repository
            Result<Event> result = eventRepository.getEventById(eventId);
            if (result == null || !result.isOk() || result.getData() == null) {
                return null;
            }

            Event event = result.getData();

            // lists on the event
            List<Entrant> waitlist = event.getEvent_waitlistEntrants();
            List<Entrant> selected = event.getEvent_selectedEntrants();
            List<Entrant> declined = event.getEvent_declinedEntrants();

            if (waitlist == null || waitlist.isEmpty()) {
                return null;
            }

            // find first entrant on waitlist who is not already selected or declined
            for (Entrant e : waitlist) {
                boolean alreadySelected = (selected != null && selected.contains(e));
                boolean alreadyDeclined = (declined != null && declined.contains(e));

                if (!alreadySelected && !alreadyDeclined) {
                    // ensure selected list is initialized
                    if (selected != null) {
                        selected.add(e);
                    }

                    // persist updated event
                    eventRepository.updateEvent(event);
                    return e;
                }
            }

            // no replacement found
            return null;
        }
    }
}
