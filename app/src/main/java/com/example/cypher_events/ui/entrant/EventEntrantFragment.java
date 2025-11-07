package com.example.cypher_events.ui.entrant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cypher_events.R;
import com.example.cypher_events.domain.model.Event;

import java.util.ArrayList;
import java.util.List;

public class EventEntrantFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_entrant, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerEvents);
        ImageButton backButton = view.findViewById(R.id.btnBack);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Back button: use fragment manager pop
        if (backButton != null) {
            backButton.setOnClickListener(v ->
                    requireActivity().getSupportFragmentManager().popBackStack()
            );
        }

        // Adapter setup
        EventAdapter adapter = new EventAdapter(eventId -> {
            Bundle bundle = new Bundle();
            bundle.putString("eventId", eventId);

            EventDetailEntrantFragment detailFragment = new EventDetailEntrantFragment();
            detailFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setAdapter(adapter);

        // ✅ Dummy data for testing
        List<Event> dummyEvents = new ArrayList<>();
        dummyEvents.add(new Event(
                "1",
                "Music Night",
                "A fun evening of music and performances",
                "Community Hall",
                System.currentTimeMillis(),
                System.currentTimeMillis() + 86400000L,
                50
        ));
        dummyEvents.add(new Event(
                "2",
                "Coding Hackathon",
                "24-hour coding marathon with prizes",
                "Tech Centre",
                System.currentTimeMillis(),
                System.currentTimeMillis() + 86400000L,
                100
        ));

        adapter.submit(dummyEvents);
    }
}