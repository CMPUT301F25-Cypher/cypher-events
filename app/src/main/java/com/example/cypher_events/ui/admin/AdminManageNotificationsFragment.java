package com.example.cypher_events.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cypher_events.R;
import com.example.cypher_events.domain.model.NotificationLog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminManageNotificationsFragment extends Fragment {

    private FirebaseFirestore db;
    private ListView lvLogs;
    private ArrayAdapter<String> adapter;
    private final List<NotificationLog> allLogs = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_manage_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v ->
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, new AdminDashboardFragment())
                            .commit()
            );
        }

        lvLogs = view.findViewById(R.id.lvNotificationLogs);
        adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                new ArrayList<>()
        );
        lvLogs.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadAllNotificationLogs();
    }

    private void loadAllNotificationLogs() {
        db.collection("Organizers")
                .get()
                .addOnSuccessListener(snaps -> {
                    allLogs.clear();
                    for (QueryDocumentSnapshot doc : snaps) {
                        Object val = doc.get("Organizer_sentNotifications");
                        if (val instanceof List) {
                            // list of maps
                            List<?> list = (List<?>) val;
                            for (Object o : list) {
                                if (o instanceof Map) {
                                    @SuppressWarnings("unchecked")
                                    Map<String, Object> m = (Map<String, Object>) o;
                                    NotificationLog n = NotificationLog.fromMap(m);
                                    if (n != null) {
                                        allLogs.add(n);
                                    }
                                }
                            }
                        }
                    }
                    refreshList();
                    Toast.makeText(requireContext(), "Loaded " + allLogs.size() + " notifications", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to load notification logs: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void refreshList() {
        List<String> labels = new ArrayList<>();
        for (NotificationLog n : allLogs) {
            String ts = n.getTimestampUtc() > 0 ? String.valueOf(n.getTimestampUtc()) : "";
            labels.add((n.getRecipientEmail() != null ? n.getRecipientEmail() : "(no-recipient)") +
                    " — " + (n.getMessage() != null ? n.getMessage() : "(no-message)") +
                    (n.getEventId() != null ? " [" + n.getEventId() + "]" : "") +
                    (ts.isEmpty() ? "" : " @" + ts));
        }
        adapter.clear();
        adapter.addAll(labels);
        adapter.notifyDataSetChanged();
    }
}