package com.example.cypher_events.ui.entrant;

import android.os.Bundle;
import android.provider.Settings;
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
import com.example.cypher_events.domain.service.EntrantNotificationService;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Displays the current entrant's notifications from Firestore.
 * Matches notifications by recipientEntrantId == this device's ANDROID_ID.
 */
public class NotificationFragment extends Fragment {

    private RecyclerView recycler;
    private NotificationAdapter adapter;
    private TextView emptyText;

    private final List<Notification> notifications = new ArrayList<>();
    private final EntrantNotificationService notificationService = new EntrantNotificationService();

    private String currentEntrantId;
    private ListenerRegistration registration;

    public NotificationFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notification, container, false);

        recycler = v.findViewById(R.id.recyclerNotifications);
        emptyText = v.findViewById(R.id.textEmptyState);

        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new NotificationAdapter(notifications);
        recycler.setAdapter(adapter);

        startListening();
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopListening();
    }

    private void startListening() {
        if (getActivity() != null && currentEntrantId == null) {
            currentEntrantId = Settings.Secure.getString(
                    getActivity().getContentResolver(),
                    Settings.Secure.ANDROID_ID
            );
        }

        if (currentEntrantId == null) {
            emptyText.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
            return;
        }

        stopListening(); // Safety: avoid multiple registrations

        registration = notificationService.listenForEntrantNotifications(
                currentEntrantId,
                (QuerySnapshot snap, com.google.firebase.firestore.FirebaseFirestoreException err) -> {
                    if (err != null) {
                        err.printStackTrace();
                        return;
                    }
                    notifications.clear();
                    if (snap != null) {
                        for (var doc : snap.getDocuments()) {
                            Notification n = doc.toObject(Notification.class);
                            if (n != null) {
                                notifications.add(n);
                            }
                        }
                        // Sort by timestamp descending (latest first)
                        Collections.sort(notifications,
                                (a, b) -> Long.compare(b.getTimestampUtc(), a.getTimestampUtc()));
                    }

                    adapter.notifyDataSetChanged();
                    boolean isEmpty = notifications.isEmpty();
                    emptyText.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                    recycler.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
                }
        );
    }

    private void stopListening() {
        if (registration != null) {
            registration.remove();
            registration = null;
        }
    }
}
