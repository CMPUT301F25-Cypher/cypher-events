package com.example.cypher_events.domain.service;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteProfile {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public String DeleteProfile(String key,String collection_name){
        String Done="Your Profile has been successfully deleted";
        Task<Void> deleteTask=db.collection(collection_name).document(key).delete();
        return Done;
    }
}
