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

import com.example.cypher_events.ProfileFragment;
import com.example.cypher_events.R;
import com.example.cypher_events.ui.SearchableFragment;
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

        // Text change -> forward to current Searchable fragment
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

        btnFilter.setOnClickListener(v -> {
            if (currentFragment instanceof SearchableFragment) {
                ((SearchableFragment) currentFragment).onFilterClicked();
            }
        });

        btnAdd.setOnClickListener(v -> {
            if (currentFragment instanceof SearchableFragment) {
                ((SearchableFragment) currentFragment).onAddClicked();
            }
        });

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
                load(new ProfileFragment());
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            load(new EventEntrantFragment());          // ensures search row is shown
            nav.setSelectedItemId(R.id.nav_browse);    // just updates the icon state
        }
    }

    private void load(Fragment fragment) {
        currentFragment = fragment;

        View root = requireView();
        View searchRow = root.findViewById(R.id.layoutSearchRow);

        boolean searchable = fragment instanceof SearchableFragment;
        searchRow.setVisibility(searchable ? View.VISIBLE : View.GONE);

        if (!searchable) {
            etSearch.setText("");
        }

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.homeContentContainer, fragment)
                .commit();
    }
}
