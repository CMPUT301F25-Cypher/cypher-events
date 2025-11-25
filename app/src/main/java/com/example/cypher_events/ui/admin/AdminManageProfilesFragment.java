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
import com.example.cypher_events.domain.model.Entrant;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminManageProfilesFragment extends Fragment {

    private static final String COLLECTION_ENTRANTS = "Entrants";
    private static final String FIELD_NAME          = "Entrant_name";
    private static final String FIELD_EMAIL         = "Entrant_email";

    private FirebaseFirestore db;

    private TextInputEditText etSearch;
    private ListView listView;
    private MaterialButton btnDelete;

    private final List<EntrantItem> allItems = new ArrayList<>();
    private final List<EntrantItem> filteredItems = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private int selectedIndex = -1;

    private static class EntrantItem {
        String id;    // document id
        String name;
        String email;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_admin_manage_profiles, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(
                    v -> requireActivity().getOnBackPressedDispatcher().onBackPressed()
            );
        }

        etSearch = view.findViewById(R.id.Profile_search);
        listView = view.findViewById(R.id.Profile_view);
        btnDelete = view.findViewById(R.id.btnDeleteSelectedProfile);

        adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_single_choice,
                new ArrayList<>()
        );
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listView.setOnItemClickListener((parent, v, position, id) -> {
            selectedIndex = position;
        });

        btnDelete.setOnClickListener(v -> deleteSelectedProfile());

        if (etSearch != null) {
            etSearch.setOnEditorActionListener((tv, actionId, event) -> {
                String q = tv.getText() != null ? tv.getText().toString().trim() : "";
                filterProfiles(q);
                return true;
            });
        }

        loadProfiles();
    }

    private void loadProfiles() {
        db.collection(COLLECTION_ENTRANTS)
                .get()
                .addOnSuccessListener(snaps -> {
                    allItems.clear();
                    for (QueryDocumentSnapshot doc : snaps) {
                        EntrantItem item = new EntrantItem();
                        item.id    = doc.getId();
                        item.name  = doc.getString(FIELD_NAME);
                        item.email = doc.getString(FIELD_EMAIL);
                        if (item.name == null) item.name = "(Unnamed)";
                        if (item.email == null) item.email = "";
                        allItems.add(item);
                    }
                    filterProfiles("");
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(),
                                "Failed to load profiles: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }

    private void filterProfiles(String query) {
        filteredItems.clear();
        List<String> labels = new ArrayList<>();

        String q = query == null ? "" : query.toLowerCase();
        for (EntrantItem item : allItems) {
            boolean match = q.isEmpty()
                    || (item.name != null && item.name.toLowerCase().contains(q))
                    || (item.email != null && item.email.toLowerCase().contains(q));
            if (match) {
                filteredItems.add(item);
                labels.add(item.name + " (" + item.email + ")");
            }
        }

        selectedIndex = -1;
        adapter.clear();
        adapter.addAll(labels);
        adapter.notifyDataSetChanged();
    }

    private void deleteSelectedProfile() {
        if (selectedIndex < 0 || selectedIndex >= filteredItems.size()) {
            Toast.makeText(requireContext(),
                    "No profile selected", Toast.LENGTH_SHORT).show();
            return;
        }

        EntrantItem item = filteredItems.get(selectedIndex);
        db.collection(COLLECTION_ENTRANTS)
                .document(item.id)
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(requireContext(),
                            "Profile deleted", Toast.LENGTH_SHORT).show();
                    allItems.remove(item);
                    filterProfiles(etSearch != null && etSearch.getText() != null
                            ? etSearch.getText().toString()
                            : "");

                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(),
                                "Failed to delete: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }
}
