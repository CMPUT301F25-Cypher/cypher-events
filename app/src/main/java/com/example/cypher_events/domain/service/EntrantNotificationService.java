package com.example.cypher_events.domain.service;

import android.util.Log;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Firestore-based service to listen for notifications for a given entrant.
 * Uses the "notifications" collection and field "recipientEntrantId".
 */
public class EntrantNotificationService {

    private static final String TAG = "EntrantNotificationSvc";

    private final FirebaseFirestore db;

    public EntrantNotificationService() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Start listening for notifications for a given entrantId (deviceId).
     *
     * @param entrantId Entrant_id / deviceId (same as Firestore Entrants doc id)
     * @param listener  Firestore EventListener<QuerySnapshot> that will receive updates
     * @return ListenerRegistration so you can remove the listener later
     */
    public ListenerRegistration listenForEntrantNotifications(String entrantId,
                                                              EventListener<QuerySnapshot> listener) {
        if (entrantId == null) {
            Log.w(TAG, "listenForEntrantNotifications: entrantId is null, aborting");
            return null;
        }

        Log.d(TAG, "Listening for notifications where recipientEntrantId = " + entrantId);

        // No orderBy here to avoid composite index headaches; we sort client-side.
        Query query = db.collection("notifications")
                .whereEqualTo("recipientEntrantId", entrantId);

        return query.addSnapshotListener(listener);
    }
}
