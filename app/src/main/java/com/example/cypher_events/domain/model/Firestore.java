/**
 * Firestore.java
 *
 * Purpose:
 * Utility class providing simplified wrappers around FirebaseFirestore
 * for pushing and pulling documents to and from the database.
 *
 * Outstanding Issues:
 * - No error handling or callback support; all methods are fire-and-forget
 */

package com.example.cypher_events.domain.model;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class Firestore {
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();

    /**
     * Pushes an object to the specified Firestore collection and document ID.
     * @param obj_name Name of the Firestore collection.
     * @param obj_id Document ID to create or overwrite.
     * @param db Map containing object data.
     */
    public void push_DB(String obj_name, String obj_id, Map<String,Object> db) {
        database.collection(obj_name).document(obj_id).set(db);
    }

    /**
     * Retrieves a document snapshot from the specified collection and ID.
     * @param obj_name Name of the Firestore collection.
     * @param obj_id Document ID to fetch.
     * @return A Task representing the asynchronous read operation.
     */
    public Task<DocumentSnapshot> pull_db(String obj_name, String obj_id){
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        Task<DocumentSnapshot> db= database.collection(obj_name).document(obj_id).get();
        return db;
    }
}
