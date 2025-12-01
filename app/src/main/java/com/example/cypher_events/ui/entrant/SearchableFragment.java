package com.example.cypher_events.ui.entrant;

public interface SearchableFragment {
    void onSearchQueryChanged(String query);
    void onFilterClicked();
    void onAddClicked();

    void onScanQRClicked();
}
