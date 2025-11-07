package com.example.cypher_events.ui.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.cypher_events.R;

public class OrganizerDashboardFragment extends Fragment {

    private TextView tvWelcomeName;
    private Button btnCreateEvent, btnMyEvents;
    private ImageButton btnAccount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organiser_dashboard, container, false);

        // Initialize
        tvWelcomeName = view.findViewById(R.id.tvWelcomeName);
        btnCreateEvent = view.findViewById(R.id.btnCreateEvent);
        btnMyEvents = view.findViewById(R.id.btnMyEvents);
        btnAccount = view.findViewById(R.id.btnAccount);

        // Example welcome text
        tvWelcomeName.setText("Welcome, John Doe");

        // Click listeners
        btnCreateEvent.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_organizerDashboard_to_createEventFragment));

        btnMyEvents.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_organizerDashboard_to_myEventsFragment));

        btnAccount.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Switching to Entrant Dashboard", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigate(R.id.action_organizerDashboard_to_entrantDashboardFragment);
        });

        return view;
    }
}
