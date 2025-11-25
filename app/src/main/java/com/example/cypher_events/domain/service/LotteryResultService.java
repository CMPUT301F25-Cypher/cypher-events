package com.example.cypher_events.domain.service;

import com.example.cypher_events.data.repository.EventRepository;
import com.example.cypher_events.domain.model.Entrant;
import com.example.cypher_events.domain.model.Event;
import com.example.cypher_events.util.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Run full lottery: draw winners via LotteryService, update Event, and notify winners & not-chosen.
 */
public class LotteryResultService {

    private final EventRepository eventRepository;
    private final LotteryService lotteryService;
    private final NotifyWinnerService notifyWinnerService = new NotifyWinnerService();
    private final NotifyNotChosenService notifyNotChosenService = new NotifyNotChosenService();

    public LotteryResultService(EventRepository eventRepository, LotteryService lotteryService) {
        this.eventRepository = eventRepository;
        this.lotteryService = lotteryService;
    }

    /**
     * Draw winners for event and notify both winners and not-chosen entrants.
     * @param eventId event identifier
     * @param winnersCount number to choose
     * @param seed optional seed
     * @param organizerId id of organizer performing action
     * @return list of winners (Entrant objects)
     */
    public List<Entrant> runLotteryAndNotify(String eventId, int winnersCount, Long seed, String organizerId) {
        Result<Event> r = eventRepository.getEventById(eventId);
        if (r == null || !r.isOk() || r.getData() == null) return new ArrayList<>();

        Event event = r.getData();
        List<Entrant> waitlist = event.getEvent_waitlistEntrants();
        if (waitlist == null || waitlist.isEmpty()) return new ArrayList<>();

        // Exclude organizer if present (compare by email if organizer embedded)
        String organizerEmail = null;
        if (event.getEvent_organizer() != null && event.getEvent_organizer().getOrganizer() != null) {
            organizerEmail = event.getEvent_organizer().getOrganizer().getEntrant_email();
        }

        List<String> candidateEmails = new ArrayList<>();
        List<Entrant> candidates = new ArrayList<>();
        for (Entrant e : waitlist) {
            if (organizerEmail != null && organizerEmail.equalsIgnoreCase(e.getEntrant_email())) continue;
            candidateEmails.add(e.getEntrant_email());
            candidates.add(e);
        }

        // draw
        List<String> winnerEmails = lotteryService.draw(candidateEmails, winnersCount, seed);

        List<Entrant> winners = new ArrayList<>();
        List<Entrant> notChosen = new ArrayList<>();
        for (Entrant e : candidates) {
            if (winnerEmails.contains(e.getEntrant_email())) winners.add(e);
            else notChosen.add(e);
        }

        // persist winners to event
        for (Entrant w : winners) {
            event.addSelectedEntrant(w);
        }
        eventRepository.updateEvent(event);

        // notify
        notifyWinnerService.sendWinningNotifications(winners, eventId, organizerId);
        notifyNotChosenService.sendNotChosenNotifications(notChosen, eventId, organizerId);

        return winners;
    }
}
