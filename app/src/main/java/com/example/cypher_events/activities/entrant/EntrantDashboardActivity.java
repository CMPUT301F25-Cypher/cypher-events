package com.example.cypher_events.activities.entrant;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cypher_events.R;
import com.example.cypher_events.activities.organizer.OrganizerDashboardActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Main dashboard for entrants (all regular users)
 * Provides access to browse events, manage waiting lists, and profile
 * Users can switch to organizer mode if they create events
 *
 * Outstanding issues: Need to implement fragments for browse, my events, profile
 */
public class EntrantDashboardActivity extends AppCompatActivity {

    private FloatingActionButton fabSwitchToOrganizer;
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrant_dashboard);

        deviceId = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        initializeViews();
    }

    private void initializeViews() {
        fabSwitchToOrganizer = findViewById(R.id.fabSwitchToOrganizer);

        // FAB to switch to organizer mode
        fabSwitchToOrganizer.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrganizerDashboardActivity.class);
            startActivity(intent);
        });
    }
}