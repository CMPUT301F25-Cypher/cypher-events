package com.example.cypher_events.ui.entrant;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cypher_events.R;
import com.example.cypher_events.domain.model.Event;
import com.example.cypher_events.domain.services.FilterEventsService;
import com.example.cypher_events.domain.services.ListEventsService;

import java.util.ArrayList;
import java.util.List;

public class EventListFragment extends Fragment {

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private EditText searchBar;
    private ImageButton btnFilter;

    private final ListEventsService listEventsService = new ListEventsService();
    private final FilterEventsService filterEventsService = new FilterEventsService();

    private List<Event> allEvents = new ArrayList<>();
    private String selectedCategory = "All";

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
        searchBar = view.findViewById(R.id.searchBar);
        btnFilter = view.findViewById(R.id.btnFilter);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new EventAdapter(eventId -> {
            // TODO: handle click to open event details
        });
        recyclerView.setAdapter(adapter);

        fetchEvents();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnFilter.setOnClickListener(v -> openFilterDialog());
    }

    private void fetchEvents() {
        listEventsService.fetchOpenEvents(new ListEventsService.EventListCallback() {
            @Override
            public void onSuccess(List<Event> events) {
                allEvents = events;
                applyFilters();
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void applyFilters() {
        String query = searchBar.getText().toString().trim();
        List<Event> filtered = filterEventsService.filterEvents(allEvents, query, selectedCategory);
        adapter.submit(filtered);
    }

    private void openFilterDialog() {
        FilterDialogFragment dialog = new FilterDialogFragment();
        dialog.setFilterListener(category -> {
            selectedCategory = category;
            applyFilters();
        });
        dialog.show(getChildFragmentManager(), "FilterDialog");
    }
}
