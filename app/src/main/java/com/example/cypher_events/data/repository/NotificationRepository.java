package com.example.cypher_events.data.repository;

import androidx.annotation.NonNull;

import com.example.cypher_events.domain.model.Notification;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class NotificationRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void addNotification(String entrantId, Notification notification) {
        db.collection("entrants")
                .document(entrantId)
                .collection("notifications")
                .add(notification);
    }

    public void getNotifications(String entrantId, Consumer<List<Notification>> onResult) {
        db.collection("entrants")
                .document(entrantId)
                .collection("notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Notification> notifications = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Notification n = doc.toObject(Notification.class);
                        n.setId(doc.getId());
                        notifications.add(n);
                    }
                    onResult.accept(notifications);
                })
                .addOnFailureListener(e -> onResult.accept(new ArrayList<>()));
    }
}

