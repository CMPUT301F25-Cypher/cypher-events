package com.example.cypher_events.ui.entrant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cypher_events.R;
import com.example.cypher_events.domain.model.Event;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class EventEntrantFragment extends Fragment {

    private static final String ARG_EVENT_ID = "EventId";

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_event_entrant, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerEvents);
        ImageButton backButton = view.findViewById(R.id.btnBack);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setHasFixedSize(true);
        eventAdapter = new EventAdapter(this::openEventDetail);
        recyclerView.setAdapter(eventAdapter);

        if (backButton != null) {
            backButton.setOnClickListener(v ->
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.container, new EntrantDashboardFragment())
                            .commit()
            );
        }

        loadEventsFromFirestore();
    }

    // Load events for entrants from Firestore
    private void loadEventsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Events").get()
                .addOnSuccessListener(snap -> {
                    if (snap == null || snap.isEmpty()) {
                        // Fallback to lowercase collection name if needed
                        db.collection("events").orderBy("Event_signupStartUtc").get()
                                .addOnSuccessListener(this::consumeSnapshot)
                                .addOnFailureListener(e -> toast("Failed: " + e.getMessage()));
                    } else {
                        consumeSnapshot(snap);
                    }
                })
                .addOnFailureListener(e -> toast("Failed: " + e.getMessage()));
    }

    // Convert Firestore snapshot into a list of Event objects
    private void consumeSnapshot(QuerySnapshot querySnapshot) {
        if (querySnapshot == null) {
            eventAdapter.submit(new ArrayList<>());
            toast("No events available online.");
            return;
        }

        List<Event> events = new ArrayList<>();
        for (DocumentSnapshot doc : querySnapshot) {
            events.add(mapEvent(doc));
        }

        eventAdapter.submit(events);

        if (events.isEmpty()) {
            toast("No events available online.");
        }
    }

    // Manual map from Firestore fields to Event model
    private Event mapEvent(DocumentSnapshot doc) {
        Event e = new Event();

        // Always use Firestore document id as event id
        e.setEvent_id(doc.getId());

        e.setEvent_title(s(doc.getString("Event_title")));
        e.setEvent_description(s(doc.getString("Event_description")));
        e.setEvent_location(s(doc.getString("Event_location")));
        e.setEvent_category(s(doc.getString("Event_category")));
        e.setEvent_status(s(doc.getString("Event_status")));

        Long start = toLong(doc.get("Event_signupStartUtc"));
        Long end = toLong(doc.get("Event_signupEndUtc"));
        Integer cap = toInt(doc.get("Event_capacity"));

        e.setEvent_signupStartUtc(start != null ? start : 0L);
        e.setEvent_signupEndUtc(end != null ? end : 0L);
        e.setEvent_capacity(cap != null ? cap : 0);

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
        return null;
    }

    private static Integer toInt(Object v) {
        if (v instanceof Integer) return (Integer) v;
        if (v instanceof Long) return ((Long) v).intValue();
        if (v instanceof Double) return ((Double) v).intValue();
        return null;
    }

    private static Boolean toBool(Object v) {
        if (v instanceof Boolean) return (Boolean) v;
        if (v instanceof String) return Boolean.parseBoolean((String) v);
        return null;
    }

    // Open event detail screen for the selected event
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
