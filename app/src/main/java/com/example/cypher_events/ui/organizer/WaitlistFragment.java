package com.example.cypher_events.ui.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cypher_events.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class WaitlistFragment extends Fragment {

    private static final String ARG_EVENT_ID = "EventId";

    private RecyclerView recyclerWaitlist;
    private TextView tvEmptyWaitlist;
    private ImageButton btnBack;
    private WaitlistAdapter adapter;
    private FirebaseFirestore db;
    private String eventId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_waitlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        
        recyclerWaitlist = view.findViewById(R.id.recyclerWaitlist);
        tvEmptyWaitlist = view.findViewById(R.id.tvEmptyWaitlist);
        btnBack = view.findViewById(R.id.btnBackWaitlist);

        Bundle args = getArguments();
        eventId = (args != null) ? args.getString(ARG_EVENT_ID) : null;

        if (eventId == null || eventId.trim().isEmpty()) {
            Toast.makeText(getContext(), "No event selected.", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
            return;
        }

        recyclerWaitlist.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new WaitlistAdapter();
        recyclerWaitlist.setAdapter(adapter);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        }

        loadWaitlist();
    }

    private void loadWaitlist() {
        // First get the Event_id field
        db.collection("Events").document(eventId).get()
                .addOnSuccessListener(eventDoc -> {
                    String actualEventId = eventDoc.getString("Event_id");
                    if (actualEventId == null || actualEventId.isEmpty()) {
                        actualEventId = eventId;
                    }
                    final String searchEventId = actualEventId;

                    db.collection("Entrants")
                            .get()
                            .addOnSuccessListener(querySnapshot -> {
                                List<Entrant> entrants = new ArrayList<>();

                                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                    List<String> joinedIds = new ArrayList<>();
                                    Object joinedObj = doc.get("Entrant_joinedEventIDs");
                                    if (joinedObj instanceof List) {
                                        List<?> tempList = (List<?>) joinedObj;
                                        for (Object item : tempList) {
                                            if (item != null) joinedIds.add(item.toString());
                                        }
                                    } else if (joinedObj instanceof java.util.Map) {
                                        joinedIds.addAll(((java.util.Map<String, Object>) joinedObj).keySet());
                                    }

                                    List<String> selectedIds = new ArrayList<>();
                                    Object selectedObj = doc.get("Entrant_selectedEventIDs");
                                    if (selectedObj instanceof List) {
                                        List<?> tempList = (List<?>) selectedObj;
                                        for (Object item : tempList) {
                                            if (item != null) selectedIds.add(item.toString());
                                        }
                                    } else if (selectedObj instanceof java.util.Map) {
                                        selectedIds.addAll(((java.util.Map<String, Object>) selectedObj).keySet());
                                    }

                                    List<String> acceptedIds = new ArrayList<>();
                                    Object acceptedObj = doc.get("Entrant_acceptedEventIDs");
                                    if (acceptedObj instanceof List) {
                                        List<?> tempList = (List<?>) acceptedObj;
                                        for (Object item : tempList) {
                                            if (item != null) acceptedIds.add(item.toString());
                                        }
                                    } else if (acceptedObj instanceof java.util.Map) {
                                        acceptedIds.addAll(((java.util.Map<String, Object>) acceptedObj).keySet());
                                    }

                                    List<String> declinedIds = new ArrayList<>();
                                    Object declinedObj = doc.get("Entrant_declinedEventIDs");
                                    if (declinedObj instanceof List) {
                                        List<?> tempList = (List<?>) declinedObj;
                                        for (Object item : tempList) {
                                            if (item != null) declinedIds.add(item.toString());
                                        }
                                    } else if (declinedObj instanceof java.util.Map) {
                                        declinedIds.addAll(((java.util.Map<String, Object>) declinedObj).keySet());
                                    }

                                    boolean hasJoined = !joinedIds.isEmpty() && (joinedIds.contains(eventId) || joinedIds.contains(searchEventId));
                                    boolean isSelected = !selectedIds.isEmpty() && (selectedIds.contains(eventId) || selectedIds.contains(searchEventId));
                                    boolean hasAccepted = !acceptedIds.isEmpty() && (acceptedIds.contains(eventId) || acceptedIds.contains(searchEventId));
                                    boolean hasDeclined = !declinedIds.isEmpty() && (declinedIds.contains(eventId) || declinedIds.contains(searchEventId));

                                    // Only show entrants who joined but haven't been selected/accepted/declined
                                    if (hasJoined && !isSelected && !hasAccepted && !hasDeclined) {
                                        String name = doc.getString("Entrant_name");
                                        String email = doc.getString("Entrant_email");
                                        entrants.add(new Entrant(
                                                name != null ? name : "Unknown",
                                                email != null ? email : "No email"
                                        ));
                                    }
                                }

                                if (entrants.isEmpty()) {
                                    recyclerWaitlist.setVisibility(View.GONE);
                                    tvEmptyWaitlist.setVisibility(View.VISIBLE);
                                } else {
                                    recyclerWaitlist.setVisibility(View.VISIBLE);
                                    tvEmptyWaitlist.setVisibility(View.GONE);
                                    adapter.setEntrants(entrants);
                                }
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(getContext(), "Failed to load waitlist: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show()
                            );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load event: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }

    // Simple Entrant model
    static class Entrant {
        String name;
        String email;

        Entrant(String name, String email) {
            this.name = name;
            this.email = email;
        }
    }

    // Adapter for RecyclerView
    static class WaitlistAdapter extends RecyclerView.Adapter<WaitlistAdapter.ViewHolder> {
        private List<Entrant> entrants = new ArrayList<>();

        void setEntrants(List<Entrant> entrants) {
            this.entrants = entrants;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_entrant, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Entrant entrant = entrants.get(position);
            holder.tvName.setText(entrant.name);
            holder.tvEmail.setText(entrant.email);
        }

        @Override
        public int getItemCount() {
            return entrants.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            TextView tvEmail;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvEntrantName);
                tvEmail = itemView.findViewById(R.id.tvEntrantEmail);
            }
        }
    }
}
