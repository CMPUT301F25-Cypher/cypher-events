package com.example.cypher_events.ui.entrant;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cypher_events.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpdateProfileEntrantFragment extends Fragment {

    private FirebaseFirestore db;
    private String deviceId;

    private EditText etFullName;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etAddress;
    private Button btnSave;
    private Button btnDelete;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_update_profile_entrant, container, false);
    }

    @SuppressLint("HardwareIds")
    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        deviceId = Settings.Secure.getString(
                requireContext().getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        etFullName = view.findViewById(R.id.etFullName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etAddress = view.findViewById(R.id.etAddress);
        btnSave = view.findViewById(R.id.btnSaveProfile);
        btnDelete = view.findViewById(R.id.btnDeleteProfile);

        ImageButton backButton = view.findViewById(R.id.btnBack);
        if (backButton != null) {
            backButton.setOnClickListener(v ->
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.container, new EntrantDashboardFragment())
                            .commit()
            );
        }

        loadEntrantProfile();

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> saveProfileChanges());
        }

        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> deleteEntrantProfile());
        }
    }

    private void loadEntrantProfile() {
        db.collection("Entrants").document(deviceId)
                .get()
                .addOnSuccessListener(this::populateFields)
                .addOnFailureListener(e ->
                        Toast.makeText(
                                getContext(),
                                "Failed to load profile: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    private void populateFields(DocumentSnapshot doc) {
        if (!doc.exists()) {
            Toast.makeText(getContext(), "Profile not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = doc.getString("Entrant_name");
        String email = doc.getString("Entrant_email");
        Object phoneObj = doc.get("Entrant_phone");
        String address = doc.getString("Entrant_address");

        String phone = (phoneObj == null) ? "" : String.valueOf(phoneObj);

        etFullName.setText(name != null ? name : "");
        etEmail.setText(email != null ? email : "");
        etPhone.setText(phone);
        etAddress.setText(address != null ? address : "");
    }

    private void saveProfileChanges() {
        String name = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(
                    getContext(),
                    "Please fill in required fields",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("Entrant_name", name);
        updatedData.put("Entrant_email", email);
        updatedData.put("Entrant_phone", phone);
        updatedData.put("Entrant_address", address);

        db.collection("Entrants").document(deviceId)
                .update(updatedData)
                .addOnSuccessListener(a ->
                        Toast.makeText(
                                getContext(),
                                "Profile updated successfully!",
                                Toast.LENGTH_SHORT
                        ).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(
                                getContext(),
                                "Failed to update profile: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    private void deleteEntrantProfile() {
        db.collection("Entrants").document(deviceId)
                .delete()
                .addOnSuccessListener(a -> {
                    Toast.makeText(
                            getContext(),
                            "Profile deleted successfully!",
                            Toast.LENGTH_SHORT
                    ).show();
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.container, new EntrantDashboardFragment())
                            .commit();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                getContext(),
                                "Error deleting profile: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }
}
