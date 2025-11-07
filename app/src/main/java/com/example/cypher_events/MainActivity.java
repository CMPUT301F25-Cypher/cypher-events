package com.example.cypher_events;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.cypher_events.domain.model.DummyData;
import com.example.cypher_events.ui.entrant.EntrantDashboardFragment;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final boolean DEBUG_LOG_ADMIN_ID = false;
    private static final boolean DEBUG_FIRESTORE_DUMMY_DATA = false;
    private static final boolean CREATE_ADMIN = false;
    private static final String ADMIN_KEY = "041f46c418140a17"; // mjoshi3 device id
    private static final String ADMIN_PASSWORD = "9999";

    private FirebaseFirestore db;
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        if (DEBUG_FIRESTORE_DUMMY_DATA) DummyData.seed();
        if (CREATE_ADMIN) DummyData.adminSeed(ADMIN_KEY);
        logAdminDeviceId();

        checkEntrantOrCreate(deviceId);

        // Load Entrant dashboard by default
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new EntrantDashboardFragment())
                    .commit();
        }
    }


    public Task<DocumentSnapshot> pull_db(String collectionName, String documentId) {
        return db.collection(collectionName).document(documentId).get();
    }


    private void checkEntrantOrCreate(String deviceId) {
        pull_db("Entrants", deviceId)
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Log.d("FIREBASE", "Entrant already exists for device: " + deviceId);
                    } else {
                        createNewEntrant(deviceId);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error accessing Firestore: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }


    private void createNewEntrant(String deviceId) {
        Map<String, Object> entrantData = new HashMap<>();
        entrantData.put("Entrant_id", deviceId);
        entrantData.put("Entrant_name", "New User");
        entrantData.put("Entrant_email", "new_user_" + deviceId.substring(0, 6) + "@example.com");
        entrantData.put("Entrant_phone", "N/A");
        entrantData.put("Entrant_joinedEventIDs", new HashMap<>());
        entrantData.put("Entrant_acceptedEventIDs", new HashMap<>());
        entrantData.put("Entrant_declinedEventIDs", new HashMap<>());

        db.collection("Entrants").document(deviceId)
                .set(entrantData)
                .addOnSuccessListener(a ->
                        Toast.makeText(this, "New entrant profile created!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to create entrant: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void logAdminDeviceId() {
        if (!DEBUG_LOG_ADMIN_ID) return;

        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("ADMIN_DEVICE_ID", "Device ID: " + id);
        Toast.makeText(this, "Device ID: " + id, Toast.LENGTH_LONG).show();
    }
}
