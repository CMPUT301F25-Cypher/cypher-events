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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cypher_events.R;
import com.example.cypher_events.util.ImageProcessor;
import com.google.android.material.textfield.TextInputEditText;
import android.location.Address;
import android.location.Geocoder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateEventFragment extends Fragment {

    private FirebaseFirestore db;
    private String deviceId;

    private String organizerEmail = "";
    private String organizerName = "";
    private String organizerPhone = "";

    private TextInputEditText inputEventName;
    private TextInputEditText inputEventDescription;
    private TextInputEditText inputEventLocation;
    private TextInputEditText inputEventCategory;
    private TextInputEditText inputEventCapacity;

    private Button btnSignupStart;
    private Button btnSignupEnd;
    private Button btnEventDate;
    private Button btnUploadPoster;
    private Button btnSubmit;

    private ImageButton btnBack;
    private ImageView imgPosterPreview;

    private String startDate = "";
    private String endDate = "";
    private String eventDate = "";
    private long signupStartUtc = 0L;
    private long signupEndUtc = 0L;
    private long eventDateUtc = 0L;

    private Uri selectedImageUri = null;
    private String posterBase64 = "";

    //  BOTTOM NAV + TOP BAR CONTROL

    @Override
    public void onResume() {
        super.onResume();
        View bar = requireActivity().findViewById(R.id.layoutSearchRow);
        if (bar != null) bar.setVisibility(View.GONE);

        View bottomNav = requireActivity().findViewById(R.id.bottomNav);
        if (bottomNav != null) bottomNav.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        View bar = requireActivity().findViewById(R.id.layoutSearchRow);
        if (bar != null) bar.setVisibility(View.VISIBLE);
    }

    // IMAGE PICKER

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;

                    if (imgPosterPreview != null) {
                        imgPosterPreview.setImageURI(uri);
                    }

                    String base64 = ImageProcessor.uriToBase64(requireContext(), uri);
                    posterBase64 = base64 != null ? base64 : "";

                    Toast.makeText(getContext(), "Poster selected!", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_event, container, false);
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

        // Bind views
        inputEventName = view.findViewById(R.id.inputEventName);
        inputEventDescription = view.findViewById(R.id.inputEventDescription);
        inputEventLocation = view.findViewById(R.id.inputEventLocation);
        inputEventCategory = view.findViewById(R.id.inputEventCategory);
        inputEventCapacity = view.findViewById(R.id.inputEventCapacity);

        btnSignupStart = view.findViewById(R.id.btnSelectStartDate);
        btnSignupEnd = view.findViewById(R.id.btnSelectEndDate);
        btnEventDate = view.findViewById(R.id.btnSelectEventDate);
        btnUploadPoster = view.findViewById(R.id.btnUploadPoster);
        btnSubmit = view.findViewById(R.id.btnCreateEventSubmit);
        imgPosterPreview = view.findViewById(R.id.imgPosterPreview);

        btnBack = view.findViewById(R.id.btnBack);

        // FIX: AVOID NULL BUTTON CRASH
        if (btnBack != null) {
            btnBack.setOnClickListener(v ->
                    requireActivity().getSupportFragmentManager().popBackStack()
            );
        }

        btnSignupStart.setOnClickListener(v -> showDatePicker("signup_start"));
        btnSignupEnd.setOnClickListener(v -> showDatePicker("signup_end"));
        btnEventDate.setOnClickListener(v -> showDatePicker("event_date"));
        btnUploadPoster.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        btnSubmit.setOnClickListener(v -> createEvent());

        loadOrganizerInfo();
    }

    //  DATE PICKER

    private void showDatePicker(String type) {
        Calendar c = Calendar.getInstance();

        new DatePickerDialog(
                getContext(),
                (view, y, m, d) -> {
                    String date = d + "/" + (m + 1) + "/" + y;

                    Calendar picked = Calendar.getInstance();
                    picked.set(y, m, d, 0, 0, 0);

                    long millis = picked.getTimeInMillis();

                    switch (type) {
                        case "signup_start":
                            startDate = date;
                            signupStartUtc = millis;
                            Toast.makeText(getContext(), "Signup Start: " + date, Toast.LENGTH_SHORT).show();
                            break;
                        case "signup_end":
                            endDate = date;
                            signupEndUtc = millis;
                            Toast.makeText(getContext(), "Signup End: " + date, Toast.LENGTH_SHORT).show();
                            break;
                        case "event_date":
                            eventDate = date;
                            eventDateUtc = millis;
                            Toast.makeText(getContext(), "Event Date: " + date, Toast.LENGTH_SHORT).show();
                            break;
                    }
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    // LOAD ORGANIZER INFO
    private void loadOrganizerInfo() {

        // Holder for Entrant values so they can be used in lambdas
        class Holder {
            String email = "";
            String name = "";
            String phone = "";
        }

        Holder h = new Holder();

        // STEP 1 — Get Entrant profile first
        db.collection("Entrants").document(deviceId).get()
                .addOnSuccessListener(entrantDoc -> {

                    h.email = safe(entrantDoc.getString("Entrant_email"));
                    h.name  = safe(entrantDoc.getString("Entrant_name"));
                    h.phone = safe(entrantDoc.getString("Entrant_phone"));

                    // STEP 2 — Check if Organizer exists
                    db.collection("Organizers").document(deviceId).get()
                            .addOnSuccessListener(orgDoc -> {

                                if (orgDoc.exists()) {
                                    organizerEmail = safe(orgDoc.getString("Organizer_email"));
                                    organizerName  = safe(orgDoc.getString("Organizer_name"));
                                    organizerPhone = safe(orgDoc.getString("Organizer_phone"));
                                    return;
                                }

                                // STEP 3 — Organizer missing → create it from Entrant
                                Map<String, Object> org = new HashMap<>();
                                org.put("Organizer_id", deviceId);
                                org.put("Organizer_email", h.email);
                                org.put("Organizer_name",  h.name);
                                org.put("Organizer_phone", h.phone);
                                org.put("Organizer_notificationsEnabled", true);
                                org.put("Organizer_createdEventIDs", new ArrayList<String>());

                                db.collection("Organizers")
                                        .document(deviceId)
                                        .set(org)
                                        .addOnSuccessListener(v -> {
                                            organizerEmail = h.email;
                                            organizerName  = h.name;
                                            organizerPhone = h.phone;
                                        });
                            });
                });
    }






    // CREATE EVENT

    private void createEvent() {

        String title = safeText(inputEventName);
        String desc = safeText(inputEventDescription);
        String loc = safeText(inputEventLocation);
        String cat = safeText(inputEventCategory);
        String capStr = safeText(inputEventCapacity);

        if (title.isEmpty() || loc.isEmpty() || capStr.isEmpty()) {
            Toast.makeText(getContext(), "Fill required fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        int cap = Integer.parseInt(capStr);

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
        event.put("Event_dateUtc", eventDateUtc);
        event.put("Event_posterBase64", posterBase64);
        event.put("Event_organizerEmail", organizerEmail);
        event.put("Event_organizerName", organizerName);
        event.put("Event_organizerPhone", organizerPhone);
        event.put("Event_organizerId", deviceId);
        event.put("Event_status", "Open");
        event.put("Event_isActive", true);
        event.put("Event_isLotteryEnabled", true);

        // Geocode location first, then save event
        Toast.makeText(getContext(), "Getting location coordinates...", Toast.LENGTH_SHORT).show();
        geocodeLocationAndSave(loc, eventId, event);
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }


    private String safeText(EditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private void geocodeLocationAndSave(String location, String eventId, Map<String, Object> event) {
        new Thread(() -> {
            double lat = 0.0;
            double lng = 0.0;
            boolean geocodingSuccess = false;
            
            if (location != null && !location.isEmpty()) {
                try {
                    Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                    
                    // Check if Geocoder is available
                    if (!Geocoder.isPresent()) {
                        android.util.Log.e("Geocoding", "Geocoder not available on this device");
                    } else {
                        List<Address> addresses = geocoder.getFromLocationName(location, 1);
                        
                        if (addresses != null && !addresses.isEmpty()) {
                            Address address = addresses.get(0);
                            lat = address.getLatitude();
                            lng = address.getLongitude();
                            geocodingSuccess = true;
                            android.util.Log.d("Geocoding", "Got coordinates: " + lat + ", " + lng);
                        } else {
                            android.util.Log.w("Geocoding", "No addresses found for: " + location);
                        }
                    }
                } catch (IOException e) {
                    android.util.Log.e("Geocoding", "Geocoding failed: " + e.getMessage(), e);
                } catch (Exception e) {
                    android.util.Log.e("Geocoding", "Unexpected error: " + e.getMessage(), e);
                }
            }
            
            // Add coordinates to event
            final double finalLat = lat;
            final double finalLng = lng;
            final boolean finalSuccess = geocodingSuccess;
            
            requireActivity().runOnUiThread(() -> {
                event.put("Event_lat", finalLat);
                event.put("Event_lng", finalLng);
                
                if (!finalSuccess) {
                    Toast.makeText(getContext(), 
                        "Note: Could not find location coordinates", 
                        Toast.LENGTH_SHORT).show();
                }
                
                // Now save the event with coordinates
                saveEventToFirestore(eventId, event);
            });
        }).start();
    }
    
    private void saveEventToFirestore(String eventId, Map<String, Object> event) {
        WriteBatch batch = db.batch();

        batch.set(db.collection("Events").document(eventId), event);

        Map<String, Object> org = new HashMap<>();
        org.put("Organizer_id", deviceId);
        org.put("Organizer_email", organizerEmail);

        batch.set(db.collection("Organizers").document(deviceId), org, SetOptions.merge());
        batch.update(db.collection("Organizers").document(deviceId),
                "Organizer_createdEventIDs",
                FieldValue.arrayUnion(eventId));

        batch.commit()
                .addOnSuccessListener(r -> {
                    Toast.makeText(getContext(), "Event created!", Toast.LENGTH_SHORT).show();

                    // >>> Go to My Events screen
                    getParentFragmentManager()
                            .beginTransaction()
                            .replace(R.id.homeContentContainer, new MyEventsFragment())
                            .commit();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
