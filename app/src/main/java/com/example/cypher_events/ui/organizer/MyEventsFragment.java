package com.example.cypher_events.ui.organizer;

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
import com.example.cypher_events.ui.entrant.EventAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyEventsFragment extends Fragment {

    private static final String ARG_EVENT_ID = "EventId";

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private FirebaseFirestore db;
    private String deviceId;

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

        adapter = new EventAdapter(this::openEventManagementScreen);
        recyclerView.setAdapter(adapter);

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v ->
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.container, new OrganizerDashboardFragment())
                            .commit()
            );
        }

        loadMyEventsForOrganizer();
    }

    private void loadMyEventsForOrganizer() {
        db.collection("Organizers").document(deviceId).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        toast("No organizer profile found.");
                        return;
                    }
                    String email = doc.getString("Organizer_email");
                    if (email == null || email.isEmpty()) {
                        toast("Organizer email missing.");
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

        if (events.isEmpty()) {
            toast("You have not created any events.");
        }

        adapter.submit(events);
    }

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
