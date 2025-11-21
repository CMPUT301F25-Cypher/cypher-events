package com.example.cypher_events.ui.entrant;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cypher_events.R;
import com.example.cypher_events.domain.model.Event;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HistoryFragmentEntrant extends Fragment {

    private static final String ARG_EVENT_ID = "EventId";

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private FirebaseFirestore db;
    private String deviceId;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_history_entrant, container, false);
    }

    @SuppressLint("HardwareIds")
    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        try {
            db = FirebaseFirestore.getInstance();
            deviceId = Settings.Secure.getString(
                    requireContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID
            );

            recyclerView = view.findViewById(R.id.recyclerHistory);
            if (recyclerView == null) {
                toast("Error loading view");
                return;
            }
            
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            eventAdapter = new EventAdapter(this::openEventDetail);
            recyclerView.setAdapter(eventAdapter);

            ImageButton backButton = view.findViewById(R.id.btnBack);
            if (backButton != null) {
                backButton.setOnClickListener(v ->
                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.container, new EntrantDashboardFragment())
                                .commit()
                );
            }

            loadEntrantHistory();
        } catch (Exception e) {
            toast("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadEntrantHistory() {
        if (db == null || deviceId == null || deviceId.isEmpty()) {
            toast("Error: Unable to load data");
            return;
        }

        db.collection("Entrants").document(deviceId).get()
                .addOnSuccessListener(entrantDoc -> {
                    try {
                        if (!entrantDoc.exists()) {
                            toast("No profile found. Please create a profile first.");
                            eventAdapter.submit(new ArrayList<>());
                            return;
                        }

                        @SuppressWarnings("unchecked")
                        List<String> joinedIds =
                                (List<String>) entrantDoc.get("Entrant_joinedEventIDs");
                        @SuppressWarnings("unchecked")
                        List<String> acceptedIds =
                                (List<String>) entrantDoc.get("Entrant_acceptedEventIDs");
                        @SuppressWarnings("unchecked")
                        List<String> declinedIds =
                                (List<String>) entrantDoc.get("Entrant_declinedEventIDs");

                        Set<String> allEventIdsSet = new HashSet<>();
                        if (joinedIds != null) {
                            allEventIdsSet.addAll(joinedIds);
                        }
                        if (acceptedIds != null) {
                            allEventIdsSet.addAll(acceptedIds);
                        }
                        if (declinedIds != null) {
                            allEventIdsSet.addAll(declinedIds);
                        }

                        if (allEventIdsSet.isEmpty()) {
                            toast("No events yet. Join some events to see them here!");
                            eventAdapter.submit(new ArrayList<>());
                            return;
                        }

                        fetchEventsByIds(new ArrayList<>(allEventIdsSet));
                    } catch (Exception e) {
                        toast("Error loading events: " + e.getMessage());
                        e.printStackTrace();
                    }
                })
                .addOnFailureListener(e -> {
                    toast("Failed to load: " + e.getMessage());
                    e.printStackTrace();
                });
    }

    // Fetch events matching the IDs in history
    private void fetchEventsByIds(List<String> eventIds) {
        db.collection("Events").get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Event> events = new ArrayList<>();

                    for (DocumentSnapshot doc : querySnapshot) {
                        String docId = doc.getId();
                        String eventIdField = doc.getString("Event_id");

                        if (eventIds.contains(docId) || eventIds.contains(eventIdField)) {
                            events.add(mapEvent(doc));
                        }
                    }

                    if (events.isEmpty()) {
                        toast("No events found for your history.");
                    }

                    eventAdapter.submit(events);
                })
                .addOnFailureListener(e ->
                        toast("Failed to load events: " + e.getMessage())
                );
    }

    // Map Firestore document into Event model (same structure as other fragments)
    private Event mapEvent(DocumentSnapshot doc) {
        Event e = new Event();

        e.setEvent_id(s(doc.getId()));
        e.setEvent_title(s(doc.getString("Event_title")));
        e.setEvent_description(s(doc.getString("Event_description")));
        e.setEvent_location(s(doc.getString("Event_location")));
        e.setEvent_category(s(doc.getString("Event_category")));
        e.setEvent_status(s(doc.getString("Event_status")));

        e.setEvent_capacity(toInt(doc.get("Event_capacity")));
        e.setEvent_signupStartUtc(toLong(doc.get("Event_signupStartUtc")));
        e.setEvent_signupEndUtc(toLong(doc.get("Event_signupEndUtc")));

        Boolean isActive = toBool(doc.get("Event_isActive"));
        Boolean isLottery = toBool(doc.get("Event_isLotteryEnabled"));

        e.setEvent_isActive(isActive != null && isActive);
        e.setEvent_isLotteryEnabled(isLottery != null && isLottery);

        return e;
    }

    private static String s(String v) {
        return v == null ? "" : v;
    }

    private static Long toLong(Object v) {
        if (v instanceof Long) return (Long) v;
        if (v instanceof Double) return ((Double) v).longValue();
        if (v instanceof Integer) return ((Integer) v).longValue();
        return 0L;
    }

    private static Integer toInt(Object v) {
        if (v instanceof Integer) return (Integer) v;
        if (v instanceof Long) return ((Long) v).intValue();
        if (v instanceof Double) return ((Double) v).intValue();
        return 0;
    }

    private static Boolean toBool(Object v) {
        if (v instanceof Boolean) return (Boolean) v;
        if (v instanceof String) return Boolean.parseBoolean((String) v);
        return false;
    }

    // Open event details for a history item
    private void openEventDetail(String eventId) {
        Bundle b = new Bundle();
        b.putString(ARG_EVENT_ID, eventId);

        EventDetailEntrantFragment f = new EventDetailEntrantFragment();
        f.setArguments(b);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.container, f)
                .addToBackStack(null)
                .commit();
    }

    private void toast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
