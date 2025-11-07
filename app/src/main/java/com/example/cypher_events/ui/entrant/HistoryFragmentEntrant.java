package com.example.cypher_events.ui.entrant;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.view.*;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.example.cypher_events.R;
import com.example.cypher_events.domain.model.Event;
import com.google.firebase.firestore.*;

import java.util.*;

public class HistoryFragmentEntrant extends Fragment {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private FirebaseFirestore db;
    private String deviceId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_entrant, container, false);
    }

    @SuppressLint("HardwareIds")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        deviceId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        recyclerView = view.findViewById(R.id.recyclerHistory);
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
                            .commit());
        }

        loadEntrantHistory();
    }

    // ───────────────────────────────
    private void loadEntrantHistory() {
        db.collection("Entrants").document(deviceId).get()
                .addOnSuccessListener(entrantDoc -> {
                    if (!entrantDoc.exists()) {
                        toast("No entrant data found");
                        return;
                    }

                    List<String> joinedIds = (List<String>) entrantDoc.get("Entrant_joinedEventIDs");
                    List<String> acceptedIds = (List<String>) entrantDoc.get("Entrant_acceptedEventIDs");
                    List<String> declinedIds = (List<String>) entrantDoc.get("Entrant_declinedEventIDs");

                    List<String> allEventIds = new ArrayList<>();
                    if (joinedIds != null) allEventIds.addAll(joinedIds);
                    if (acceptedIds != null) allEventIds.addAll(acceptedIds);
                    if (declinedIds != null) allEventIds.addAll(declinedIds);

                    if (allEventIds.isEmpty()) {
                        toast("No events to show.");
                        return;
                    }

                    fetchEventsByIds(allEventIds);
                })
                .addOnFailureListener(e -> toast("Failed to load entrant: " + e.getMessage()));
    }

    // ───────────────────────────────
    private void fetchEventsByIds(List<String> eventIds) {
        db.collection("Events").get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Event> events = new ArrayList<>();

                    for (DocumentSnapshot doc : querySnapshot) {
                        // Either match Firestore docId or Event_id field
                        String docId = doc.getId();
                        String eventIdField = doc.getString("Event_id");

                        if (eventIds.contains(docId) || eventIds.contains(eventIdField)) {
                            events.add(mapEvent(doc));
                        }
                    }

                    if (events.isEmpty()) toast("No events found for your history.");
                    eventAdapter.submit(events);
                })
                .addOnFailureListener(e -> toast("Failed to load events: " + e.getMessage()));
    }

    // ───────────────────────────────
    // Manual mapping (same as EventEntrantFragment)
    private Event mapEvent(DocumentSnapshot doc) {
        Event e = new Event();

        e.setEvent_id(s(doc.getId())); // Firestore doc ID as fallback
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

        e.setEvent_isActive(isActive != null ? isActive : false);
        e.setEvent_isLotteryEnabled(isLottery != null ? isLottery : false);

        return e;
    }

    private static String s(String v) { return v == null ? "" : v; }
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

    // ───────────────────────────────
    private void openEventDetail(String eventId) {
        Bundle b = new Bundle();
        b.putString("EventId", eventId);

        EventDetailEntrantFragment f = new EventDetailEntrantFragment();
        f.setArguments(b);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.container, f)
                .addToBackStack(null)
                .commit();
    }

    private void toast(String m) {
        Toast.makeText(getContext(), m, Toast.LENGTH_SHORT).show();
    }
}
