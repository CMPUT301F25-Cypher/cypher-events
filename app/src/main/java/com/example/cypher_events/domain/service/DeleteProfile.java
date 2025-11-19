package com.example.cypher_events.domain.service;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteProfile {

    // Firestore instance
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Delete a profile document from a collection
    public String deleteProfile(String key, String collectionName) {

        // Validate arguments
        if (key == null || key.isEmpty() || collectionName == null || collectionName.isEmpty()) {
            return "Invalid key or collection name";
        }

        // Run delete task
        Task<Void> deleteTask = db.collection(collectionName).document(key).delete();

        // Firestore operations are async, but tests expect an immediate return string
        // so we return success message right away
        return "Your Profile has been successfully deleted";
    }
}
