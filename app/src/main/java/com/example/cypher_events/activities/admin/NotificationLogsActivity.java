package com.example.cypher_events.activities.admin;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cypher_events.R;
import com.example.cypher_events.models.NotificationLog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity for admins to review logs of all notifications sent
 * Implements US 03.08.01 - Review logs of notifications sent to entrants
 *
 * Outstanding issues: Need to create NotificationLogAdapter
 */
public class NotificationLogsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewLogs;
    private FirebaseFirestore db;
    private List<NotificationLog> logList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_logs);

        db = FirebaseFirestore.getInstance();
        logList = new ArrayList<>();

        initializeViews();
        loadNotificationLogs();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Notification Logs");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initializeViews() {
        recyclerViewLogs = findViewById(R.id.recyclerViewLogs);
        recyclerViewLogs.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Loads all notification logs from Firestore
     * Implements US 03.08.01
     */
    private void loadNotificationLogs() {
        db.collection("notificationLogs")
                .orderBy("sentDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    logList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        NotificationLog log = document.toObject(NotificationLog.class);
                        logList.add(log);
                    }
                    // TODO: Set up adapter and notify
                    Toast.makeText(this,
                            "Loaded " + logList.size() + " notification logs",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Error loading logs: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}