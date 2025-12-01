package com.example.cypher_events;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.cypher_events.domain.model.DummyData;
import com.example.cypher_events.ui.auth.SignupFragment;
import com.example.cypher_events.ui.entrant.HomeContainerFragment;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private static final boolean DEBUG_LOG_ADMIN_ID         = false;
    private static final boolean DEBUG_FIRESTORE_DUMMY_DATA = false;
    private static final boolean CREATE_ADMIN               = false;

    private static final String ADMIN_KEY = "041f46c418140a17";
    private static final String ADMIN_PASSWORD = "9999";

    private FirebaseFirestore db;
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        deviceId = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        // optional dummy data
        if (DEBUG_FIRESTORE_DUMMY_DATA) DummyData.seed();
        if (CREATE_ADMIN) DummyData.adminSeed(ADMIN_KEY);
        logAdminDeviceId();

        // IMPORTANT: do NOT auto-load HomeContainerFragment here
        // First check if user exists
        checkEntrantStatus(deviceId);
    }

    /** FIREBASE WRAPPER */
    public Task<DocumentSnapshot> pull_db(String collectionName, String documentId) {
        return db.collection(collectionName).document(documentId).get();
    }

    public void openNotificationsTab() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new HomeContainerFragment())
                .commit();

        // Delay is needed so BottomNav exists
        new android.os.Handler().postDelayed(() -> {
            HomeContainerFragment f =
                    (HomeContainerFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.container);

            if (f != null && f.getView() != null) {
                BottomNavigationView nav = f.getView().findViewById(R.id.bottomNav);
                if (nav != null) nav.setSelectedItemId(R.id.nav_notifications);
            }
        }, 50);
    }


    /** CHECK IF ENTRANT EXISTS OR SHOW SIGNUP SCREEN */
    private void checkEntrantStatus(String deviceId) {
        db.collection("Entrants").document(deviceId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        // existing user → go to home
                        loadHome();
                    } else {
                        // first-time user → open signup screen
                        loadSignup();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(
                            this,
                            "Firestore error: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    /** LOAD SIGNUP FRAGMENT (FIRST TIME USERS) */
    private void loadSignup() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new SignupFragment())
                .commit();
    }

    /** LOAD HOME FRAGMENT (RETURNING USERS) */
    private void loadHome() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new HomeContainerFragment())
                .commit();
    }

    /** DEBUG: PRINT DEVICE ID IF ENABLED */
    private void logAdminDeviceId() {
        if (!DEBUG_LOG_ADMIN_ID) return;

        String id = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        Log.d("ADMIN_DEVICE_ID", "Device ID: " + id);
        Toast.makeText(this, "Device ID: " + id, Toast.LENGTH_LONG).show();
    }
}
