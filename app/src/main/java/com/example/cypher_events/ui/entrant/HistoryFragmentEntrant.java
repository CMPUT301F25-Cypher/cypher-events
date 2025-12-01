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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cypher_events.R;
import com.example.cypher_events.domain.model.Event;
import com.example.cypher_events.ui.SearchableFragment;
import com.example.cypher_events.ui.organizer.CreateEventFragment;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HistoryFragmentEntrant extends Fragment implements SearchableFragment {

    private static final String ARG_EVENT_ID = "EventId";

    private static final String[] CATEGORIES = {"film", "music", "sports", "gaming"};
    private final boolean[] selectedCats = new boolean[CATEGORIES.length];

    private List<Event> allEvents = new ArrayList<>();

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private FirebaseFirestore db;
    private String deviceId;



    @Override
    public void onSearchQueryChanged(String query) {
        if (allEvents == null) return;

        String q = query.trim().toLowerCase();
        if (q.isEmpty()) {
            eventAdapter.submit(allEvents);
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
        eventAdapter.submit(filtered);
    }

    @Override
    public void onFilterClicked() {
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Filter by category")
                .setMultiChoiceItems(CATEGORIES, selectedCats, (dialog, which, isChecked) -> {
                    selectedCats[which] = isChecked;
                })
                .setPositiveButton("Apply", (d, w) -> applyCategoryFilter())
                .setNegativeButton("Clear", (d, w) -> {
                    java.util.Arrays.fill(selectedCats, false);
                    eventAdapter.submit(allEvents);
                })
                .show();
    }

    private void applyCategoryFilter() {
        List<String> active = new ArrayList<>();
        for (int i = 0; i < CATEGORIES.length; i++) {
            if (selectedCats[i]) active.add(CATEGORIES[i].toLowerCase());
        }
        if (active.isEmpty()) {
            eventAdapter.submit(allEvents);
            return;
        }

        List<Event> filtered = new ArrayList<>();
        for (Event e : allEvents) {
            String cat = e.getEvent_category().toLowerCase();
            if (active.contains(cat)) filtered.add(e);
        }
        eventAdapter.submit(filtered);
    }

    @Override
    public void onAddClicked() {
        // Open CreateEventFragment inside HomeContainerFragment (child manager)
        Fragment parent = getParentFragment(); // HomeContainerFragment
        if (parent != null) {
            parent.getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.homeContentContainer, new CreateEventFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onScanQRClicked() {
        // Fullscreen scan still goes on the activity manager
        ScanQRFragment scanQRFragment = new ScanQRFragment();

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.container, scanQRFragment)
                .addToBackStack(null)
                .commit();
    }



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

            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            recyclerView.setHasFixedSize(true);
            eventAdapter = new EventAdapter(this::openEventDetail);
            recyclerView.setAdapter(eventAdapter);

            ImageButton backButton = view.findViewById(R.id.btnBack);
            if (backButton != null) {
                backButton.setOnClickListener(v -> {
                    // Switch bottom nav back to Browse tab
                    Fragment parent = getParentFragment(); // HomeContainerFragment
                    if (parent != null && parent.getView() != null) {
                        BottomNavigationView nav =
                                parent.getView().findViewById(R.id.bottomNav);
                        if (nav != null) {
                            nav.setSelectedItemId(R.id.nav_browse);
                        }
                    }
                });
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

                        List<String> joinedIds = extractIdList(entrantDoc.get("Entrant_joinedEventIDs"));
                        List<String> acceptedIds = extractIdList(entrantDoc.get("Entrant_acceptedEventIDs"));
                        List<String> declinedIds = extractIdList(entrantDoc.get("Entrant_declinedEventIDs"));

                        Set<String> allEventIdsSet = new HashSet<>();
                        allEventIdsSet.addAll(joinedIds);
                        allEventIdsSet.addAll(acceptedIds);
                        allEventIdsSet.addAll(declinedIds);

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

    private List<String> extractIdList(Object obj) {
        List<String> list = new ArrayList<>();
        if (obj instanceof List) {
            //noinspection unchecked
            list.addAll((List<String>) obj);
        } else if (obj instanceof java.util.Map) {
            //noinspection unchecked
            list.addAll(((java.util.Map<String, Object>) obj).keySet());
        }
        return list;
    }

    // Fetch events matching the IDs in history
    private void fetchEventsByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            allEvents = new ArrayList<>();
            eventAdapter.submit(allEvents);
            return;
        }

        List<Event> result = new ArrayList<>();
        List<List<String>> batches = new ArrayList<>();

        // Split into groups of 10 (Firestore whereIn limit)
        for (int i = 0; i < ids.size(); i += 10) {
            batches.add(ids.subList(i, Math.min(i + 10, ids.size())));
        }

        List<Task<QuerySnapshot>> tasks = new ArrayList<>();
        for (List<String> batch : batches) {
            Task<QuerySnapshot> t = db.collection("Events")
                    .whereIn(FieldPath.documentId(), batch)
                    .get();
            tasks.add(t);
        }

        Tasks.whenAllSuccess(tasks)
                .addOnSuccessListener(results -> {
                    for (Object o : results) {
                        QuerySnapshot snap = (QuerySnapshot) o;
                        for (DocumentSnapshot doc : snap.getDocuments()) {
                            result.add(mapEvent(doc));
                        }
                    }

                    allEvents = result;
                    eventAdapter.submit(result);

                    if (result.isEmpty()) {
                        toast("No events found in your history.");
                    }
                })
                .addOnFailureListener(e ->
                        toast("Failed to load events: " + e.getMessage()));
    }



    private Event mapEvent(DocumentSnapshot doc) {
        Event e = new Event();

        e.setEvent_id(s(doc.getId()));
        e.setPosterBase64(doc.getString("Event_posterBase64"));
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



    private void openEventDetail(String eventId) {
        Bundle b = new Bundle();
        b.putString(ARG_EVENT_ID, eventId);

        EventDetailEntrantFragment f = new EventDetailEntrantFragment();
        f.setArguments(b);

        Fragment parent = getParentFragment(); // HomeContainerFragment
        if (parent != null) {
            parent.getChildFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.homeContentContainer, f)
                    .addToBackStack(null)
                    .commit();
        }
    }



    private void toast(String message) {
        if (!isAdded() || getContext() == null) return;
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
