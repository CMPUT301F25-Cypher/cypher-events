package com.example.cypher_events.ui.organizer;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cypher_events.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.example.cypher_events.util.ImageProcessor;

public class CreateEventFragment extends Fragment {

    private FirebaseFirestore db;
    private String deviceId;
    private String organizerEmail = "unknown@example.com";

    private TextInputEditText inputEventName;
    private TextInputEditText inputEventDescription;
    private TextInputEditText inputEventLocation;
    private TextInputEditText inputEventCategory;
    private TextInputEditText inputEventCapacity;
    private Button btnSignupStart;
    private Button btnSignupEnd;
    private Button btnUploadPoster;
    private Button btnSubmit;
    private ImageButton btnBack;
    private ImageView imgPosterPreview;

    private String startDate = "";
    private String endDate = "";
    private long signupStartUtc = 0L;
    private long signupEndUtc = 0L;

    private double selectedLat = 0;
    private double selectedLng = 0;

    private Uri selectedImageUri = null;

    // will hold the compressed base64 poster string that we save to firestore
    private String posterBase64 = "";

    @Override
    public void onResume() {
        super.onResume();
        View bar = requireActivity().findViewById(R.id.layoutSearchRow);
        if (bar != null) bar.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        View bar = requireActivity().findViewById(R.id.layoutSearchRow);
        if (bar != null) bar.setVisibility(View.VISIBLE);
    }


    //opens the system gallery gallery/file picker
    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    //remember the original uri to show a quick preview
                    selectedImageUri = uri;

                    //show the picked image in the preview box so the user can see it immediately
                    if (imgPosterPreview != null) {
                        imgPosterPreview.setImageURI(uri);
                    }

                    //now also turn this image into a compressed base64 string
                    //this uses the ImageProcessor helper class
                    String resultBase64 = ImageProcessor.uriToBase64(requireContext(), uri);

                    if (resultBase64 != null && !resultBase64.isEmpty()) {
                        //keep this string to save it with the event in Firestore
                        posterBase64 = resultBase64;

                        Toast.makeText(getContext(), "Poster selected and processed!", Toast.LENGTH_SHORT).show();
                    } else {
                        //if something went wrong while processing the image, still keep the local preview
                        //but does not save a poster
                        posterBase64 = "";
                        Toast.makeText(getContext(), "Poster selected, but could not process image.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //user backed out without selecting and image from the gallery
                    Toast.makeText(getContext(), "No image selected.", Toast.LENGTH_SHORT).show();
                }
            });
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_create_event, container, false);
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

        View mapPicker = view.findViewById(R.id.layoutMapPicker);
        TextView tvMapLocationSummary = view.findViewById(R.id.tvMapLocationSummary);

        inputEventName = view.findViewById(R.id.inputEventName);
        inputEventDescription = view.findViewById(R.id.inputEventDescription);
        inputEventLocation = view.findViewById(R.id.inputEventLocation);
        inputEventCategory = view.findViewById(R.id.inputEventCategory);
        inputEventCapacity = view.findViewById(R.id.inputEventCapacity);
        btnSignupStart = view.findViewById(R.id.btnSelectStartDate);
        btnSignupEnd = view.findViewById(R.id.btnSelectEndDate);
        btnUploadPoster = view.findViewById(R.id.btnUploadPoster);
        imgPosterPreview = view.findViewById(R.id.imgPosterPreview);
        btnSubmit = view.findViewById(R.id.btnCreateEventSubmit);
        btnBack = view.findViewById(R.id.btnBack);

        if (btnBack != null) {
            btnBack.setOnClickListener(v ->
                    requireActivity().getSupportFragmentManager().popBackStack()
            );
        }

        if (btnSignupStart != null) {
            btnSignupStart.setOnClickListener(v -> showDatePicker(true));
        }
        if (btnSignupEnd != null) {
            btnSignupEnd.setOnClickListener(v -> showDatePicker(false));
        }

        if (btnUploadPoster != null) {
            btnUploadPoster.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        }

        loadOrganizerInfo();

        if (btnSubmit != null) {
            btnSubmit.setOnClickListener(v -> createEvent());
        }
    }

    private void showDatePicker(boolean isStart) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(
                getContext(),
                (view, y, m, d) -> {
                    String date = d + "/" + (m + 1) + "/" + y;
                    Calendar picked = Calendar.getInstance();
                    picked.set(Calendar.YEAR, y);
                    picked.set(Calendar.MONTH, m);
                    picked.set(Calendar.DAY_OF_MONTH, d);
                    picked.set(Calendar.HOUR_OF_DAY, 0);
                    picked.set(Calendar.MINUTE, 0);
                    picked.set(Calendar.SECOND, 0);
                    picked.set(Calendar.MILLISECOND, 0);
                    long millis = picked.getTimeInMillis();

                    if (isStart) {
                        startDate = date;
                        signupStartUtc = millis;
                        Toast.makeText(getContext(), "Signup Start: " + date, Toast.LENGTH_SHORT).show();
                    } else {
                        endDate = date;
                        signupEndUtc = millis;
                        Toast.makeText(getContext(), "Signup End: " + date, Toast.LENGTH_SHORT).show();
                    }
                },
                year,
                month,
                day
        ).show();
    }

    private void loadOrganizerInfo() {
        db.collection("Organizers").document(deviceId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String email = doc.getString("Organizer_email");
                        if (email != null && !email.isEmpty()) {
                            organizerEmail = email;
                        }
                    } else {
                        Map<String, Object> org = new HashMap<>();
                        org.put("Organizer_id", deviceId);
                        org.put("Organizer_email", organizerEmail);
                        db.collection("Organizers")
                                .document(deviceId)
                                .set(org, SetOptions.merge());
                    }
                });
    }

    private void createEvent() {
        String title = safeText(inputEventName);
        String desc = safeText(inputEventDescription);
        String loc = safeText(inputEventLocation);
        String cat = safeText(inputEventCategory);
        String capStr = safeText(inputEventCapacity);

        if (title.isEmpty() || loc.isEmpty() || capStr.isEmpty()) {
            Toast.makeText(getContext(), "Please fill required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int cap;
        try {
            cap = Integer.parseInt(capStr);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Invalid capacity", Toast.LENGTH_SHORT).show();
            return;
        }

        String eventId = "EVT" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        Map<String, Object> event = new HashMap<>();
        event.put("Event_id", eventId);
        event.put("Event_title", title);
        event.put("Event_description", desc);
        event.put("Event_location", loc);
        event.put("Event_category", cat);
        event.put("Event_capacity", cap);

        event.put("Event_startDate", startDate);
        event.put("Event_endDate", endDate);

        event.put("Event_signupStartUtc", signupStartUtc);
        event.put("Event_signupEndUtc", signupEndUtc);

        //store the base64 poster string instead of the local uri
        //if posterBase64 is empty, just means there was no poster uploaded
        event.put("Event_posterBase64", posterBase64);

        event.put("Event_organizerEmail", organizerEmail);
        event.put("Event_status", "Open");
        event.put("Event_isActive", true);
        event.put("Event_isLotteryEnabled", true);

        WriteBatch batch = db.batch();
        batch.set(db.collection("Events").document(eventId), event);

        Map<String, Object> org = new HashMap<>();
        org.put("Organizer_id", deviceId);
        org.put("Organizer_email", organizerEmail);
        batch.set(db.collection("Organizers").document(deviceId), org, SetOptions.merge());
        batch.update(
                db.collection("Organizers").document(deviceId),
                "Organizer_createdEventIDs",
                FieldValue.arrayUnion(eventId)
        );

        batch.commit()
                .addOnSuccessListener(result -> {
                    Toast.makeText(getContext(), "Event created successfully!", Toast.LENGTH_SHORT).show();

                    // go to "event created" screen with this eventId
                    EventCreatedFragment eventCreatedFragment = EventCreatedFragment.newInstance(eventId);

                    getParentFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, eventCreatedFragment)
                            .addToBackStack(null)
                            .commit();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                getContext(),
                                "Failed: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()
                );

    }
    private String safeText(EditText et) {
        return et != null && et.getText() != null
                ? et.getText().toString().trim()
                : "";
    }
}
