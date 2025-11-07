package com.example.cypher_events.ui.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

        tvWelcomeName = view.findViewById(R.id.tvWelcomeName);
        btnCreateEvent = view.findViewById(R.id.btnCreateEvent);
        btnMyEvents = view.findViewById(R.id.btnMyEvents);
        btnAccount = view.findViewById(R.id.btnAccount);

        tvWelcomeName.setText("Welcome, John Doe");

        if (btnCreateEvent != null) {
            btnCreateEvent.setOnClickListener(v ->
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.container, new CreateEventFragment())
                            .commit());
        }

        if (btnMyEvents != null) {
            btnMyEvents.setOnClickListener(v ->
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.container, new MyEventsFragment())
                            .commit());
        }

        if (btnAccount != null) {
            btnAccount.setOnClickListener(v ->
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.container,
                                    new com.example.cypher_events.ui.entrant.EntrantDashboardFragment())
                            .commit());
        }

        return view;
    }
}
