package com.example.cypher_events.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cypher_events.R;
import com.example.cypher_events.domain.model.Notification;
import com.example.cypher_events.ui.entrant.NotificationAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Displays the current entrant's notifications.
 * You must call setCurrentEntrantId(...) before showing fragment or set it from session.
 */
public class NotificationFragment extends Fragment {

    private RecyclerView recycler;
    private NotificationAdapter adapter;
    private TextView emptyText;
    private final List<Notification> notifications = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String currentEntrantId;

    public NotificationFragment() {}

    public void setCurrentEntrantId(String entrantId) {
        this.currentEntrantId = entrantId;
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notification, container, false);
        recycler = v.findViewById(R.id.recyclerNotifications);
        emptyText = v.findViewById(R.id.textEmptyState);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new NotificationAdapter(notifications);
        recycler.setAdapter(adapter);

        loadNotifications();
        return v;
    }

    private void loadNotifications() {
        if (currentEntrantId == null) {
            // attempt to read from a session or Entrant service — for now bail
            emptyText.setVisibility(View.VISIBLE);
            return;
        }

        // query notifications where recipientEntrantId == currentEntrantId
        db.collection("notifications")
                .whereEqualTo("recipientEntrantId", currentEntrantId)
                .orderBy("timestampUtc", Query.Direction.DESCENDING)
                .addSnapshotListener((snap, err) -> {
                    if (err != null) {
                        err.printStackTrace();
                        return;
                    }
                    notifications.clear();
                    if (snap != null) {
                        for (var doc : snap.getDocuments()) {
                            Notification n = doc.toObject(Notification.class);
                            if (n != null) notifications.add(n);
                        }
                        Collections.sort(notifications, (a,b) -> Long.compare(b.getTimestampUtc(), a.getTimestampUtc()));
                    }
                    adapter.notifyDataSetChanged();
                    emptyText.setVisibility(notifications.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }
}
