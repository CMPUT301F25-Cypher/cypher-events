package com.example.cypher_events.domain.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.Collections;

/**
 * DummyData Seeder
 * Seeds one Entrant, one Organizer, and three Events (Open / Accepted / Declined)
 * into Firestore for testing and structure verification.
 */

public class DummyData {
    private static final String TAG = "DummyData";

    public static void seed() {
        Firestore firestore = new Firestore();


        // Create Entrant
        Entrant entrant = new Entrant("John Doe", "john@example.com", "5551234");
        entrant.setEntrant_latitude(53.5461);
        entrant.setEntrant_longitude(-113.4938);
        entrant.setEntrant_notificationsEnabled(true);
        entrant.setEntrant_status("DEVICE_ABC123"); // simulated device ID
        entrant.updateAdminStatus("DEVICE_ABC123"); // marks admin if matches
        entrant.setEntrant_joinedEvents(new ArrayList<>());
        entrant.setEntrant_acceptedEvents(new ArrayList<>());
        entrant.setEntrant_declinedEvents(new ArrayList<>());

        // Create Organizer (wrapping the Entrant)
        Organizer organizer = new Organizer(entrant);
        organizer.setOrganizer_notificationsEnabled(true);
        organizer.setOrganizer_removalReason(null);
        organizer.setOrganizer_createdEvents(new ArrayList<>());


        // Create Events (Open / Accepted / Declined)
        long now = System.currentTimeMillis();

        Event openEvent = new Event(
                "EVT001",
                "Community Coding Workshop",
                "A fun intro to Android development with live coding!",
                "UBC Makerspace",
                now,
                now + (7 * 24 * 60 * 60 * 1000L),
                30,
                organizer
        );
        openEvent.setEvent_status("Open");
        openEvent.setEvent_category("Workshop");
        openEvent.setEvent_joinedEntrants(Collections.singletonList(entrant));

        Event acceptedEvent = new Event(
                "EVT002",
                "AI Bootcamp 2025",
                "A two-day intensive workshop on practical AI and ML.",
                "UBC ICICS Building",
                now - (5 * 24 * 60 * 60 * 1000L),
                now + (2 * 24 * 60 * 60 * 1000L),
                40,
                organizer
        );
        acceptedEvent.setEvent_status("Accepted");
        acceptedEvent.setEvent_category("Bootcamp");
        acceptedEvent.setEvent_joinedEntrants(Collections.singletonList(entrant));

        Event declinedEvent = new Event(
                "EVT003",
                "Hackathon Night",
                "A 24-hour hackathon for rapid prototyping and fun.",
                "UBC Student Union Building",
                now + (10 * 24 * 60 * 60 * 1000L),
                now + (11 * 24 * 60 * 60 * 1000L),
                100,
                organizer
        );
        declinedEvent.setEvent_status("Declined");
        declinedEvent.setEvent_category("Hackathon");
        declinedEvent.setEvent_joinedEntrants(Collections.singletonList(entrant));

        Entrant waitlistedEntrant = new Entrant("Alice Waitlist", "alice@pending.com", "5559876");
        waitlistedEntrant.setEntrant_status("DEVICE_XYZ123");

        List<Entrant> waitlist = new ArrayList<>();
        waitlist.add(waitlistedEntrant);
        openEvent.setEvent_waitlistEntrants(waitlist);

        // Reflect relationships both ways
        List<Event> joinedEvents = entrant.getEntrant_joinedEvents();
        joinedEvents.add(openEvent);
        joinedEvents.add(acceptedEvent);
        joinedEvents.add(declinedEvent);
        entrant.setEntrant_joinedEvents(joinedEvents);

        List<Event> acceptedEvents = entrant.getEntrant_acceptedEvents();
        acceptedEvents.add(acceptedEvent);
        entrant.setEntrant_acceptedEvents(acceptedEvents);

        List<Event> declinedEvents = entrant.getEntrant_declinedEvents();
        declinedEvents.add(declinedEvent);
        entrant.setEntrant_declinedEvents(declinedEvents);

        List<Event> createdEvents = organizer.getOrganizer_createdEvents();
        createdEvents.add(openEvent);
        createdEvents.add(acceptedEvent);
        createdEvents.add(declinedEvent);
        organizer.setOrganizer_createdEvents(createdEvents);


        // Push to Firestore
        try {
            firestore.push_DB("Entrants", waitlistedEntrant.getEntrant_id(), waitlistedEntrant.toMap());
            firestore.push_DB("Entrants", entrant.getEntrant_id(), entrant.toMap());
            firestore.push_DB("Organizers", organizer.getOrganizer_id(), organizer.toMap());
            firestore.push_DB("Events", openEvent.getEvent_id(), openEvent.toMap());
            firestore.push_DB("Events", acceptedEvent.getEvent_id(), acceptedEvent.toMap());
            firestore.push_DB("Events", declinedEvent.getEvent_id(), declinedEvent.toMap());

            Log.d("DummyData", "Dummy Entrant, Organizer, and Events successfully seeded!");
        } catch (Exception e) {
            Log.e("DummyData", "Error pushing dummy data: ", e);
        }
    }


    public static void adminSeed(String key) {

        Firestore firestore = new Firestore();

        // Create Entrant
        Entrant entrant = new Entrant("Cypher", "cypher@yakuza.com", "666-6666");
        entrant.setEntrant_latitude(0.0); // null island
        entrant.setEntrant_longitude(0.0);
        entrant.setEntrant_notificationsEnabled(true);
        entrant.setEntrant_status("041f46c418140a17"); // mjoshi3 device ID
        entrant.updateAdminStatus(key); // false unless matches admin id

        // Create Admin
        Admin admin = new Admin(entrant);

        // Set optional / unique admin data
        admin.setAdmin_notificationsEnabled(true);
        admin.setAdmin_status("Active");
        admin.setAdmin_deletedEvents(new ArrayList<>());
        admin.setAdmin_deletedProfiles(new ArrayList<>());
        admin.setAdmin_deletedImages(new ArrayList<>());
        admin.setAdmin_visitedEvents(new ArrayList<>());
        admin.setAdmin_visitedProfiles(new ArrayList<>());
        admin.setAdmin_visitedImages(new ArrayList<>());
        admin.setAdmin_reviewedLogs(new ArrayList<>());

        // Push to Firestore
        try {
            firestore.push_DB("Admin", "CypherV1", admin.toMap());
            Log.d(TAG, "Cypher information has been uploaded.");
        } catch (Exception e) {
            Log.e(TAG, "Cypher information push unsuccessful.", e);
        }

    }

}
