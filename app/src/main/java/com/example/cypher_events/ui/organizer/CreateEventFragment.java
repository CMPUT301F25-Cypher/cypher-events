package com.example.cypher_events.ui.organizer;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cypher_events.R;

import java.util.Calendar;

public class CreateEventFragment extends Fragment {

    private Button btnSignupStart, btnSignupEnd, btnUploadPoster;
    private ImageButton btnBack;
    private EditText etEventCapacity;
    private ImageView imgPosterPreview;
    private Uri selectedImageUri = null;

    // Modern, safe activity result launcher
    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    imgPosterPreview.setImageURI(uri);
                    Toast.makeText(getContext(), "Poster selected!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "No image selected.", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);

        btnSignupStart = view.findViewById(R.id.btnSelectStartDate);
        btnSignupEnd = view.findViewById(R.id.btnSelectEndDate);
        btnUploadPoster = view.findViewById(R.id.btnUploadPoster);
        btnBack = view.findViewById(R.id.btnBack);
        etEventCapacity = view.findViewById(R.id.inputEventCapacity);


        // Back button → Organizer dashboard
        if (btnBack != null) {
            btnBack.setOnClickListener(v ->
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.container, new OrganizerDashboardFragment())
                            .commit());
        }

        // Date pickers
        if (btnSignupStart != null)
            btnSignupStart.setOnClickListener(v -> showDatePicker("Signup Start Date selected"));

        if (btnSignupEnd != null)
            btnSignupEnd.setOnClickListener(v -> showDatePicker("Signup End Date selected"));

        // Upload Poster → open image picker
        if (btnUploadPoster != null)
            btnUploadPoster.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        return view;
    }

    private void showDatePicker(String message) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(getContext(), (view, y, m, d) ->
                Toast.makeText(getContext(), message + ": " + d + "/" + (m + 1) + "/" + y,
                        Toast.LENGTH_SHORT).show(),
                year, month, day).show();
    }
}
