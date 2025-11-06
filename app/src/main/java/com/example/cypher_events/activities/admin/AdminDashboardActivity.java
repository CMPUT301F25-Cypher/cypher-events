package com.example.cypher_events.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cypher_events.R;

/**
 * Dashboard for admin users
 * Provides access to admin functions like browsing/removing content
 *
 * Outstanding issues: Need to implement all admin features
 */
public class AdminDashboardActivity extends AppCompatActivity {

    private Button btnBrowseEvents;
    private Button btnBrowseProfiles;
    private Button btnBrowseImages;
    private Button btnNotificationLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initializeViews();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin Dashboard");
        }
    }

    private void initializeViews() {
        btnBrowseEvents = findViewById(R.id.btnBrowseEvents);
        btnBrowseProfiles = findViewById(R.id.btnBrowseProfiles);
        btnBrowseImages = findViewById(R.id.btnBrowseImages);
        btnNotificationLogs = findViewById(R.id.btnNotificationLogs);

        btnBrowseEvents.setOnClickListener(v -> {
            // TODO: Implement browse events
        });

        btnBrowseProfiles.setOnClickListener(v -> {
            // TODO: Implement browse profiles
        });

        btnBrowseImages.setOnClickListener(v -> {
            // TODO: Implement browse images
        });

        btnNotificationLogs.setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationLogsActivity.class);
            startActivity(intent);
        });
    }
}
