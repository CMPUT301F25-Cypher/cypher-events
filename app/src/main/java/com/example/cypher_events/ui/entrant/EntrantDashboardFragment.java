package com.example.cypher_events.ui.entrant;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cypher_events.R;
import com.example.cypher_events.ui.entrant.HistoryFragmentEntrant;
import com.example.cypher_events.ui.entrant.UpdateProfileEntrantFragment;
import com.example.cypher_events.ui.entrant.EventEntrantFragment;
import com.example.cypher_events.ui.organizer.OrganizerDashboardFragment;
import com.example.cypher_events.ui.admin.AdminDashboardFragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import android.provider.Settings;


public class EntrantDashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        // Inflate the entrant dashboard layout
        return inflater.inflate(R.layout.entrant_dashboard_fragment, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        Button btnScanQr        = view.findViewById(R.id.buttonScanQr);
        Button btnEvents        = view.findViewById(R.id.btnEvents);
        Button btnShowHistory   = view.findViewById(R.id.btnShowHistory);
        Button btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);
        ImageButton btnOrganizer = view.findViewById(R.id.btnOrganizer);
        ImageButton btnAdmin = view.findViewById(R.id.btnAdmin);

        String deviceId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        Toast.makeText(requireContext(), "DeviceID: " + deviceId, Toast.LENGTH_LONG).show();
        android.util.Log.d("ADMIN_CHECK", "DeviceID = " + deviceId);

        FirebaseFirestore.getInstance()
                .collection("Entrants")
                .document(deviceId)   // Entrant_id is stored as document id
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        Log.d("ADMIN_CHECK", "Entrant doc does not exist for this device.");
                        return;
                    }

                    Boolean isAdmin = doc.getBoolean("Entrant_isAdmin");

                    Log.d("ADMIN_CHECK", "Entrant_isAdmin = " + isAdmin);

                    if (Boolean.TRUE.equals(isAdmin)) {
                        btnAdmin.setVisibility(View.VISIBLE);
                        btnAdmin.setOnClickListener(v ->
                                requireActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.container, new AdminDashboardFragment())
                                        .addToBackStack(null)
                                        .commit()
                        );
                    }
                })
                .addOnFailureListener(e -> Log.e("ADMIN_CHECK", "Error loading entrant", e));


        // QR button: open scan/join event screen
        if (btnScanQr != null) {
            btnScanQr.setOnClickListener(v -> open(new ScanQRFragment()));
        }

        // Go to list of events entrant can join
        if (btnEvents != null) {
            btnEvents.setOnClickListener(v -> open(new EventEntrantFragment()));
        }

        // Go to entrant history screen
        if (btnShowHistory != null) {
            btnShowHistory.setOnClickListener(v -> open(new HistoryFragmentEntrant()));
        }

        // Go to update profile screen
        if (btnUpdateProfile != null) {
            btnUpdateProfile.setOnClickListener(v -> open(new UpdateProfileEntrantFragment()));
        }

        // Switch to organizer dashboard
        if (btnOrganizer != null) {
            btnOrganizer.setOnClickListener(v ->
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.container, new OrganizerDashboardFragment())
                            .commit()
            );
        }
    }

    // Helper to open a fragment and add it to back stack
    private void open(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
