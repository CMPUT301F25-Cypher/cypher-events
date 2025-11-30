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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cypher_events.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventManagementFragment extends Fragment {

    private static final String ARG_EVENT_ID = "EventId";

    private TextView tvEventTitle;
    private Button btnGenerateQR;
    private Button btnUpdateEvent;
    private Button btnDrawWinner;
    private Button btnDrawReplacement;
    private ImageButton btnBack;

    private RecyclerView rvWaitingList;
    private TextView tvNoWaitingList;
    private WaitingListAdapter waitingListAdapter;

    private FirebaseFirestore db;
    private String eventId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_detail_organizer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        tvEventTitle = view.findViewById(R.id.tvEventTitle);
        btnGenerateQR = view.findViewById(R.id.btnGenerateQR);
        btnUpdateEvent = view.findViewById(R.id.btnUpdateEvent);
        btnDrawWinner = view.findViewById(R.id.btnDrawWinner);
        btnDrawReplacement = view.findViewById(R.id.btnDrawReplacement);
        btnBack = view.findViewById(R.id.btnBack);

        rvWaitingList = view.findViewById(R.id.rvWaitingList);
        tvNoWaitingList = view.findViewById(R.id.tvNoWaitingList);

        waitingListAdapter = new WaitingListAdapter();
        rvWaitingList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvWaitingList.setAdapter(waitingListAdapter);

        eventId = getArguments() != null ? getArguments().getString(ARG_EVENT_ID) : null;

        if (eventId == null || eventId.trim().isEmpty()) {
            Toast.makeText(getContext(), "No event selected.", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
            return;
        }

        loadEventTitle();

        btnBack.setOnClickListener(v -> requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new MyEventsFragment())
                .commit());

        btnGenerateQR.setOnClickListener(v -> openGenerateQR());
        btnUpdateEvent.setOnClickListener(v -> openUpdateEvent());
        btnDrawWinner.setOnClickListener(v -> openDrawWinner());
        btnDrawReplacement.setOnClickListener(v -> openDrawReplacement());
    }

    private void loadEventTitle() {
        db.collection("Events").document(eventId).get()
                .addOnSuccessListener(this::handleEventLoaded)
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Failed to load event: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }

    private void handleEventLoaded(DocumentSnapshot doc) {
        if (!doc.exists()) {
            Toast.makeText(getContext(), "Event not found.", Toast.LENGTH_SHORT).show();
            return;
        }
        String title = doc.getString("Event_title");
        if (tvEventTitle != null) {
            tvEventTitle.setText(title != null ? title : "Event");
        }
    }

    private void openDrawWinner() {
        Bundle b = new Bundle();
        b.putString(ARG_EVENT_ID, eventId);
        DrawWinnerFragment f = new DrawWinnerFragment();
        f.setArguments(b);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, f)
                .addToBackStack(null)
                .commit();
    }

    private void openDrawReplacement() {
        Bundle b = new Bundle();
        b.putString(ARG_EVENT_ID, eventId);
        DrawReplacementFragment f = new DrawReplacementFragment();
        f.setArguments(b);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, f)
                .addToBackStack(null)
                .commit();
    }

    private void openGenerateQR() {
        Bundle b = new Bundle();
        b.putString(ARG_EVENT_ID, eventId);
        GenerateQRFragment f = new GenerateQRFragment();
        f.setArguments(b);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, f)
                .addToBackStack(null)
                .commit();
    }

    private void openUpdateEvent() {
        Bundle b = new Bundle();
        b.putString(ARG_EVENT_ID, eventId);
        UpdateEventFragment f = new UpdateEventFragment();
        f.setArguments(b);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, f)
                .addToBackStack(null)
                .commit();
    }

    private void viewWaitingList() {
        db.collection("Entrants")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    StringBuilder waitlist = new StringBuilder("Waiting List:\n\n");
                    int count = 0;

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        @SuppressWarnings("unchecked")
                        java.util.List<String> joinedIds = (java.util.List<String>) doc.get("Entrant_joinedEventIDs");
                        
                        if (joinedIds != null && joinedIds.contains(eventId)) {
                            String name = doc.getString("Entrant_name");
                            String email = doc.getString("Entrant_email");
                            count++;
                            waitlist.append(count).append(". ")
                                    .append(name != null ? name : "Unknown")
                                    .append(" (").append(email != null ? email : "No email").append(")\n");
                        }

                        if (joined.contains(eventId)) {
                            WaitingListAdapter.EntrantItem item =
                                    new WaitingListAdapter.EntrantItem();
                            item.name = doc.getString("Entrant_name");
                            item.email = doc.getString("Entrant_email");
                            result.add(item);
                        }
                    }

                    if (result.isEmpty()) {
                        rvWaitingList.setVisibility(View.GONE);
                        tvNoWaitingList.setVisibility(View.VISIBLE);
                    } else {
                        rvWaitingList.setVisibility(View.VISIBLE);
                        tvNoWaitingList.setVisibility(View.GONE);
                        waitingListAdapter.setData(result);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load waiting list: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }
    private void openEventCreatedScreen() {
        if (eventId == null || eventId.trim().isEmpty()) {
            Toast.makeText(getContext(), "No event selected.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Reuse the same flow as CreateEventFragment after submit
        EventCreatedFragment eventCreatedFragment = EventCreatedFragment.newInstance(eventId);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.container, eventCreatedFragment)
                .addToBackStack(null)
                .commit();
    }
}
