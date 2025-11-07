package com.example.cypher_events;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * AdminManageProfilesFragment
 * Mirrors Manage Images fragment.
 * Handles Add, Delete, and Search for profiles.
 */
public class AdminManageProfilesFragment extends Fragment {

    private EditText searchInput;
    private Button addProfileButton;
    private Button deleteProfileButton;
    private ListView profileListView;

    private ArrayAdapter<String> adapter;
    private List<String> allProfiles;
    private List<String> visibleProfiles;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate the layout
        View view = inflater.inflate(R.layout.fragment_admin_manage_profiles, container, false);

        // Initialize UI components
        searchInput = view.findViewById(R.id.Profil_search);
        addProfileButton = view.findViewById(R.id.Admin_ad_profile);
        deleteProfileButton = view.findViewById(R.id.btnDeleteSelectedProfile);
        profileListView = view.findViewById(R.id.Profile_view);
        profileListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // Initialize data lists
        allProfiles = new ArrayList<>();
        visibleProfiles = new ArrayList<>();

        visibleProfiles.addAll(allProfiles);

        // Set adapter
        adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_single_choice,
                visibleProfiles
        );
        profileListView.setAdapter(adapter);

        // Search functionality
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim().toLowerCase();
                filterProfiles(query);
            }
        });

        // Add profile
        addProfileButton.setOnClickListener(v -> {
            String newProfile = "New_Profile_" + (allProfiles.size() + 1);
            allProfiles.add(newProfile);
            filterProfiles(searchInput.getText().toString());
            Toast.makeText(getContext(), "Added: " + newProfile, Toast.LENGTH_SHORT).show();
        });

        //  Delete selected profile
        deleteProfileButton.setOnClickListener(v -> {
            int selectedPos = profileListView.getCheckedItemPosition();
            if (selectedPos == ListView.INVALID_POSITION) {
                Toast.makeText(getContext(), "Select a profile to delete", Toast.LENGTH_SHORT).show();
                return;
            }
            String selectedProfile = visibleProfiles.get(selectedPos);
            allProfiles.remove(selectedProfile);
            visibleProfiles.remove(selectedProfile);
            adapter.notifyDataSetChanged();
            profileListView.clearChoices();
            Toast.makeText(getContext(), "Deleted: " + selectedProfile, Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    /**
     * Filters profile list based on user search.
     */
    private void filterProfiles(String query) {
        visibleProfiles.clear();
        if (query.isEmpty()) {
            visibleProfiles.addAll(allProfiles);
        } else {
            for (String name : allProfiles) {
                if (name.toLowerCase().contains(query)) {
                    visibleProfiles.add(name);
                }
            }
        }
        adapter.notifyDataSetChanged();
        profileListView.clearChoices();
    }
}
