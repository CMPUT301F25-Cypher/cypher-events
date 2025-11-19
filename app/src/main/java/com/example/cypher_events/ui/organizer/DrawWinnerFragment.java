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

import com.example.cypher_events.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class DrawWinnerFragment extends Fragment {

    private static final String ARG_EVENT_ID = "EventId";

    private Button btnGenerateWinner;
    private TextView tvWinnerResult;
    private ImageButton backButton;

    private FirebaseFirestore db;
    private String eventId;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_draw_winner, container, false);

        btnGenerateWinner = view.findViewById(R.id.btnGenerateWinner);
        tvWinnerResult = view.findViewById(R.id.tvWinnerResult);
        backButton = view.findViewById(R.id.btnBackWinner);

        if (backButton != null) {
            backButton.setOnClickListener(v ->
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.container, new OrganizerDashboardFragment())
                            .commit()
            );
        }

        return view;
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        Bundle args = getArguments();
        eventId = (args != null) ? args.getString(ARG_EVENT_ID) : null;

        if (btnGenerateWinner != null) {
            btnGenerateWinner.setOnClickListener(v -> drawWinner());
        }
    }

    private void drawWinner() {
        if (eventId == null || eventId.trim().isEmpty()) {
            Toast.makeText(getContext(), "No event selected.", Toast.LENGTH_SHORT).show();
            return;
        }

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

    @SuppressWarnings("unchecked")
    private void handleEventLoaded(DocumentSnapshot doc) {
        if (!doc.exists()) {
            Toast.makeText(getContext(), "Event not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Map<String, Object>> waitlist =
                (List<Map<String, Object>>) doc.get("Event_waitlistEntrants");
        List<Map<String, Object>> selected =
                (List<Map<String, Object>>) doc.get("Event_selectedEntrants");

        if (waitlist == null || waitlist.isEmpty()) {
            tvWinnerResult.setText("No entrants in waitlist.");
            Toast.makeText(getContext(), "No entrants available to draw.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> winner = pickRandom(waitlist);
        if (winner == null) {
            tvWinnerResult.setText("No valid entrants.");
            Toast.makeText(getContext(), "No valid entrant found.", Toast.LENGTH_SHORT).show();
            return;
        }

        WriteBatch batch = db.batch();
        batch.update(
                db.collection("Events").document(eventId),
                "Event_waitlistEntrants", FieldValue.arrayRemove(winner),
                "Event_selectedEntrants", FieldValue.arrayUnion(winner)
        );

        batch.commit()
                .addOnSuccessListener(a -> {
                    String name = safeString(winner.get("Entrant_name"));
                    String email = safeString(winner.get("Entrant_email"));

                    String label;
                    if (!name.isEmpty()) {
                        label = "Winner: " + name;
                    } else if (!email.isEmpty()) {
                        label = "Winner (email): " + email;
                    } else {
                        label = "Winner drawn.";
                    }

                    tvWinnerResult.setText(label);
                    Toast.makeText(
                            getContext(),
                            "Winner drawn successfully!",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                getContext(),
                                "Failed to update winner: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    private Map<String, Object> pickRandom(List<Map<String, Object>> waitlist) {
        if (waitlist == null || waitlist.isEmpty()) return null;
        Random rnd = new Random();
        int index = rnd.nextInt(waitlist.size());
        return waitlist.get(index);
    }

    private String safeString(Object v) {
        return (v instanceof String) ? (String) v : "";
    }
}
