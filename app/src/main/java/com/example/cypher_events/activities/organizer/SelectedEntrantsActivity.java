package com.example.cypher_events.activities.organizer;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cypher_events.R;
import com.example.cypher_events.models.Entrant;
import com.example.cypher_events.services.FirestoreService;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity to view list of selected entrants who were invited to apply
 * Implements US 02.06.01 - View list of all chosen entrants
 *
 * Outstanding issues: Need to create EntrantAdapter
 */
public class SelectedEntrantsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewSelectedEntrants;
    private String eventId;
    private FirestoreService firestoreService;
    private List<Entrant> selectedEntrants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_entrants);

        // Get event ID from intent
        eventId = getIntent().getStringExtra("eventId");
        if (eventId == null) {
            Toast.makeText(this, "Error: No event ID provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        firestoreService = new FirestoreService();
        selectedEntrants = new ArrayList<>();

        initializeViews();
        loadSelectedEntrants();
    }

    private void initializeViews() {
        recyclerViewSelectedEntrants = findViewById(R.id.recyclerViewSelectedEntrants);
        recyclerViewSelectedEntrants.setLayoutManager(new LinearLayoutManager(this));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Selected Entrants");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Loads all entrants with "selected" status for this event
     */
    private void loadSelectedEntrants() {
        firestoreService.getEventEntrants(eventId, new FirestoreService.EntrantListCallback() {
            @Override
            public void onSuccess(List<Entrant> entrants) {
                selectedEntrants.clear();
                // Filter only selected entrants
                for (Entrant entrant : entrants) {
                    if ("selected".equals(entrant.getStatus())) {
                        selectedEntrants.add(entrant);
                    }
                }
                // TODO: Set up adapter and notify
                Toast.makeText(SelectedEntrantsActivity.this,
                        selectedEntrants.size() + " selected entrants",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(SelectedEntrantsActivity.this,
                        "Error loading entrants: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}