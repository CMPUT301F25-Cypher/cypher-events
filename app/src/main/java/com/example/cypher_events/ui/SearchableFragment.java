package com.example.cypher_events.ui;

public interface SearchableFragment {
    void onSearchQueryChanged(String query);
    void onFilterClicked();
    void onAddClicked();
}
