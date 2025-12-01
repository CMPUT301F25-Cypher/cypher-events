package com.example.cypher_events.ui.entrant;

/**
 * EventEntrantFragment
 * 
 * Purpose: Browse and discover available events as an entrant.
 * Implements SearchableFragment interface for search and filter functionality.
 * 
 * Key Features:
 * - Display all available events in a grid layout
 * - Search events by title, location, or category
 * - Filter events by category (Film, Music, Sports, Gaming)
 * - Filter events by date range (based on actual event date)
 * - Navigate to event details for joining/viewing
 * - Real-time data loading from Firestore
 * 
 * Navigation: Main browse tab in HomeContainerFragment
 * 
 * Outstanding Issues: None
 */

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cypher_events.R;
import com.example.cypher_events.domain.model.Event;
import com.example.cypher_events.ui.SearchableFragment;
import com.example.cypher_events.ui.organizer.CreateEventFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventEntrantFragment extends Fragment implements SearchableFragment {

    private static final String ARG_EVENT_ID = "EventId";

    private final String[] CATEGORIES = {"film", "music", "sports", "gaming"};
    private final boolean[] selectedCats = new boolean[CATEGORIES.length];

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;

    private List<Event> allEvents = new ArrayList<>();
    
    private long filterStartDate = 0;
    private long filterEndDate = 0;

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
        showFilterDialog();
    }

    private void showFilterDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_filter_events, null);
        
        // Category checkboxes
        CheckBox cbFilm = dialogView.findViewById(R.id.cbFilm);
        CheckBox cbMusic = dialogView.findViewById(R.id.cbMusic);
        CheckBox cbSports = dialogView.findViewById(R.id.cbSports);
        CheckBox cbGaming = dialogView.findViewById(R.id.cbGaming);
        
        // Set current selections
        cbFilm.setChecked(selectedCats[0]);
        cbMusic.setChecked(selectedCats[1]);
        cbSports.setChecked(selectedCats[2]);
        cbGaming.setChecked(selectedCats[3]);
        
        // Date filter buttons
        Button btnStartDate = dialogView.findViewById(R.id.btnSelectStartDate);
        Button btnEndDate = dialogView.findViewById(R.id.btnSelectEndDate);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        
        // Update button text if dates are already selected
        if (filterStartDate > 0) {
            btnStartDate.setText("Start: " + dateFormat.format(filterStartDate));
        }
        if (filterEndDate > 0) {
            btnEndDate.setText("End: " + dateFormat.format(filterEndDate));
        }
        
        // Start date picker
        btnStartDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            if (filterStartDate > 0) {
                cal.setTimeInMillis(filterStartDate);
            }
            
            DatePickerDialog picker = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth, 0, 0, 0);
                    selected.set(Calendar.MILLISECOND, 0);
                    filterStartDate = selected.getTimeInMillis();
                    btnStartDate.setText("Start: " + dateFormat.format(filterStartDate));
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            );
            picker.show();
        });
        
        // End date picker
        btnEndDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            if (filterEndDate > 0) {
                cal.setTimeInMillis(filterEndDate);
            }
            
            DatePickerDialog picker = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth, 23, 59, 59);
                    selected.set(Calendar.MILLISECOND, 999);
                    filterEndDate = selected.getTimeInMillis();
                    btnEndDate.setText("End: " + dateFormat.format(filterEndDate));
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            );
            picker.show();
        });
        
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Filter Events")
                .setView(dialogView)
                .setPositiveButton("Apply", null)
                .setNegativeButton("Clear", null)
                .create();
        
        dialog.show();
        
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            // Update category selections
            selectedCats[0] = cbFilm.isChecked();
            selectedCats[1] = cbMusic.isChecked();
            selectedCats[2] = cbSports.isChecked();
            selectedCats[3] = cbGaming.isChecked();
            
            applyFilters();
            dialog.dismiss();
        });
        
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> {
            clearFilters();
            dialog.dismiss();
        });
    }

    private void applyFilters() {
        List<Event> filtered = new ArrayList<>();
        
        List<String> activeCategories = new ArrayList<>();
        for (int i = 0; i < CATEGORIES.length; i++) {
            if (selectedCats[i]) activeCategories.add(CATEGORIES[i].toLowerCase());
        }
        
        boolean hasDateFilter = filterStartDate > 0 || filterEndDate > 0;
        boolean hasCategoryFilter = !activeCategories.isEmpty();
        
        for (Event e : allEvents) {
            boolean matchesCategory = true;
            boolean matchesDate = true;
            
            if (hasCategoryFilter) {
                String cat = e.getEvent_category().toLowerCase();
                matchesCategory = activeCategories.contains(cat);
            }
            
            if (hasDateFilter) {
                long eventDate = e.getEvent_dateUtc();
                if (eventDate > 0) {
                    if (filterStartDate > 0 && eventDate < filterStartDate) {
                        matchesDate = false;
                    }
                    if (filterEndDate > 0 && eventDate > filterEndDate) {
                        matchesDate = false;
                    }
                } else {
                    matchesDate = false;
                }
            }
            
            if (matchesCategory && matchesDate) {
                filtered.add(e);
            }
        }
        
        eventAdapter.submit(filtered);
    }

    private void clearFilters() {
        Arrays.fill(selectedCats, false);
        filterStartDate = 0;
        filterEndDate = 0;
        eventAdapter.submit(allEvents);
        toast("Filters cleared");
    }

    @Override
    public void onAddClicked() {
        // Open CreateEventFragment inside HomeContainerFragment
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.homeContentContainer, new CreateEventFragment())
                .addToBackStack(null)
                .commit();
    }

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

        if (events.isEmpty()) {
            toast("No events available online.");
        }

        allEvents = events;
        eventAdapter.submit(events);

    }

    // Manual map from Firestore fields to Event model
    private Event mapEvent(DocumentSnapshot doc) {
        Event e = new Event();

        // Always use Firestore document id as event id
        e.setEvent_id(doc.getId());

        e.setEvent_title(s(doc.getString("Event_title")));
        e.setPosterBase64(doc.getString("Event_posterBase64"));
        e.setEvent_description(s(doc.getString("Event_description")));
        e.setEvent_location(s(doc.getString("Event_location")));
        e.setEvent_category(s(doc.getString("Event_category")));
        e.setEvent_status(s(doc.getString("Event_status")));

        Long start = toLong(doc.get("Event_signupStartUtc"));
        Long end = toLong(doc.get("Event_signupEndUtc"));
        Long eventDate = toLong(doc.get("Event_dateUtc"));
        Integer cap = toInt(doc.get("Event_capacity"));

        e.setEvent_signupStartUtc(start != null ? start : 0L);
        e.setEvent_signupEndUtc(end != null ? end : 0L);
        e.setEvent_dateUtc(eventDate != null ? eventDate : 0L);
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

        Fragment parent = getParentFragment(); // this is HomeContainerFragment
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
