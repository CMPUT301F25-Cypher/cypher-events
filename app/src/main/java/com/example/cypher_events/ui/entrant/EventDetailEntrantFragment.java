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

        ImageButton back = view.findViewById(R.id.btnBack);
        if (back != null) back.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        Button join = view.findViewById(R.id.btnJoinWaitlist);
        if (join != null) join.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Joined waiting list!", Toast.LENGTH_SHORT).show()
        );

        // (Keep your Accept/Decline wiring as-is if you like.)
    }

}
