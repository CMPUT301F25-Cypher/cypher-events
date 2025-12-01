package com.example.cypher_events;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.cypher_events.domain.model.DummyData;
import com.example.cypher_events.ui.auth.SignupFragment;
import com.example.cypher_events.ui.entrant.HomeContainerFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String deviceId;

    private boolean shouldOpenNotifications = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        deviceId = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        // Detect notification click BEFORE loading fragments
        shouldOpenNotifications =
                getIntent() != null &&
                        getIntent().getBooleanExtra("open_notifications", false);

        checkEntrantStatus(deviceId);
    }

    /** FIREBASE WRAPPER */
    public Task<DocumentSnapshot> pull_db(String collectionName, String documentId) {
        return db.collection(collectionName).document(documentId).get();
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
        HomeContainerFragment home = new HomeContainerFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, home)
                .commit();

        // After home is attached, set tab if needed
        new Handler().postDelayed(() -> {
            if (shouldOpenNotifications) {
                forceOpenNotifications(home);
            }
        }, 120);
    }

    /** FORCE THE HOME FRAGMENT TO SWITCH TO NOTIFICATIONS TAB */
    private void forceOpenNotifications(HomeContainerFragment home) {
        if (home == null || home.getView() == null) return;

        BottomNavigationView nav = home.getView().findViewById(R.id.bottomNav);
        if (nav != null) {
            nav.setSelectedItemId(R.id.nav_notifications);
        }
    }

    /** CHECK IF ENTRANT EXISTS OR SHOW SIGNUP SCREEN */
    private void checkEntrantStatus(String deviceId) {
        db.collection("Entrants").document(deviceId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        loadHome();
                    } else {
                        loadSignup();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Firestore error: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }
}
