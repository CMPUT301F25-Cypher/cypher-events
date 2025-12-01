package com.example.cypher_events.ui.auth;

import android.os.Bundle;
import android.provider.Settings;
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
import com.example.cypher_events.ui.entrant.HomeContainerFragment;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignupFragment extends Fragment {

    private EditText etName, etMobile, etEmail;
    private Button btnSignUp;

    private FirebaseFirestore db;
    private String deviceId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etName = view.findViewById(R.id.etName);
        etMobile = view.findViewById(R.id.etMobile);
        etEmail = view.findViewById(R.id.etEmail);
        btnSignUp = view.findViewById(R.id.btnSignUp);

        db = FirebaseFirestore.getInstance();
        deviceId = Settings.Secure.getString(
                requireActivity().getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        btnSignUp.setOnClickListener(v -> saveUser());
    }

    private void saveUser() {
        String name = etName.getText().toString().trim();
        String phone = etMobile.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Required");
            return;
        }

        Map<String, Object> entrant = new HashMap<>();
        entrant.put("Entrant_id", deviceId);
        entrant.put("Entrant_name", name);
        entrant.put("Entrant_phone", phone.isEmpty() ? "N/A" : phone);
        entrant.put("Entrant_email", email);

        entrant.put("Entrant_joinedEventIDs", new ArrayList<String>());
        entrant.put("Entrant_acceptedEventIDs", new ArrayList<String>());
        entrant.put("Entrant_declinedEventIDs", new ArrayList<String>());

        db.collection("Entrants").document(deviceId)
                .set(entrant)
                .addOnSuccessListener(a -> {
                    Toast.makeText(getContext(),
                            "Account created!",
                            Toast.LENGTH_SHORT).show();

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, new HomeContainerFragment())
                            .commit();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}
