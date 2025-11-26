package com.example.cypher_events.ui.organizer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cypher_events.R;
import com.example.cypher_events.util.ImageProcessor;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventManagementFragment extends Fragment {

    private static final String ARG_EVENT_ID = "EventId";

    private TextView tvEventTitle;
    private TextView tvEventDescription;   // NEW: description at top
    private ImageView imgEventPoster;      // NEW: poster preview

    private Button btnGenerateQR;
    private Button btnUpdateEvent;
    private Button btnViewWaitingList;
    private Button btnDrawWinner;
    private Button btnDrawReplacement;
    private ImageButton btnBack;

    private FirebaseFirestore db;
    private String eventId;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_event_management, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        tvEventTitle = view.findViewById(R.id.tvEventTitle);
        tvEventDescription = view.findViewById(R.id.tvEventDescription); // NEW
        imgEventPoster = view.findViewById(R.id.imgEventPoster);         // NEW

        btnGenerateQR = view.findViewById(R.id.btnGenerateQR);
        btnUpdateEvent = view.findViewById(R.id.btnUpdateEvent);
        btnViewWaitingList = view.findViewById(R.id.btnViewWaitingList);
        btnDrawWinner = view.findViewById(R.id.btnDrawWinner);
        btnDrawReplacement = view.findViewById(R.id.btnDrawReplacement);
        btnBack = view.findViewById(R.id.btnBack);

        Bundle args = getArguments();
        eventId = (args != null) ? args.getString(ARG_EVENT_ID) : null;

        if (eventId == null || eventId.trim().isEmpty()) {
            Toast.makeText(getContext(), "No event selected.", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
            return;
        }

        // now loads title + description + poster
        loadEventDetails();

        if (btnBack != null) {
            btnBack.setOnClickListener(v ->
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.container, new MyEventsFragment())
                            .commit()
            );
        }

        if (btnGenerateQR != null) {
            btnGenerateQR.setOnClickListener(v -> openEventCreatedScreen());
        }

        if (btnUpdateEvent != null) {
            btnUpdateEvent.setOnClickListener(v -> openUpdateEvent());
        }

        if (btnViewWaitingList != null) {
            btnViewWaitingList.setOnClickListener(v -> viewWaitingList());
        }

        if (btnDrawWinner != null) {
            btnDrawWinner.setOnClickListener(v -> openDrawWinner());
        }

        if (btnDrawReplacement != null) {
            btnDrawReplacement.setOnClickListener(v -> openDrawReplacement());
        }
    }

    /**
     * Load event data to show, title, description and poster (from Event_posterBase64)
     */
    private void loadEventDetails() {
        db.collection("Events").document(eventId).get()
                .addOnSuccessListener(this::handleEventLoaded)
                .addOnFailureListener(e ->
                        Toast.makeText(
                                getContext(),
                                "Failed to load event: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    private void handleEventLoaded(DocumentSnapshot doc) {
        if (!doc.exists()) {
            Toast.makeText(getContext(), "Event not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = doc.getString("Event_title");
        String description = doc.getString("Event_description");
        String posterBase64 = doc.getString("Event_posterBase64");

        // title
        if (tvEventTitle != null) {
            tvEventTitle.setText(title != null ? title : "Event");
        }

        // description
        if (tvEventDescription != null) {
            tvEventDescription.setText(
                    description != null && !description.isEmpty()
                            ? description
                            : "No description available."
            );
        }

        // poster
        if (posterBase64 != null && !posterBase64.isEmpty() && imgEventPoster != null) {
            Bitmap posterBitmap = ImageProcessor.base64ToBitmap(posterBase64);
            if (posterBitmap != null) {
                imgEventPoster.setImageBitmap(posterBitmap);
            }
            // if decoding fails, the XML placeholder stays
        }
    }

    private void openDrawWinner() {
        Bundle b = new Bundle();
        b.putString(ARG_EVENT_ID, eventId);

        DrawWinnerFragment f = new DrawWinnerFragment();
        f.setArguments(b);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
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
                .setReorderingAllowed(true)
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
                .setReorderingAllowed(true)
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
                .setReorderingAllowed(true)
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
                    }

                    if (count == 0) {
                        waitlist.append("No entrants on waiting list yet.");
                    }

                    Toast.makeText(getContext(), waitlist.toString(), Toast.LENGTH_LONG).show();
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

        EventCreatedFragment eventCreatedFragment = EventCreatedFragment.newInstance(eventId);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.container, eventCreatedFragment)
                .addToBackStack(null)
                .commit();
    }
}

