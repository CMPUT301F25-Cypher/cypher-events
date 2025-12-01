package com.example.cypher_events.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cypher_events.R;

public class AdminDashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.admin_dashboard, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        // Buttons from XML
        View btnManageEvents   = view.findViewById(R.id.btnAdminEvents);
        View btnManageProfiles = view.findViewById(R.id.btnAdminProfiles);
        View btnManageImages   = view.findViewById(R.id.btnAdminImages);
        View btnManageOrganizers = view.findViewById(R.id.btnAdminOrganizers);
        ImageButton btnProfile = view.findViewById(R.id.btnAdminProfile);

        // Navigate to Manage Events screen
        btnManageEvents.setOnClickListener(v ->
                open(new AdminManageEventsFragment())
        );

        // Navigate to Manage Profiles screen
        btnManageProfiles.setOnClickListener(v ->
                open(new AdminManageProfilesFragment())
        );

        // Navigate to Manage Images screen
        btnManageImages.setOnClickListener(v ->
                open(new AdminManageImagesFragment())
        );

        // Navigate to Manage Organizers screen
        if (btnManageOrganizers != null) {
            btnManageOrganizers.setOnClickListener(v ->
                    open(new AdminManageOrganizersFragment())
            );
        }
        View btnManageNotifications = view.findViewById(R.id.btnAdminNotifications);
        if (btnManageNotifications != null) {
            btnManageNotifications.setOnClickListener(v -> open(new AdminManageNotificationsFragment()));
        }

        btnProfile.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.container,
                                new com.example.cypher_events.ui.entrant.HomeContainerFragment())
                        .addToBackStack(null)
                        .commit()
        );

    }

    private void open(Fragment fragment) {
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }
}