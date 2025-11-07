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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.cypher_events.R;

public class UpdateProfileEntrantFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update_profile_entrant, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);

        ImageButton backButton = view.findViewById(R.id.btnBack);
        Button btnSave = view.findViewById(R.id.btnSaveProfile);

        if (backButton != null) {
            backButton.setOnClickListener(v -> navController.navigateUp());
        }

        if (btnSave != null) {
            btnSave.setOnClickListener(v ->
                    Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
            );
        }
    }
}
