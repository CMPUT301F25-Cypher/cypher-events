package com.example.cypher_events.domain.service;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cypher_events.domain.model.Notification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EntrantNotificationService {
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();

    public LiveData<List<Notification>> getNotificationsForEntrant(String entrantId) {
        MutableLiveData<List<Notification>> liveData = new MutableLiveData<>();
        database.getReference("notifications").orderByChild("entrantId").equalTo(entrantId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Notification> notifications = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Notification n = child.getValue(Notification.class);
                    if (n != null) {
                        notifications.add(n);
                    }
                }
                liveData.setValue(notifications);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                liveData.setValue(new ArrayList<>());
            }
        });
        return liveData;
    }
}
