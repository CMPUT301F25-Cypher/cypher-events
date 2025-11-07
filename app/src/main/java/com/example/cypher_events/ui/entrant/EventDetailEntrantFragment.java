package com.example.cypher_events.ui.entrant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.cypher_events.R;

public class EventDetailEntrantFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_detail_entrant, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);

        ImageButton backButton = view.findViewById(R.id.btnBack);
        Button joinButton = view.findViewById(R.id.btnJoinWaitlist);
        LinearLayout acceptDeclineLayout = view.findViewById(R.id.layoutAcceptDecline);
        Button acceptButton = view.findViewById(R.id.btnAccept);
        Button declineButton = view.findViewById(R.id.btnDecline);

        backButton.setOnClickListener(v -> navController.navigateUp());

        joinButton.setOnClickListener(v ->
                Toast.makeText(getContext(), "Joined waiting list!", Toast.LENGTH_SHORT).show()
        );

        acceptButton.setOnClickListener(v ->
                Toast.makeText(getContext(), "Accepted invitation!", Toast.LENGTH_SHORT).show()
        );

        declineButton.setOnClickListener(v ->
                Toast.makeText(getContext(), "Declined invitation.", Toast.LENGTH_SHORT).show()
        );

        // Example toggle for testing
        boolean isSelected = false;
        if (isSelected) {
            acceptDeclineLayout.setVisibility(View.VISIBLE);
        }
    }
}
