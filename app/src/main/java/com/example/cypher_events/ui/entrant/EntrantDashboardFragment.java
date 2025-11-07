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
import com.example.cypher_events.ui.organizer.OrganizerDashboardFragment;

// Import your own fragments below:
import com.example.cypher_events.ui.entrant.UpdateProfileEntrantFragment;
import com.example.cypher_events.ui.entrant.HistoryFragmentEntrant;
import com.example.cypher_events.ui.entrant.EventDetailEntrantFragment;

public class EntrantDashboardFragment extends Fragment {

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Use the file name you actually have. You posted "entrant_dashboard_fragment.xml".
        return inflater.inflate(R.layout.entrant_dashboard_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnScanQr        = view.findViewById(R.id.buttonScanQr);
        Button btnEvents        = view.findViewById(R.id.btnEvents);
        Button btnShowHistory   = view.findViewById(R.id.btnShowHistory);
        Button btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);
        ImageButton btnOrganizer = view.findViewById(R.id.btnOrganizer);


        // QR → toast only
        if (btnScanQr != null) {
            btnScanQr.setOnClickListener(v ->
                    Toast.makeText(requireContext(), "QR scanning coming soon 🚧", Toast.LENGTH_SHORT).show()
            );
        }

        if (btnEvents != null) {
            btnEvents.setOnClickListener(v -> open(new EventEntrantFragment()));
        }
        if (btnShowHistory != null) {
            btnShowHistory.setOnClickListener(v -> open(new HistoryFragmentEntrant()));
        }
        if (btnUpdateProfile != null) {
            btnUpdateProfile.setOnClickListener(v -> open(new UpdateProfileEntrantFragment()));
        }
        if (btnOrganizer != null) {
            btnOrganizer.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.container, new com.example.cypher_events.ui.organizer.OrganizerDashboardFragment())
                        .commit(); // hard switch; no back stack needed
                android.util.Log.d("EntrantDash","btnOrganizer=" + (btnOrganizer!=null));
            });
        }
    }

    private void open(Fragment f) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, f)
                .addToBackStack(null)
                .commit();
    }
}