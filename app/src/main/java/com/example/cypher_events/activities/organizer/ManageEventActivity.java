package com.example.cypher_events.activities.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cypher_events.R;
import com.example.cypher_events.models.Event;
import com.example.cypher_events.services.FirestoreService;
import com.example.cypher_events.utils.Constants;

/**
 * Activity for managing a single event
 * Provides access to all organizer features for this event
 * Navigation hub for YOUR implemented user stories
 */
public class ManageEventActivity extends AppCompatActivity {

    private TextView textViewEventName;
    private TextView textViewEventDetails;
    private TextView textViewCapacityInfo;
    private Button btnViewSelectedEntrants;
    private Button btnViewCancelledEntrants;
    private Button btnDrawReplacement;
    private Button btnViewMap;
    private Button btnEditEvent;

    private String eventId;
    private Event event;
    private FirestoreService firestoreService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_event);

        eventId = getIntent().getStringExtra(Constants.EXTRA_EVENT_ID);
        if (eventId == null) {
            Toast.makeText(this, "Error: No event ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        firestoreService = new FirestoreService();

        initializeViews();
        loadEventDetails();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Manage Event");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initializeViews() {
        textViewEventName = findViewById(R.id.textViewEventName);
        textViewEventDetails = findViewById(R.id.textViewEventDetails);
        textViewCapacityInfo = findViewById(R.id.textViewCapacityInfo);
        btnViewSelectedEntrants = findViewById(R.id.btnViewSelectedEntrants);
        btnViewCancelledEntrants = findViewById(R.id.btnViewCancelledEntrants);
        btnDrawReplacement = findViewById(R.id.btnDrawReplacement);
        btnViewMap = findViewById(R.id.btnViewMap);
        btnEditEvent = findViewById(R.id.btnEditEvent);

        // US 02.06.01 - View selected entrants
        btnViewSelectedEntrants.setOnClickListener(v -> {
            Intent intent = new Intent(this, SelectedEntrantsActivity.class);
            intent.putExtra(Constants.EXTRA_EVENT_ID, eventId);
            startActivity(intent);
        });

        // US 02.06.02 - View cancelled entrants
        btnViewCancelledEntrants.setOnClickListener(v -> {
            Intent intent = new Intent(this, CancelledEntrantsActivity.class);
            intent.putExtra(Constants.EXTRA_EVENT_ID, eventId);
            startActivity(intent);
        });

        // US 02.06.03 - Draw replacement
        btnDrawReplacement.setOnClickListener(v -> {
            Intent intent = new Intent(this, DrawReplacementActivity.class);
            intent.putExtra(Constants.EXTRA_EVENT_ID, eventId);
            intent.putExtra(Constants.EXTRA_EVENT_NAME, event != null ? event.getEventName() : "");
            startActivity(intent);
        });

        // US 02.02.02 - View entrants map
        btnViewMap.setOnClickListener(v -> {
            if (event != null && event.isGeolocationRequired()) {
                Intent intent = new Intent(this, EntrantsMapActivity.class);
                intent.putExtra(Constants.EXTRA_EVENT_ID, eventId);
                startActivity(intent);
            } else {
                Toast.makeText(this,
                        "Geolocation is not enabled for this event",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnEditEvent.setOnClickListener(v -> {
            Toast.makeText(this, "Edit event - To be implemented", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadEventDetails() {
        firestoreService.getEvent(eventId, new FirestoreService.EventCallback() {
            @Override
            public void onSuccess(Event loadedEvent) {
                event = loadedEvent;
                displayEventDetails();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(ManageEventActivity.this,
                        "Error loading event: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayEventDetails() {
        textViewEventName.setText(event.getEventName());

        String details = "Location: " + (event.getLocation() != null ? event.getLocation() : "N/A") + "\n" +
                "Price: $" + event.getPrice() + "\n" +
                "Geolocation: " + (event.isGeolocationRequired() ? "Required" : "Not required");
        textViewEventDetails.setText(details);

        // US 02.03.01 - Display capacity info
        String capacityInfo = "Waiting List: " + event.getCapacityDisplay() + "\n" +
                "Selected: " + event.getSelectedCount() + "\n" +
                "Enrolled: " + event.getEnrolledCount() + "\n" +
                "Cancelled: " + event.getCancelledCount();
        textViewCapacityInfo.setText(capacityInfo);

        // Enable/disable map button based on geolocation setting
        btnViewMap.setEnabled(event.isGeolocationRequired());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEventDetails(); // Refresh data when returning to this activity
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}