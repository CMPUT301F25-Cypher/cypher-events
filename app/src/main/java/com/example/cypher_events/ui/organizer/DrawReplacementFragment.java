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

public class DrawReplacementFragment extends Fragment {

    private Button btnGenerateReplacement;
    private TextView tvReplacementResult;
    private ImageButton btnBackReplacement;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_draw_replacement, container, false);

        btnGenerateReplacement = view.findViewById(R.id.btnGenerateReplacement);
        tvReplacementResult = view.findViewById(R.id.tvReplacementResult);

        ImageButton backButton = view.findViewById(R.id.btnBackReplacement);
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.container, new com.example.cypher_events.ui.organizer.OrganizerDashboardFragment())
                        .commit(); // hard return to Organizer home
            });}

        btnGenerateReplacement.setOnClickListener(v -> {
            // TODO: Connect to Firestore or DrawReplacementService
            tvReplacementResult.setText("Replacement: Jane Smith");
            Toast.makeText(getContext(), "Replacement drawn successfully!", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
