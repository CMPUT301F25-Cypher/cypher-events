package com.example.cypher_events.activities.organizer;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cypher_events.R;
import com.example.cypher_events.models.Entrant;
import com.example.cypher_events.services.FirestoreService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;

/**
 * Activity to display map showing where entrants joined the waiting list
 * Implements US 02.02.02 - View map of entrant join locations
 *
 * Outstanding issues: Need to add clustering for many markers, info windows with user details
 */
public class EntrantsMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private String eventId;
    private FirestoreService firestoreService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrant_map);

        // Get event ID from intent
        eventId = getIntent().getStringExtra("eventId");
        if (eventId == null) {
            Toast.makeText(this, "Error: No event ID provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        firestoreService = new FirestoreService();

        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Entrant Locations");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;
        loadEntrantLocations();
    }

    /**
     * Loads all entrants and displays their join locations on the map
     * Implements US 02.02.02
     */
    private void loadEntrantLocations() {
        firestoreService.getEventEntrants(eventId, new FirestoreService.EntrantListCallback() {
            @Override
            public void onSuccess(List<Entrant> entrants) {
                int markerCount = 0;
                LatLng firstLocation = null;

                for (Entrant entrant : entrants) {
                    if (entrant.getJoinLocation() != null) {
                        double lat = entrant.getJoinLocation().getLatitude();
                        double lng = entrant.getJoinLocation().getLongitude();
                        LatLng location = new LatLng(lat, lng);

                        // Add marker for this entrant
                        String title = "Entrant: " + entrant.getUserId();
                        String snippet = entrant.getJoinLocationAddress() != null ?
                                entrant.getJoinLocationAddress() : "Unknown location";

                        googleMap.addMarker(new MarkerOptions()
                                .position(location)
                                .title(title)
                                .snippet(snippet));

                        if (firstLocation == null) {
                            firstLocation = location;
                        }
                        markerCount++;
                    }
                }

                // Move camera to first marker location
                if (firstLocation != null) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 10));
                }

                Toast.makeText(EntrantsMapActivity.this,
                        "Showing " + markerCount + " entrant locations",
                        Toast.LENGTH_SHORT).show();

                if (markerCount == 0) {
                    Toast.makeText(EntrantsMapActivity.this,
                            "No location data available for this event",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(EntrantsMapActivity.this,
                        "Error loading locations: " + error,
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