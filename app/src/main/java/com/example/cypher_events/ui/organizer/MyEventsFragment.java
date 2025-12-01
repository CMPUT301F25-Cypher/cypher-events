package com.example.cypher_events.ui.organizer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cypher_events.R;
import com.example.cypher_events.domain.model.Event;
import com.example.cypher_events.ui.SearchableFragment;
import com.example.cypher_events.ui.entrant.EventAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MyEventsFragment extends Fragment implements SearchableFragment {

    private static final String ARG_EVENT_ID = "EventId";

    private static final String[] CATEGORIES = {"film", "music", "sports", "gaming"};
    private final boolean[] selectedCats = new boolean[CATEGORIES.length];

    private List<Event> allEvents = new ArrayList<>();

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private FirebaseFirestore db;
    private String deviceId;

    // ───── Search / filter / add from HomeContainer ─────

    @Override
    public void onSearchQueryChanged(String query) {
        if (allEvents == null || eventAdapter == null) return;

        String q = query.trim().toLowerCase(Locale.ROOT);
        if (q.isEmpty()) {
            eventAdapter.submit(allEvents);
            return;
        }

        List<Event> filtered = new ArrayList<>();
        for (Event e : allEvents) {
            if (e.getEvent_title().toLowerCase(Locale.ROOT).contains(q)
                    || e.getEvent_location().toLowerCase(Locale.ROOT).contains(q)
                    || e.getEvent_category().toLowerCase(Locale.ROOT).contains(q)) {
                filtered.add(e);
            }
        }
        eventAdapter.submit(filtered);
    }

    @Override
    public void onFilterClicked() {
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Filter by category")
                .setMultiChoiceItems(CATEGORIES, selectedCats,
                        (dialog, which, isChecked) -> selectedCats[which] = isChecked)
                .setPositiveButton("Apply", (d, w) -> applyCategoryFilter())
                .setNegativeButton("Clear", (d, w) -> {
                    Arrays.fill(selectedCats, false);
                    eventAdapter.submit(allEvents);
                })
                .show();
    }

    private void applyCategoryFilter() {
        if (allEvents == null) return;

        List<String> active = new ArrayList<>();
        for (int i = 0; i < CATEGORIES.length; i++) {
            if (selectedCats[i]) active.add(CATEGORIES[i].toLowerCase(Locale.ROOT));
        }
        if (active.isEmpty()) {
            eventAdapter.submit(allEvents);
            return;
        }

        List<Event> filtered = new ArrayList<>();
        for (Event e : allEvents) {
            String cat = e.getEvent_category().toLowerCase(Locale.ROOT);
            if (active.contains(cat)) filtered.add(e);
        }
        eventAdapter.submit(filtered);
    }

    @Override
    public void onAddClicked() {
        // Anyone can create events – go to CreateEventFragment
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.container, new CreateEventFragment())
                .addToBackStack(null)
                .commit();
    }

    // ───── Lifecycle ─────

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_my_events, container, false);
    }

    @SuppressLint("HardwareIds")
    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        deviceId = Settings.Secure.getString(
                requireContext().getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        recyclerView = view.findViewById(R.id.recyclerMyEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        eventAdapter = new EventAdapter(this::openEventManagementScreen);
        recyclerView.setAdapter(eventAdapter);

        // You said you don’t really need a back button here anymore,
        // so we can rely on bottom nav instead – no back button wiring.

        loadMyEventsForOrganizer();
    }

    // ───── Firestore ─────

    private void loadMyEventsForOrganizer() {
        db.collection("Organizers").document(deviceId).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        toast("No organizer profile found.");
                        eventAdapter.submit(new ArrayList<>());
                        return;
                    }
                    String email = doc.getString("Organizer_email");
                    if (email == null || email.isEmpty()) {
                        toast("Organizer email missing.");
                        eventAdapter.submit(new ArrayList<>());
                        return;
                    }
                    queryEventsByOrganizerEmail(email);
                })
                .addOnFailureListener(e ->
                        toast("Failed to load organizer: " + e.getMessage())
                );
    }

    private void queryEventsByOrganizerEmail(String email) {
        db.collection("Events")
                .whereEqualTo("Event_organizerEmail", email)
                .get()
                .addOnSuccessListener(this::handleEventsSnapshot)
                .addOnFailureListener(e ->
                        toast("Failed to load events: " + e.getMessage())
                );
    }

    private void handleEventsSnapshot(QuerySnapshot snap) {
        List<Event> events = new ArrayList<>();
        for (DocumentSnapshot doc : snap) {
            events.add(mapEvent(doc));
        }

        allEvents = events;            // <- this is what search/filter use

        if (events.isEmpty()) {
            toast("You have not created any events.");
        }

        eventAdapter.submit(events);
    }

    private Event mapEvent(DocumentSnapshot doc) {
        Event e = new Event();

        e.setEvent_id(s(doc.getId()));
        e.setEvent_title(s(doc.getString("Event_title")));
        e.setPosterBase64(doc.getString("Event_posterBase64")); // <- keep your poster
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

    public void openEventManagementScreen(String eventId) {
        Bundle b = new Bundle();
        b.putString(ARG_EVENT_ID, eventId);

        EventManagementFragment f = new EventManagementFragment();
        f.setArguments(b);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.container, f)
                .addToBackStack(null)
                .commit();
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

    private void toast(String m) {
        Toast.makeText(getContext(), m, Toast.LENGTH_SHORT).show();
    }
}
