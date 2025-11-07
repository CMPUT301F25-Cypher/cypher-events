/**
 * EventListFragment.java
 *
 * Purpose:
 * Displays a scrollable list of events available for entrants to join.
 * Fetches data from Firebase Firestore and populates it into a RecyclerView.
 * Supports search and filter functionality.
 *
 * Outstanding issues:
 * - Error handling can be improved to show messages on screen.
 * - Navigation to EventDetailFragment is not yet implemented.
 */

package com.example.cypher_events;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cypher_events.domain.model.Event;
import com.example.cypher_events.domain.model.Entrant;
import com.example.cypher_events.domain.model.Firestore;
import com.example.cypher_events.ui.entrant.EventAdapter;
import com.example.cypher_events.ui.entrant.FilterDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EventListFragment extends Fragment {

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private FirebaseFirestore db;
    private List<Event> allEvents = new ArrayList<>();
    private Entrant currentEntrant;
    private Firestore firestoreHelper = new Firestore();
    private String currentUserUid;

    private EditText editTextSearch;
    private Button btnFilter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_events_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        editTextSearch = view.findViewById(R.id.editTextSearch);
        btnFilter = view.findViewById(R.id.btnFilter);

        // Get current user UID
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            currentUserUid = null;
        }

        adapter = new EventAdapter(new EventAdapter.OnClick() {
            @Override
            public void open(String eventId) {
                // TODO: Open EventDetailFragment
            }

            @Override
            public void accept(String eventId) {
                if (currentEntrant == null) return;
                if (currentEntrant.getEntrant_acceptedEvents() == null) {
                    currentEntrant.setEntrant_acceptedEvents(new ArrayList<>());
                }
                if (!currentEntrant.getEntrant_acceptedEvents().contains(eventId)) {
                    currentEntrant.getEntrant_acceptedEvents().add(eventId);
                    String docId = (currentUserUid != null) ? currentUserUid : currentEntrant.getEntrant_email();
                    firestoreHelper.push_DB("entrants", docId, currentEntrant.toMap());
                }
            }

            @Override
            public void decline(String eventId) {
                if (currentEntrant == null) return;
                if (currentEntrant.getEntrant_declinedEvents() == null) {
                    currentEntrant.setEntrant_declinedEvents(new ArrayList<>());
                }
                if (!currentEntrant.getEntrant_declinedEvents().contains(eventId)) {
                    currentEntrant.getEntrant_declinedEvents().add(eventId);
                    String docId = (currentUserUid != null) ? currentUserUid : currentEntrant.getEntrant_email();
                    firestoreHelper.push_DB("entrants", docId, currentEntrant.toMap());
                }
                // Remove from displayed list
                allEvents.removeIf(e -> e.id != null && e.id.equals(eventId));
                adapter.submit(allEvents);
            }
        });

        recyclerView.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();

        setupSearch();
        setupFilter();

        fetchEntrant();
        fetchEvents();
    }

    /** Sets up instant filtering by search text */
    private void setupSearch() {
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase();
                List<Event> filtered = new ArrayList<>();
                for (Event event : allEvents) {
                    if ((event.title != null && event.title.toLowerCase().contains(query)) ||
                            (event.location != null && event.location.toLowerCase().contains(query))) {
                        filtered.add(event);
                    }
                }
                adapter.submit(filtered);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    /** Sets up the filter dialog */
    private void setupFilter() {
        btnFilter.setOnClickListener(v -> {
            FilterDialogFragment dialog = new FilterDialogFragment();
            dialog.setFilterListener((selectedInterests, startDateUtc, endDateUtc) -> {
                List<Event> filtered = new ArrayList<>();
                for (Event event : allEvents) {
                    boolean matchesInterest = selectedInterests.isEmpty() || selectedInterests.contains(event.interests);
                    boolean matchesDate = (event.signupStartUtc >= startDateUtc && event.signupEndUtc <= endDateUtc);
                    if (matchesInterest && matchesDate) {
                        filtered.add(event);
                    }
                }
                adapter.submit(filtered);
            });
            dialog.show(getParentFragmentManager(), "FilterDialog");
        });
    }

    /** Fetches all events from Firestore */
    private void fetchEvents() {
        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allEvents.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Event event = doc.toObject(Event.class);
                        if (event.id == null) event.id = doc.getId();
                        allEvents.add(event);
                    }
                    adapter.submit(allEvents);
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }

    /** Fetches current entrant from Firestore */
    private void fetchEntrant() {
        if (currentUserUid == null) return;

        firestoreHelper.pull_db("entrants", currentUserUid)
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        currentEntrant = doc.toObject(Entrant.class);
                        if (currentEntrant.getEntrant_acceptedEvents() == null)
                            currentEntrant.setEntrant_acceptedEvents(new ArrayList<>());
                        if (currentEntrant.getEntrant_declinedEvents() == null)
                            currentEntrant.setEntrant_declinedEvents(new ArrayList<>());
                        if (currentEntrant.getEntrant_joinedEvents() == null)
                            currentEntrant.setEntrant_joinedEvents(new ArrayList<>());
                    }
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }
}
