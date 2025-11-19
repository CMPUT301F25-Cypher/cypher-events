package com.example.cypher_events.ui.entrant;

import android.os.Bundle;
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

        // QR button: placeholder behavior for now
        if (btnScanQr != null) {
            btnScanQr.setOnClickListener(v ->
                    Toast.makeText(requireContext(),
                            "QR scanning coming soon 🚧",
                            Toast.LENGTH_SHORT
                    ).show()
            );
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
