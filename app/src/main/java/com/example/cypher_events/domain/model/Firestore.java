package com.example.cypher_events.domain.model;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;


public class Firestore {

    private final FirebaseFirestore database;

    public Firestore() {
        this.database = FirebaseFirestore.getInstance();
    }

    public void push_DB(String collection, String documentId, Map<String, Object> data) {
        if (collection == null || documentId == null || data == null) return;
        database.collection(collection).document(documentId).set(data);
    }


    public Task<DocumentSnapshot> pull_db(String collection, String documentId) {
        if (collection == null || documentId == null) return null;
        return database.collection(collection).document(documentId).get();
    }

    public Task<QuerySnapshot> pullAll(String collection) {
        if (collection == null) return null;
        return database.collection(collection).get();
    }

    public Task<QuerySnapshot> pullFiltered(String collection, String fieldName, Object fieldValue) {
        if (collection == null || fieldName == null) return null;
        return database.collection(collection)
                .whereEqualTo(fieldName, fieldValue)
                .get();
    }


    public Task<Object> pullField(String collection, String documentId, String fieldName) {
        if (collection == null || documentId == null || fieldName == null) return null;

        return database.collection(collection)
                .document(documentId)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        return task.getResult().get(fieldName);
                    }
                    return null;
                });
    }
}
