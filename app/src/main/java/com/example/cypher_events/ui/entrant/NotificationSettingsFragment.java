package com.example.cypher_events.ui.entrant;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cypher_events.R;
import com.example.cypher_events.domain.service.NotificationOptOutService;

/**
 * Simple settings screen where an entrant can enable/disable notifications.
 * Uses device Android ID (Entrant_id) as key.
 */
public class NotificationSettingsFragment extends Fragment {

    private NotificationOptOutService optOutService;
    private String currentEntrantId;

    public NotificationSettingsFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notification_settings, container, false);

        optOutService = new NotificationOptOutService();

        if (getActivity() != null) {
            currentEntrantId = Settings.Secure.getString(
                    getActivity().getContentResolver(),
                    Settings.Secure.ANDROID_ID
            );
        }

        Switch switchNotifications = v.findViewById(R.id.switchNotifications);

        // Default to "enabled". For full accuracy, you could read the current value from Firestore first.
        switchNotifications.setChecked(true);

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (currentEntrantId == null) {
                Toast.makeText(requireContext(), "No entrant ID available", Toast.LENGTH_SHORT).show();
                return;
            }
            optOutService.setEnabled(currentEntrantId, isChecked);
        });

        return v;
    }
}
