package com.example.cypher_events.services;

import com.example.cypher_events.models.Event;
import com.example.cypher_events.models.Entrant;
import com.example.cypher_events.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for common Firestore operations
 * Provides centralized database access methods
 *
 * Outstanding issues: Need to add error handling for network failures
 */
public class FirestoreService {

    private FirebaseFirestore db;

    public FirestoreService() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Saves an event to Firestore
     */
    public void saveEvent(Event event, SaveCallback callback) {
        String eventId = db.collection("events").document().getId();
        event.setEventId(eventId);

        db.collection("events").document(eventId)
                .set(event)
                .addOnSuccessListener(aVoid -> callback.onSuccess(eventId))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    /**
     * Gets events created by a specific organizer
     */
    public void getOrganizerEvents(String organizerId, EventListCallback callback) {
        db.collection("events")
                .whereEqualTo("organizerId", organizerId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Event> events = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Event event = document.toObject(Event.class);
                        events.add(event);
                    }
                    callback.onSuccess(events);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    /**
     * Gets all entrants for a specific event
     */
    public void getEventEntrants(String eventId, EntrantListCallback callback) {
        db.collection("entrants")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Entrant> entrants = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Entrant entrant = document.toObject(Entrant.class);
                        entrants.add(entrant);
                    }
                    callback.onSuccess(entrants);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    /**
     * Gets a single event by ID
     */
    public void getEvent(String eventId, EventCallback callback) {
        db.collection("events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Event event = documentSnapshot.toObject(Event.class);
                    if (event != null) {
                        callback.onSuccess(event);
                    } else {
                        callback.onFailure("Event not found");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    /**
     * Saves or updates a user profile
     */
    public void saveUser(User user, SaveCallback callback) {
        db.collection("users").document(user.getUserId())
                .set(user)
                .addOnSuccessListener(aVoid -> callback.onSuccess(user.getUserId()))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Callback interfaces
    public interface SaveCallback {
        void onSuccess(String documentId);
        void onFailure(String error);
    }

    public interface EventCallback {
        void onSuccess(Event event);
        void onFailure(String error);
    }

    public interface EventListCallback {
        void onSuccess(List<Event> events);
        void onFailure(String error);
    }

    public interface EntrantListCallback {
        void onSuccess(List<Entrant> entrants);
        void onFailure(String error);
    }
}