package com.example.cypher_events.activities.organizer;

import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cypher_events.R;
import com.example.cypher_events.models.Event;
import com.example.cypher_events.services.FirestoreService;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Date;

/**
 * Activity for creating a new event
 * Implements US 02.03.01 - Limit number of entrants
 * Implements US 02.02.03 - Enable/disable geolocation requirement
 *
 * Outstanding issues: Need to add date pickers, image upload, QR code generation
 */
public class CreateEventActivity extends AppCompatActivity {

    private TextInputEditText editEventName;
    private TextInputEditText editDescription;
    private TextInputEditText editLocation;
    private TextInputEditText editPrice;
    private TextInputEditText editCapacity;
    private TextInputEditText editMaxEntrants;
    private CheckBox checkBoxUnlimitedEntrants;
    private CheckBox checkBoxGeolocationRequired;
    private Button btnCreateEvent;

    private FirestoreService firestoreService;
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        deviceId = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        firestoreService = new FirestoreService();

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        editEventName = findViewById(R.id.editEventName);
        editDescription = findViewById(R.id.editDescription);
        editLocation = findViewById(R.id.editLocation);
        editPrice = findViewById(R.id.editPrice);
        editCapacity = findViewById(R.id.editCapacity);
        editMaxEntrants = findViewById(R.id.editMaxEntrants);
        checkBoxUnlimitedEntrants = findViewById(R.id.checkBoxUnlimitedEntrants);
        checkBoxGeolocationRequired = findViewById(R.id.checkBoxGeolocationRequired);
        btnCreateEvent = findViewById(R.id.btnCreateEvent);
    }

    private void setupListeners() {
        // US 02.03.01 - Disable max entrants input when unlimited is checked
        checkBoxUnlimitedEntrants.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editMaxEntrants.setEnabled(!isChecked);
            if (isChecked) {
                editMaxEntrants.setText("");
            }
        });

        btnCreateEvent.setOnClickListener(v -> createEvent());
    }

    /**
     * Creates and saves the event to Firestore
     * Implements US 02.03.01 and US 02.02.03
     */
    private void createEvent() {
        // Validate inputs
        String eventName = editEventName.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String location = editLocation.getText().toString().trim();

        if (eventName.isEmpty()) {
            Toast.makeText(this, "Please enter event name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (description.isEmpty()) {
            Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create event object
        Event event = new Event(eventName, description, deviceId);
        event.setLocation(location);

        // Set price
        String priceStr = editPrice.getText().toString().trim();
        if (!priceStr.isEmpty()) {
            try {
                double price = Double.parseDouble(priceStr);
                event.setPrice(price);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Set capacity
        String capacityStr = editCapacity.getText().toString().trim();
        if (!capacityStr.isEmpty()) {
            try {
                int capacity = Integer.parseInt(capacityStr);
                event.setCapacity(capacity);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid capacity format", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // US 02.03.01 - Set max entrants (can be null for unlimited)
        if (!checkBoxUnlimitedEntrants.isChecked()) {
            String maxEntrantsStr = editMaxEntrants.getText().toString().trim();
            if (!maxEntrantsStr.isEmpty()) {
                try {
                    int maxEntrants = Integer.parseInt(maxEntrantsStr);
                    if (maxEntrants > 0) {
                        event.setMaxEntrants(maxEntrants);
                    } else {
                        Toast.makeText(this, "Max entrants must be greater than 0",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid max entrants format", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        // US 02.02.03 - Set geolocation requirement
        event.setGeolocationRequired(checkBoxGeolocationRequired.isChecked());

        // Set dates (using current date for now - should add date pickers)
        event.setRegistrationStartDate(new Date());
        event.setEventDate(new Date());

        // Save to Firestore
        firestoreService.saveEvent(event, new FirestoreService.SaveCallback() {
            @Override
            public void onSuccess(String documentId) {
                Toast.makeText(CreateEventActivity.this,
                        "Event created successfully!",
                        Toast.LENGTH_SHORT).show();
                finish(); // Go back to organizer dashboard
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(CreateEventActivity.this,
                        "Error creating event: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}