/**
 * EventListFragment.java
 *
 * Purpose:
 * Displays a scrollable list of events available for entrants to join.
 * Fetches data from Firebase Firestore and populates it into a RecyclerView.
 *
 * Outstanding issues:
 * - Error handling can be improved to show messages on screen.
 * - Navigation to EventDetailFragment is not yet implemented.
 */

package com.example.cypher_events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cypher_events.domain.model.Event;
import com.example.cypher_events.domain.model.Entrant;
import com.example.cypher_events.domain.model.Firestore;
import com.example.cypher_events.ui.entrant.EventAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * EventListFragment displays available events and allows user to accept/decline invitations.
 */
public class EventListFragment extends Fragment {

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private FirebaseFirestore db;
    private List<Event> allEvents = new ArrayList<>();
    private Entrant currentEntrant; //load from firestore
    private Firestore firestoreHelper = new Firestore();
    private String currentUserUid;

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

        //get current user uid
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            currentUserUid = null;
        }

        adapter = new EventAdapter(new EventAdapter.OnClick() {
            @Override
            public void open(String eventId) {

            }

            @Override
            public void accept(String eventId) {
                if (currentEntrant == null) {
                return;
                }
                if (currentEntrant.getEntrant_acceptedEvents() == null) {
                    currentEntrant.setEntrant_acceptedEvents(new ArrayList<>());
                }
            }

            @Override
            public void decline(String eventId) {
                if (currentEntrant == null) {
                    return;
                }

                if (currentEntrant.getEntrant_declinedEvents() == null) {
                    currentEntrant.setEntrant_declinedEvents(new ArrayList<>());
                }

                if (!currentEntrant.getEntrant_declinedEvents().contains(eventId)) {
                    currentEntrant.getEntrant_declinedEvents().add(eventId);
                    String docId = (currentUserUid != null) ? currentUserUid : currentEntrant.getEntrant_email();
                    firestoreHelper.push_DB("entrants", docId, currentEntrant.toMap());
                }
                //remove from displayed list (local UI)
                allEvents.removeIf(e -> e.id != null && e.id.equals(eventId));
                adapter.submit(allEvents);
            }
        });

        recyclerView.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();

        fetchEntrant(); //load current user
        fetchEvents();

    }

    /**
     * Retrieves all Event documents from the Firestore "events" collection
     * and updates adapter with the results.
     */
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

    private void fetchEntrant() {
        // use currentUserUid where possible
        String userId = (FirebaseAuth.getInstance().getCurrentUser() != null)
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (userId == null) {
            // redirect to login page if no user is signed in
            return;
        }

        firestoreHelper.pull_db("entrants", userId).addOnSuccessListener(doc -> {
            if (doc.exists()) {
                currentEntrant = doc.toObject(Entrant.class);

                // Ensure lists are not null
                if (currentEntrant.getEntrant_acceptedEvents() == null) {
                    currentEntrant.setEntrant_acceptedEvents(new ArrayList<>());
                }
                if (currentEntrant.getEntrant_declinedEvents() == null) {
                    currentEntrant.setEntrant_declinedEvents(new ArrayList<>());
                }
                if (currentEntrant.getEntrant_joinedEvents() == null) {
                    currentEntrant.setEntrant_joinedEvents(new ArrayList<>());
                }
            }
        }).addOnFailureListener(Throwable::printStackTrace);
    }
}