package com.example.cypher_events.activities.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cypher_events.R;
import com.example.cypher_events.activities.entrant.EntrantDashboardActivity;
import com.example.cypher_events.models.Event;
import com.example.cypher_events.services.FirestoreService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

/**
 * Dashboard for users in organizer mode
 * Allows creating events, managing entrants, viewing lists
 * Users can switch back to entrant mode anytime
 *
 * Outstanding issues: Need to create EventAdapter
 */
public class OrganizerDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerViewEvents;
    private FloatingActionButton fabSwitchToEntrant;
    private FloatingActionButton fabCreateEvent;
    private String deviceId;
    private FirestoreService firestoreService;
    private List<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_dashboard);

        deviceId = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        firestoreService = new FirestoreService();
        eventList = new ArrayList<>();

        initializeViews();
        loadMyEvents();
    }

    private void initializeViews() {
        recyclerViewEvents = findViewById(R.id.recyclerViewEvents);
        fabSwitchToEntrant = findViewById(R.id.fabSwitchToEntrant);
        fabCreateEvent = findViewById(R.id.fabCreateEvent);

        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));

        // Switch back to entrant mode
        fabSwitchToEntrant.setOnClickListener(v -> {
            Intent intent = new Intent(this, EntrantDashboardActivity.class);
            startActivity(intent);
            finish();
        });

        // Create new event
        fabCreateEvent.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateEventActivity.class);
            startActivity(intent);
        });
    }

    private void loadMyEvents() {
        firestoreService.getOrganizerEvents(deviceId, new FirestoreService.EventListCallback() {
            @Override
            public void onSuccess(List<Event> events) {
                eventList.clear();
                eventList.addAll(events);
                // TODO: Notify adapter
                Toast.makeText(OrganizerDashboardActivity.this,
                        "Loaded " + events.size() + " events",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(OrganizerDashboardActivity.this,
                        "Error: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMyEvents(); // Refresh events when returning to this activity
    }
}