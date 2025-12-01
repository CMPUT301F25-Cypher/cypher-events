package com.example.cypher_events;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cypher_events.R;
import com.example.cypher_events.ui.admin.AdminDashboardFragment;
import com.example.cypher_events.ui.entrant.UpdateProfileEntrantFragment;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private FirebaseFirestore db;

    private String deviceId;
    private boolean isAdmin = false;

    private TextView tvName, tvEmail, tvPhone, tvAddress;
    private ImageView imgAvatar;
    private Button btnEditProfile;

    @SuppressLint("HardwareIds")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @SuppressLint("HardwareIds")
    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        deviceId = Settings.Secure.getString(
                requireContext().getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        tvName = view.findViewById(R.id.tvProfileName);
        tvEmail = view.findViewById(R.id.tvProfileEmail);
        tvPhone = view.findViewById(R.id.tvProfilePhone);
        tvAddress = view.findViewById(R.id.tvProfileAddress);

        imgAvatar = view.findViewById(R.id.imgProfileAvatar);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        loadProfile();

        imgAvatar.setOnClickListener(v -> checkAdminAndNavigate());
        btnEditProfile.setOnClickListener(v -> goToEditProfile());
    }

    private void loadProfile() {
        db.collection("Entrants").document(deviceId).get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) {
                        Toast.makeText(getContext(), "Profile not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    tvName.setText(doc.getString("Entrant_name"));
                    tvEmail.setText(doc.getString("Entrant_email"));
                    tvPhone.setText(doc.getString("Entrant_phone"));
                    tvAddress.setText(doc.getString("Entrant_address"));

                    Boolean adminFlag = doc.getBoolean("Entrant_isAdmin");
                    isAdmin = adminFlag != null && adminFlag;

                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Failed to load profile: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }

    private void checkAdminAndNavigate() {
        if (isAdmin) {
            // Go to Admin Home
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.homeContentContainer, new AdminDashboardFragment())
                    .addToBackStack(null)
                    .commit();
        } else {
            Toast.makeText(getContext(),
                    "You are not an admin",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void goToEditProfile() {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.homeContentContainer, new UpdateProfileEntrantFragment())
                .addToBackStack(null)
                .commit();
    }
}
