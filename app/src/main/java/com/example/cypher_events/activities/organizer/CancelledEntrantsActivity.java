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
 * Activity to view list of cancelled entrants
 * Implements US 02.06.02 - See list of all cancelled entrants
 *
 * Outstanding issues: Need to create EntrantAdapter
 */
public class CancelledEntrantsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCancelledEntrants;
    private String eventId;
    private FirestoreService firestoreService;
    private List<Entrant> cancelledEntrants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelled_entrants);

        // Get event ID from intent
        eventId = getIntent().getStringExtra("eventId");
        if (eventId == null) {
            Toast.makeText(this, "Error: No event ID provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        firestoreService = new FirestoreService();
        cancelledEntrants = new ArrayList<>();

        initializeViews();
        loadCancelledEntrants();
    }

    private void initializeViews() {
        recyclerViewCancelledEntrants = findViewById(R.id.recyclerViewCancelledEntrants);
        recyclerViewCancelledEntrants.setLayoutManager(new LinearLayoutManager(this));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Cancelled Entrants");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Loads all entrants with "cancelled" status for this event
     */
    private void loadCancelledEntrants() {
        firestoreService.getEventEntrants(eventId, new FirestoreService.EntrantListCallback() {
            @Override
            public void onSuccess(List<Entrant> entrants) {
                cancelledEntrants.clear();
                // Filter only cancelled entrants
                for (Entrant entrant : entrants) {
                    if ("cancelled".equals(entrant.getStatus())) {
                        cancelledEntrants.add(entrant);
                    }
                }
                // TODO: Set up adapter and notify
                Toast.makeText(CancelledEntrantsActivity.this,
                        cancelledEntrants.size() + " cancelled entrants",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(CancelledEntrantsActivity.this,
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