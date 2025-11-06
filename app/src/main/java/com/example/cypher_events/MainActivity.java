package com.example.cypher_events;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cypher_events.activities.admin.AdminDashboardActivity;
import com.example.cypher_events.activities.entrant.EntrantDashboardActivity;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Main entry point of the application
 * Automatically routes users based on device ID and admin status
 * All users start as entrants; admins are identified by device ID
 *
 * Outstanding issues: None
 */
public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String deviceId;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        db = FirebaseFirestore.getInstance();

        // Get unique device ID (US 01.07.01 - identified by device)
        deviceId = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        // Check if this device is an admin
        checkAdminStatus();
    }

    /**
     * Checks if the current device ID is registered as an admin
     * If admin: go to AdminDashboard
     * If not: go to EntrantDashboard (default for all users)
     */
    private void checkAdminStatus() {
        db.collection("admins")
                .document(deviceId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Intent intent;
                    if (documentSnapshot.exists()) {
                        // This device is registered as an admin
                        intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
                    } else {
                        // Regular user - go to entrant dashboard
                        intent = new Intent(MainActivity.this, EntrantDashboardActivity.class);
                    }
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // On error, default to entrant dashboard
                    Intent intent = new Intent(MainActivity.this, EntrantDashboardActivity.class);
                    startActivity(intent);
                    finish();
                });
    }
}