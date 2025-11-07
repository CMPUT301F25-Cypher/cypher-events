package com.example.cypher_events.ui.entrant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.cypher_events.R;

public class EntrantDashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.entrant_dashboard_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);

        Button btnEvents = view.findViewById(R.id.btnEvents);
        Button btnHistory = view.findViewById(R.id.btnShowHistory);
        Button btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);
        ImageButton btnOrganizer = view.findViewById(R.id.btnOrganizer);
        Button btnScanQr = view.findViewById(R.id.buttonScanQr);

        if (btnEvents != null) {
            btnEvents.setOnClickListener(v ->
                    navController.navigate(R.id.action_entrantDashboard_to_eventEntrant));
        }

        if (btnHistory != null) {
            btnHistory.setOnClickListener(v ->
                    navController.navigate(R.id.action_entrantDashboard_to_historyEntrant));
        }

        if (btnUpdateProfile != null) {
            btnUpdateProfile.setOnClickListener(v ->
                    navController.navigate(R.id.action_entrantDashboard_to_updateProfileEntrant));
        }

        if (btnOrganizer != null) {
            btnOrganizer.setOnClickListener(v ->
                    navController.navigate(R.id.action_entrantDashboard_to_organizerDashboard));
        }
    }
}
