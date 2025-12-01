package com.example.cypher_events.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cypher_events.R;
import com.example.cypher_events.domain.model.Event;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminManageEventsFragment extends Fragment {

    private static final String COLLECTION_EVENTS = "Events";
    private static final String FIELD_ID = "Event_id";
    private static final String FIELD_TITLE = "Event_title";
    private static final String FIELD_LOCATION = "Event_location";

    private FirebaseFirestore db;
    private RecyclerView rvEvents;
    private MaterialButton btnDeleteSelected;
    private TextInputEditText etSearch;
    private AdminEventAdapter adapter;
    private final List<Event> allEvents = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_manage_events, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v ->
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, new AdminDashboardFragment())
                            .commit()
            );
        }

        rvEvents = view.findViewById(R.id.rvEvents);
        btnDeleteSelected = view.findViewById(R.id.btnDeleteSelectedEvent);
        etSearch = view.findViewById(R.id.etSearchEvents);

        rvEvents.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new AdminEventAdapter();
        rvEvents.setAdapter(adapter);

        btnDeleteSelected.setOnClickListener(v -> deleteSelectedEvent());

        if (etSearch != null) {
            etSearch.setOnEditorActionListener((tv, actionId, event) -> {
                filterEvents(tv.getText() != null ? tv.getText().toString().trim() : "");
                return true;
            });
        }

        loadEvents();
    }

    private void loadEvents() {
        db.collection(COLLECTION_EVENTS)
                .get()
                .addOnSuccessListener(snaps -> {
                    allEvents.clear();
                    for (QueryDocumentSnapshot doc : snaps) {
                        Event e = snapshotToEvent(doc);
                        if (e != null) allEvents.add(e);
                    }
                    adapter.setEvents(new ArrayList<>(allEvents));
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Failed to load events: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private Event snapshotToEvent(QueryDocumentSnapshot doc) {
        Event e = new Event();
        String id = doc.contains(FIELD_ID) ? doc.getString(FIELD_ID) : doc.getId();
        String title = doc.getString(FIELD_TITLE);
        String loc = doc.getString(FIELD_LOCATION);

        e.setEvent_id(id);
        e.setEvent_title(title != null ? title : "(Untitled)");
        e.setEvent_location(loc != null ? loc : "");
        return e;
    }

    private void filterEvents(String q) {
        if (q == null || q.isEmpty()) {
            adapter.setEvents(new ArrayList<>(allEvents));
            return;
        }

        List<Event> out = new ArrayList<>();
        q = q.toLowerCase();
        for (Event e : allEvents) {
            if (e.getEvent_title() != null && e.getEvent_title().toLowerCase().contains(q))
                out.add(e);
        }

        adapter.setEvents(out);
    }

    private void deleteSelectedEvent() {
        Event selected = adapter.getSelectedEvent();
        if (selected == null) {
            Toast.makeText(requireContext(), "No event selected", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = selected.getEvent_id();
        if (id == null || id.isEmpty()) {
            Toast.makeText(requireContext(), "Invalid event ID", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection(COLLECTION_EVENTS)
                .document(id)
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(requireContext(), "Event deleted", Toast.LENGTH_SHORT).show();
                    allEvents.remove(selected);
                    adapter.remove(selected);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Delete failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private static class AdminEventAdapter extends RecyclerView.Adapter<AdminEventViewHolder> {

        private final List<Event> events = new ArrayList<>();
        private int selectedPos = RecyclerView.NO_POSITION;

        void setEvents(List<Event> newEvents) {
            events.clear();
            if (newEvents != null) events.addAll(newEvents);
            selectedPos = RecyclerView.NO_POSITION;
            notifyDataSetChanged();
        }

        Event getSelectedEvent() {
            if (selectedPos >= 0 && selectedPos < events.size()) return events.get(selectedPos);
            return null;
        }

        void remove(Event e) {
            int idx = events.indexOf(e);
            if (idx >= 0) {
                events.remove(idx);
                notifyItemRemoved(idx);
                if (selectedPos == idx) selectedPos = RecyclerView.NO_POSITION;
            }
        }

        @NonNull
        @Override
        public AdminEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
            return new AdminEventViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull AdminEventViewHolder holder, int position) {

            Event event = events.get(position);
            boolean isSelected = (position == selectedPos);

            holder.bind(event, isSelected);

            // Click on radio button
            holder.rbSelect.setOnClickListener(v -> {
                int oldPos = selectedPos;
                int newPos = holder.getBindingAdapterPosition();
                if (newPos == RecyclerView.NO_POSITION) return;

                selectedPos = newPos;

                if (oldPos != RecyclerView.NO_POSITION) notifyItemChanged(oldPos);
                notifyItemChanged(selectedPos);
            });

            // Click on item row
            holder.itemView.setOnClickListener(v -> {
                int oldPos = selectedPos;
                int newPos = holder.getBindingAdapterPosition();
                if (newPos == RecyclerView.NO_POSITION) return;

                selectedPos = newPos;

                if (oldPos != RecyclerView.NO_POSITION) notifyItemChanged(oldPos);
                notifyItemChanged(selectedPos);
            });
        }



        @Override
        public int getItemCount() {
            return events.size();
        }
    }

    public static class AdminEventViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvLocation;
        TextView tvDate;
        RadioButton rbSelect;

        public AdminEventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvEventName);
            tvLocation = itemView.findViewById(R.id.tvEventLocation);
            tvDate = itemView.findViewById(R.id.tvEventDate);
            rbSelect = itemView.findViewById(R.id.rbSelect);
            rbSelect.setVisibility(View.VISIBLE);
        }

        public void bind(Event e, boolean isSelected) {
            tvName.setText(e.getEvent_title());
            tvLocation.setText(e.getEvent_location());
            tvDate.setText(formatDate(e.getEvent_signupStartUtc()));
            rbSelect.setChecked(isSelected);
        }
    }

    private static String formatDate(long utc) {
        if (utc <= 0) return "";
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(utc));
    }
}