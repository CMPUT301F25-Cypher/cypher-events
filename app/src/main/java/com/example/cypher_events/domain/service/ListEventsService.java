package com.example.cypher_events.domain.service;

import com.example.cypher_events.domain.model.Event;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to fetch all currently open events from Firestore.
 * Used for displaying a list of events entrants can join.
 */
public class ListEventsService {

    private final FirebaseFirestore db;

    public ListEventsService() {
        db = FirebaseFirestore.getInstance();
    }

    /** Callback interface for async results */
    public interface EventListCallback {
        void onSuccess(List<Event> events);
        void onFailure(Exception e);
    }

    /**
     * Fetches all active/open events from Firestore.
     * Example filter: event_status == "Open"
     */
    public void fetchOpenEvents(EventListCallback callback) {
        db.collection("events")
                .whereEqualTo("Event_status", "Open")
                .get()
                .addOnSuccessListener(query -> {
                    List<Event> events = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : query) {
                        Event event = doc.toObject(Event.class);
                        if (event.getEvent_id() == null)
                            event.setEvent_id(doc.getId());
                        events.add(event);
                    }
                    callback.onSuccess(events);
                })
                .addOnFailureListener(callback::onFailure);
    }
}
