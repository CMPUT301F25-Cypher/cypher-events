package com.example.cypher_events.ui.entrant;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cypher_events.R;
import com.example.cypher_events.domain.model.Entrant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateProfileFragment extends Fragment {

    private EditText etName, etEmail, etPhone;
    private Button btnUpdateProfile;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private String entrantId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        etName = view.findViewById(R.id.etName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() != null) {
            entrantId = auth.getCurrentUser().getUid();
            loadEntrantProfile();
        }

        btnUpdateProfile.setOnClickListener(v -> updateProfile());
    }

    private void loadEntrantProfile() {
        DocumentReference docRef = db.collection("entrants").document(entrantId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Entrant entrant = documentSnapshot.toObject(Entrant.class);
                if (entrant != null) {
                    etName.setText(entrant.getEntrant_name());
                    etEmail.setText(entrant.getEntrant_email());
                    etPhone.setText(entrant.getEntrant_phone());
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show());
    }

    private void updateProfile() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "Name and Email are required", Toast.LENGTH_SHORT).show();
            return;
        }

        Entrant updatedEntrant = new Entrant(name, email, phone);

        db.collection("entrants").document(entrantId)
                .set(updatedEntrant)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show());
    }
}
