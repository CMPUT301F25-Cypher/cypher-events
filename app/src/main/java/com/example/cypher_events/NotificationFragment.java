package com.example.cypher_events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cypher_events.R;
import com.example.cypher_events.domain.model.Notification;
import com.example.cypher_events.ui.entrant.NotificationAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private final List<Notification> notificationList = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        recyclerView = view.findViewById(R.id.recyclerNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(adapter);

        loadNotifications();
        return view;
    }

    private void loadNotifications() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users")
                .document(userId)
                .collection("notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    notificationList.clear();
                    if (value != null) {
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Notification n = doc.toObject(Notification.class);
                            if (n != null) notificationList.add(n);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
