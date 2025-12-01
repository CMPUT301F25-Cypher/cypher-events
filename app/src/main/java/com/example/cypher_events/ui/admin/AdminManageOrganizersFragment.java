package com.example.cypher_events.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cypher_events.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminManageOrganizersFragment extends Fragment {

    private static final String COLLECTION_ORGANIZERS = "Organizers";
    private static final String FIELD_EMAIL           = "Organizer_email";

    private FirebaseFirestore db;

    private TextInputEditText etSearch;
    private ListView listView;
    private MaterialButton btnRemove;

    private final List<OrganizerItem> allItems = new ArrayList<>();
    private final List<OrganizerItem> filteredItems = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private int selectedIndex = -1;

    private static class OrganizerItem {
        String id;
        String email;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_manage_organizers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        etSearch = view.findViewById(R.id.Organizer_search);
        listView = view.findViewById(R.id.Organizer_view);
        btnRemove = view.findViewById(R.id.btnRemoveSelectedOrganizer);

        adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_single_choice,
                new ArrayList<>()
        );
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listView.setOnItemClickListener((parent, v, position, id) -> selectedIndex = position);

        btnRemove.setOnClickListener(v -> removeSelectedOrganizer());

        if (etSearch != null) {
            etSearch.setOnEditorActionListener((tv, actionId, event) -> {
                String q = tv.getText() != null ? tv.getText().toString().trim() : "";
                filterOrganizers(q);
                return true;
            });
        }

        loadOrganizers();
    }
    private void loadOrganizers() {
        db.collection(COLLECTION_ORGANIZERS)
                .get()
                .addOnSuccessListener(snaps -> {
                    int count = snaps.size();
                    Toast.makeText(requireContext(), "Organizers found: " + count, Toast.LENGTH_SHORT).show();

                    allItems.clear();
                    for (QueryDocumentSnapshot doc : snaps) {
                        OrganizerItem item = new OrganizerItem();
                        item.id = doc.getId();
                        item.email = doc.getString(FIELD_EMAIL);
                        if (item.email == null) item.email = "(no-email)";
                        allItems.add(item);
                    }
                    filterOrganizers("");
                })
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to load organizers: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void filterOrganizers(String query) {
        filteredItems.clear();
        List<String> labels = new ArrayList<>();

        String q = query == null ? "" : query.toLowerCase();
        for (OrganizerItem item : allItems) {
            boolean match = q.isEmpty() || (item.email != null && item.email.toLowerCase().contains(q));
            if (match) {
                filteredItems.add(item);
                labels.add(item.email + " (" + item.id + ")");
            }
        }

        selectedIndex = -1;
        adapter.clear();
        adapter.addAll(labels);
        adapter.notifyDataSetChanged();
    }

    private void removeSelectedOrganizer() {
        if (selectedIndex < 0 || selectedIndex >= filteredItems.size()) {
            Toast.makeText(requireContext(), "No organizer selected", Toast.LENGTH_SHORT).show();
            return;
        }

        OrganizerItem item = filteredItems.get(selectedIndex);
        db.collection(COLLECTION_ORGANIZERS)
                .document(item.id)
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(requireContext(), "Organizer removed successfully", Toast.LENGTH_SHORT).show();
                    allItems.remove(item);
                    filterOrganizers(etSearch != null && etSearch.getText() != null ? etSearch.getText().toString() : "");
                })
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to remove organizer: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}