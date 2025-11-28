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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cypher_events.R;
import com.example.cypher_events.util.ImageProcessor;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventManagementFragment extends Fragment {

    private static final String ARG_EVENT_ID = "EventId";

    private TextView tvEventTitle;
    private TextView tvEventDescription;
    private ImageView imgEventPoster;

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
        tvEventDescription = view.findViewById(R.id.tvEventDescription);
        imgEventPoster = view.findViewById(R.id.imgEventPoster);

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

        loadEventDetails();
        loadWaitingList();

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

    private void loadEventDetails() {
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
        String description = doc.getString("Event_description");
        String posterBase64 = doc.getString("Event_posterBase64");

        tvEventTitle.setText(title != null ? title : "Event");

        tvEventDescription.setText(
                description != null && !description.isEmpty()
                        ? description : "No description available."
        );

        if (posterBase64 != null && !posterBase64.isEmpty()) {
            Bitmap bmp = ImageProcessor.base64ToBitmap(posterBase64);
            if (bmp != null) imgEventPoster.setImageBitmap(bmp);
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

    private void loadWaitingList() {
        db.collection("Entrants").get()
                .addOnSuccessListener(query -> {
                    List<WaitingListAdapter.EntrantItem> result = new ArrayList<>();

                    for (DocumentSnapshot doc : query.getDocuments()) {

                        Object raw = doc.get("Entrant_joinedEventIDs");
                        List<String> joined = new ArrayList<>();

                        if (raw instanceof List<?>) {
                            for (Object o : (List<?>) raw) {
                                if (o != null) joined.add(o.toString());
                            }
                        } else if (raw instanceof Map<?, ?>) {
                            Map<?, ?> map = (Map<?, ?>) raw;
                            for (Object key : map.keySet()) {
                                Object value = map.get(key);
                                if (value != null) joined.add(value.toString());
                            }
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
                        Toast.makeText(getContext(),
                                "Failed to load waitlist: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }
}
