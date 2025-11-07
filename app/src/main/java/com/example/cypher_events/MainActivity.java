package com.example.cypher_events;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.cypher_events.domain.model.DummyData;

import com.example.cypher_events.ui.entrant.EntrantDashboardFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {


    private static final boolean DEBUG_LOG_ADMIN_ID = false;
    private static final boolean DEBUG_FIRESTORE_DUMMY_DATA = true;

    private static final boolean CREATE_ADMIN = true; // will have to mess with the DummyData.adminSeed()
                                                      // to create a new admin (i suggest keep one for now)
    private static final String ADMIN_KEY = "041f46c418140a17"; // mjoshi3 device id
    private static final String ADMIN_PASSWORD = "9999";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (DEBUG_FIRESTORE_DUMMY_DATA) {
            DummyData.seed();
        }

        if (CREATE_ADMIN) {
            DummyData.adminSeed(ADMIN_KEY);
        }

        logAdminDeviceId();

        // Load default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new com.example.cypher_events.ui.entrant.EntrantDashboardFragment())
                    .commit();
        }



    }

    private void logAdminDeviceId(){
        if (!DEBUG_LOG_ADMIN_ID) return;

        String deviceId = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        Log.d("ADMING_DEVICE_ID", "Device ID: " + deviceId);
        Toast.makeText(this, "Device ID: " + deviceId, Toast.LENGTH_LONG).show();

    }


}
