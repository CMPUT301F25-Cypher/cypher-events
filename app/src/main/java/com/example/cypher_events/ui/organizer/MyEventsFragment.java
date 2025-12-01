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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cypher_events.R;
import com.example.cypher_events.domain.model.Event;
import com.example.cypher_events.ui.SearchableFragment;
import com.example.cypher_events.ui.entrant.EventAdapter;
import com.example.cypher_events.ui.entrant.ScanQRFragment;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.*;

import java.util.*;

public class MyEventsFragment extends Fragment implements SearchableFragment {

    private static final String ARG_EVENT_ID = "EventId";
    private static final String[] CATEGORIES = {"film", "music", "sports", "gaming"};
    private final boolean[] selectedCats = new boolean[CATEGORIES.length];

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private FirebaseFirestore db;
    private String deviceId;
    private List<Event> allEvents = new ArrayList<>();

    // ------------------------------------------------------------
    // SEARCH BAR HANDLER
    // ------------------------------------------------------------
    @Override
    public void onSearchQueryChanged(String query) {
        if (allEvents == null) return;

        String q = query.trim().toLowerCase();
        if (q.isEmpty()) {
            adapter.submit(allEvents);
            return;
        }

        List<Event> filtered = new ArrayList<>();
        for (Event e : allEvents) {
            if (e.getEvent_title().toLowerCase().contains(q)
                    || e.getEvent_location().toLowerCase().contains(q)
                    || e.getEvent_category().toLowerCase().contains(q)) {
                filtered.add(e);
            }
        }
        adapter.submit(filtered);
    }

    // ------------------------------------------------------------
    // FILTER DIALOG
    // ------------------------------------------------------------
    @Override
    public void onFilterClicked() {
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Filter by category")
                .setMultiChoiceItems(CATEGORIES, selectedCats,
                        (dialog, which, isChecked) -> selectedCats[which] = isChecked)
                .setPositiveButton("Apply", (d, w) -> applyCategoryFilter())
                .setNegativeButton("Clear", (d, w) -> {
                    Arrays.fill(selectedCats, false);
                    adapter.submit(allEvents);
                })
                .show();
    }

    private void applyCategoryFilter() {
        List<String> active = new ArrayList<>();
        for (int i = 0; i < CATEGORIES.length; i++)
            if (selectedCats[i]) active.add(CATEGORIES[i]);

        if (active.isEmpty()) {
            adapter.submit(allEvents);
            return;
        }

        List<Event> filtered = new ArrayList<>();

        for (Event e : allEvents) {
            String cat = e.getEvent_category().toLowerCase();
            for (String a : active) {
                if (cat.equals(a.toLowerCase()))
                    filtered.add(e);
            }
        }

        adapter.submit(filtered);
    }

    @Override
    public void onAddClicked() {
        // Organizer pressing the blue add button = Create event screen
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new CreateEventFragment())
                .addToBackStack(null)
                .commit();
    }
    @Override
    public void onScanQRClicked() {
        // Create the fragment you want to navigate to
        ScanQRFragment scanQRFragment = new ScanQRFragment();

        // Use the hosting activity's FragmentManager to replace the current fragment
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.container, scanQRFragment)
                .addToBackStack(null)  // so back button returns here
                .commit();
    }
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
                Settings.Secure.ANDROID_ID);

        recyclerView = view.findViewById(R.id.recyclerMyEvents);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new EventAdapter(this::openEventManagementScreen);
        recyclerView.setAdapter(adapter);

        loadMyEvents();
    }

    // ------------------------------------------------------------
    // LOAD ONLY ORGANIZER'S EVENTS
    // ------------------------------------------------------------
    private void loadMyEvents() {
        db.collection("Organizers").document(deviceId).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        toast("Organizer profile missing.");
                        return;
                    }

                    List<String> myIds = new ArrayList<>();
                    Object obj = doc.get("Organizer_createdEventIDs");

                    if (obj instanceof List)
                        myIds = (List<String>) obj;
                    else if (obj instanceof Map)
                        myIds.addAll(((Map<String, Object>) obj).keySet());

                    if (myIds.isEmpty()) {
                        adapter.submit(new ArrayList<>());
                        return;
                    }

                    fetchEventsByIds(myIds);
                })
                .addOnFailureListener(e ->
                        toast("Failed to load: " + e.getMessage()));
    }

    // ------------------------------------------------------------
    // FETCH EVENTS BY ID (FAST, PRECISE)
    // ------------------------------------------------------------
    private void fetchEventsByIds(List<String> ids) {
        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();

        for (String id : ids)
            tasks.add(db.collection("Events").document(id).get());

        Tasks.whenAllSuccess(tasks)
                .addOnSuccessListener(results -> {
                    List<Event> events = new ArrayList<>();

                    for (Object o : results) {
                        DocumentSnapshot doc = (DocumentSnapshot) o;
                        if (doc.exists()) events.add(mapEvent(doc));
                    }

                    allEvents = events;
                    adapter.submit(events);
                })
                .addOnFailureListener(e ->
                        toast("Failed to get events: " + e.getMessage()));
    }

    private Event mapEvent(DocumentSnapshot doc) {
        Event e = new Event();
        e.setEvent_id(doc.getId());
        e.setEvent_title(s(doc.getString("Event_title")));
        e.setEvent_description(s(doc.getString("Event_description")));
        e.setEvent_location(s(doc.getString("Event_location")));
        e.setEvent_category(s(doc.getString("Event_category")));
        e.setEvent_status(s(doc.getString("Event_status")));
        e.setEvent_capacity(toInt(doc.get("Event_capacity")));
        e.setEvent_signupStartUtc(toLong(doc.get("Event_signupStartUtc")));
        e.setEvent_signupEndUtc(toLong(doc.get("Event_signupEndUtc")));
        e.setEvent_isActive(toBool(doc.get("Event_isActive")));
        e.setEvent_isLotteryEnabled(toBool(doc.get("Event_isLotteryEnabled")));
        return e;
    }

    private static String s(String v) { return v == null ? "" : v; }
    private static Long toLong(Object v) { return v instanceof Number ? ((Number) v).longValue() : 0; }
    private static Integer toInt(Object v) { return v instanceof Number ? ((Number) v).intValue() : 0; }
    private static Boolean toBool(Object v) { return v instanceof Boolean ? (Boolean) v : false; }

    // ------------------------------------------------------------
    private void openEventManagementScreen(String eventId) {
        Bundle b = new Bundle();
        b.putString(ARG_EVENT_ID, eventId);

        EventManagementFragment f = new EventManagementFragment();
        f.setArguments(b);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.homeContentContainer, f)
                .addToBackStack(null)
                .commit();
    }

    private void toast(String m) {
        Toast.makeText(getContext(), m, Toast.LENGTH_SHORT).show();
    }
}
