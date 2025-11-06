package com.example.cypher_events.activities.organizer;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cypher_events.R;
import com.example.cypher_events.services.LotteryService;

/**
 * Activity for drawing replacement entrants when someone cancels/declines
 * Implements US 02.06.03 - Draw replacement from pooling system
 *
 * Outstanding issues: Need to display user details of selected replacement
 */
public class DrawReplacementActivity extends AppCompatActivity {

    private TextView textViewEventName;
    private TextView textViewAvailablePool;
    private Button btnDrawReplacement;
    private String eventId;
    private LotteryService lotteryService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_replacement);

        // Get event ID and name from intent
        eventId = getIntent().getStringExtra("eventId");
        String eventName = getIntent().getStringExtra("eventName");

        if (eventId == null) {
            Toast.makeText(this, "Error: No event ID provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        lotteryService = new LotteryService();

        initializeViews();
        textViewEventName.setText(eventName != null ? eventName : "Unknown Event");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Draw Replacement");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initializeViews() {
        textViewEventName = findViewById(R.id.textViewEventName);
        textViewAvailablePool = findViewById(R.id.textViewAvailablePool);
        btnDrawReplacement = findViewById(R.id.btnDrawReplacement);

        btnDrawReplacement.setOnClickListener(v -> showConfirmationDialog());
    }

    /**
     * Shows confirmation dialog before drawing replacement
     */
    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Draw Replacement")
                .setMessage("Are you sure you want to draw a replacement entrant from the waiting list?")
                .setPositiveButton("Draw", (dialog, which) -> drawReplacement())
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Draws a replacement entrant using LotteryService
     * Implements US 02.06.03
     */
    private void drawReplacement() {
        btnDrawReplacement.setEnabled(false);
        btnDrawReplacement.setText("Drawing...");

        lotteryService.drawReplacement(eventId, new LotteryService.ReplacementCallback() {
            @Override
            public void onSuccess(String replacementEntrantId) {
                btnDrawReplacement.setEnabled(true);
                btnDrawReplacement.setText("Draw Another Replacement");

                new AlertDialog.Builder(DrawReplacementActivity.this)
                        .setTitle("Replacement Selected!")
                        .setMessage("A replacement entrant has been selected from the waiting list.\n\n" +
                                "Entrant ID: " + replacementEntrantId + "\n\n" +
                                "They will be notified of their selection.")
                        .setPositiveButton("OK", null)
                        .show();
            }

            @Override
            public void onFailure(String error) {
                btnDrawReplacement.setEnabled(true);
                btnDrawReplacement.setText("Draw Replacement");

                Toast.makeText(DrawReplacementActivity.this,
                        "Error: " + error,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}