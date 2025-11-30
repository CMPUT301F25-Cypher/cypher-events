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

import com.example.cypher_events.R;
import com.example.cypher_events.ui.organizer.MyEventsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeContainerFragment extends Fragment {

    private EditText etSearch;
    private ImageButton btnFilter;
    private ImageButton btnAdd;
    private Fragment currentFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_container, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etSearch = view.findViewById(R.id.etSearch);
        btnFilter = view.findViewById(R.id.btnFilter);
        btnAdd = view.findViewById(R.id.btnAdd);

        final BottomNavigationView nav = view.findViewById(R.id.bottomNav);

        // Search typing
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (currentFragment instanceof SearchableFragment) {
                    ((SearchableFragment) currentFragment)
                            .onSearchQueryChanged(s.toString());
                }
            }
        });

        // Filter button
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment instanceof SearchableFragment) {
                    ((SearchableFragment) currentFragment).onFilterClicked();
                }
            }
        });

        // Add button
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment instanceof SearchableFragment) {
                    ((SearchableFragment) currentFragment).onAddClicked();
                }
            }
        });

        // ottom nav selection
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_browse) {
                load(new EventEntrantFragment());
                return true;

            } else if (id == R.id.nav_joined) {
                load(new HistoryFragmentEntrant());
                return true;

            } else if (id == R.id.nav_my_events) {
                load(new MyEventsFragment());
                return true;

            } else if (id == R.id.nav_notifications) {
                load(new NotificationFragment());
                return true;

            } else if (id == R.id.nav_profile) {
                load(new UpdateProfileEntrantFragment());
                return true;
            }

            return false;
        });

        // Default tab
        if (savedInstanceState == null) {
            nav.setSelectedItemId(R.id.nav_browse);
        }
    }

    private void load(Fragment fragment) {
        currentFragment = fragment;

        // Show/hide search row depending on fragment type
        View root = requireView();
        View searchRow = root.findViewById(R.id.layoutSearchRow);
        if (fragment instanceof SearchableFragment) {
            searchRow.setVisibility(View.VISIBLE);
        } else {
            searchRow.setVisibility(View.GONE);
        }

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.homeContentContainer, fragment)
                .commit();
    }
}
