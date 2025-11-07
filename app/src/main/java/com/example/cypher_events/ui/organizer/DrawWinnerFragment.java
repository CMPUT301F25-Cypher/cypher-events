package com.example.cypher_events.ui.organizer;

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
import androidx.navigation.Navigation;

import com.example.cypher_events.R;

public class DrawWinnerFragment extends Fragment {

    private Button btnGenerateWinner;
    private TextView tvWinnerResult;
    private ImageButton backButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_draw_winner, container, false);

        btnGenerateWinner = view.findViewById(R.id.btnGenerateWinner);
        tvWinnerResult = view.findViewById(R.id.tvWinnerResult);
        backButton = view.findViewById(R.id.btnBackWinner);

        // In every organizer subfragment with a back button
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.container, new com.example.cypher_events.ui.organizer.OrganizerDashboardFragment())
                        .commit(); // hard return to Organizer home
            });
        }


        btnGenerateWinner.setOnClickListener(v -> {
            // TODO: Connect to Firestore or DrawWinnerService
            tvWinnerResult.setText("Winner: John Doe");
            Toast.makeText(getContext(), "Winner drawn successfully!", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
