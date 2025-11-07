package com.example.cypher_events;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cypher_events.R;
import com.example.cypher_events.domain.model.Notification;
import com.example.cypher_events.domain.model.NotificationItem;
import com.example.cypher_events.ui.entrant.NotificationAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        recyclerView = view.findViewById(R.id.recyclerNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter();
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadNotifications();
        return view;
    }

    private void loadNotifications() {
        String entrantId = "CURRENT_ENTRANT_ID"; // Replace with actual logged-in entrant ID

        db.collection("entrants")
                .document(entrantId)
                .collection("notifications")
                .orderBy("timestamp")
                .addSnapshotListener((QuerySnapshot snapshots, FirebaseFirestore e) -> {
                    if (e != null) return;

                    notificationList.clear();
                    if (snapshots != null) {
                        for (DocumentChange docChange : snapshots.getDocumentChanges()) {
                            Notification notification = docChange.getDocument().toObject(Notification.class);
                            notificationList.add(notification);
                        }
                        adapter.updateList(notificationList);
                    }
                });
    }
}
