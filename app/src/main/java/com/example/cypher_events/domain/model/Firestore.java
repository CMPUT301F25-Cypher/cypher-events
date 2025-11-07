package com.example.cypher_events.domain.model;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.Map;

public class Firestore {

    private final FirebaseFirestore database;

    public Firestore() {
        this.database = FirebaseFirestore.getInstance();
    }

    public void push_DB(String obj_name, String obj_id, Map<String,Object> db){
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        database.collection(obj_name).document(obj_id).set(db);
    }
    public Task<DocumentSnapshot> pull_db(String obj_name, String obj_id){
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        Task<DocumentSnapshot> db= database.collection(obj_name).document(obj_id).get();
        return db;
    }

    public Task<QuerySnapshot> pullAll(String collectionName) {
        return database.collection(collectionName).get();
    }

    public Task<QuerySnapshot> pullFiltered(String collectionName, String fieldName, Object fieldValue) {
        return database.collection(collectionName)
                .whereEqualTo(fieldName, fieldValue)
                .get();
    }

    public Task<Object> pullField(String collectionName, String documentId, String fieldName) {
        return database.collection(collectionName).document(documentId)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot snapshot = task.getResult();
                        return snapshot.get(fieldName);
                    } else {
                        return null;
                    }
                }
                );}

}
