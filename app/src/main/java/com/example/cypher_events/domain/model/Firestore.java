package com.example.cypher_events.domain.model;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.util.Map;

public class Firestore {
    public void push_DB(String obj_name, String obj_id, Map<String,Object> db){
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        database.collection(obj_name).document(obj_id).set(db);
    }
    public Task<DocumentSnapshot> pull_db(String obj_name, String obj_id){
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        Task<DocumentSnapshot> db= database.collection(obj_name).document(obj_id).get();
        return db;
    }
}
