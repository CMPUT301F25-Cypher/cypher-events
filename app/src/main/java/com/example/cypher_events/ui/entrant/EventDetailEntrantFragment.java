package com.example.cypher_events.ui.entrant;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cypher_events.R;
import com.example.cypher_events.util.ImageProcessor; //helper for base64 to bitmap
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EventDetailEntrantFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_EVENT_ID = "EventId";

    private FirebaseFirestore db;
    private String eventId;
    private String deviceId;
    private GoogleMap googleMap;
    private double eventLat = 0;
    private double eventLng = 0;

    private TextView tvEventTitle;
    private TextView tvEventDescription;
    private TextView tvEventLocation;
    private TextView tvSignupStart;
    private TextView tvSignupEnd;
    private TextView tvEventStatus;
    private TextView tvOrganizerName;
    private TextView tvOrganizerPhone;
    private TextView tvOrganizerEmail;
    private ImageView imgEventPoster;
    private Button btnJoinWaitlist;
    private Button btnAccept;
    private Button btnDecline;
    private LinearLayout layoutAcceptDecline;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_detail_entrant, container, false);
    }

    @SuppressLint("HardwareIds")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        deviceId = Settings.Secure.getString(
                requireContext().getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        tvEventTitle = view.findViewById(R.id.tvEventTitle);
        tvEventDescription = view.findViewById(R.id.tvEventDescription);
        tvEventLocation = view.findViewById(R.id.tvEventLocation);
        tvSignupStart = view.findViewById(R.id.tvSignupStart);
        tvSignupEnd = view.findViewById(R.id.tvSignupEnd);
        tvEventStatus = view.findViewById(R.id.tvEventStatus);
        tvOrganizerName = view.findViewById(R.id.tvOrganizerName);
        tvOrganizerPhone = view.findViewById(R.id.tvOrganizerPhone);
        tvOrganizerEmail = view.findViewById(R.id.tvOrganizerEmail);
        imgEventPoster = view.findViewById(R.id.imgEventPoster);
        btnJoinWaitlist = view.findViewById(R.id.btnJoinWaitlist);
        btnAccept = view.findViewById(R.id.btnAccept);
        btnDecline = view.findViewById(R.id.btnDecline);
        layoutAcceptDecline = view.findViewById(R.id.layoutAcceptDecline);

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        }

        Bundle args = getArguments();
        eventId = (args != null) ? args.getString(ARG_EVENT_ID) : null;

        if (eventId == null || eventId.trim().isEmpty()) {
            toast("No event selected");
            requireActivity().getSupportFragmentManager().popBackStack();
            return;
        }

        loadEventDetails();
        setupButtons();
        setupMap();
    }

    private void setupMap() {
        try {
            View view = getView();
            if (view == null) {
                android.util.Log.e("EventDetail", "View is null, cannot setup map");
                return;
            }
            
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.mapContainer);
            
            if (mapFragment == null) {
                mapFragment = SupportMapFragment.newInstance();
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mapContainer, mapFragment)
                        .commit();
            }
            
            mapFragment.getMapAsync(this);
        } catch (Exception e) {
            android.util.Log.e("EventDetail", "Error setting up map", e);
            e.printStackTrace();
            // Hide map container if there's an error
            try {
                View view = getView();
                if (view != null) {
                    View mapContainer = view.findViewById(R.id.mapContainer);
                    if (mapContainer != null) {
                        mapContainer.setVisibility(View.GONE);
                    }
                }
            } catch (Exception ex) {
                android.util.Log.e("EventDetail", "Error hiding map container", ex);
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        android.util.Log.d("EventDetail", "Map is ready");
        
        // If we already have coordinates, show them
        if (eventLat != 0 && eventLng != 0) {
            android.util.Log.d("EventDetail", "Showing location: " + eventLat + ", " + eventLng);
            showLocationOnMap();
        } else {
            android.util.Log.d("EventDetail", "No coordinates available yet");
        }
    }

    private void showLocationOnMap() {
        if (googleMap == null) {
            android.util.Log.d("EventDetail", "Map not ready yet");
            return;
        }
        
        if (eventLat == 0 && eventLng == 0) {
            android.util.Log.d("EventDetail", "No valid coordinates");
            // Hide map if no coordinates
            try {
                View view = getView();
                if (view != null) {
                    View mapContainer = view.findViewById(R.id.mapContainer);
                    if (mapContainer != null) {
                        mapContainer.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                android.util.Log.e("EventDetail", "Error hiding map", e);
            }
            return;
        }

        try {
            LatLng location = new LatLng(eventLat, eventLng);
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title("Event Location"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
            android.util.Log.d("EventDetail", "Map updated successfully");
        } catch (Exception e) {
            android.util.Log.e("EventDetail", "Error showing location on map", e);
        }
    }

    private void loadEventDetails() {
        db.collection("Events").document(eventId).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        toast("Event not found");
                        return;
                    }

                    String organizerEmail = doc.getString("Event_organizerEmail");
                    checkIfUserIsOrganizer(organizerEmail, () -> displayEventData(doc));
                })
                .addOnFailureListener(e -> toast("Failed to load event: " + e.getMessage()));
    }

    private void checkIfUserIsOrganizer(String eventOrganizerEmail, Runnable onNotOrganizer) {
        db.collection("Organizers").document(deviceId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String userEmail = doc.getString("Organizer_email");
                        if (userEmail != null && userEmail.equals(eventOrganizerEmail)) {
                            btnJoinWaitlist.setEnabled(false);
                            btnJoinWaitlist.setText("Cannot Join Own Event");
                            toast("You cannot join your own event");
                        }
                    }
                    onNotOrganizer.run();
                })
                .addOnFailureListener(e -> onNotOrganizer.run());
    }

    private void displayEventData(DocumentSnapshot doc) {
        if (!doc.exists()) {
            toast("Event not found");
            return;
        }

        String title = doc.getString("Event_title");
        String description = doc.getString("Event_description");
        String location = doc.getString("Event_location");
        String status = doc.getString("Event_status");
        Long signupStart = doc.getLong("Event_signupStartUtc");
        Long signupEnd = doc.getLong("Event_signupEndUtc");
        String organizerEmail = doc.getString("Event_organizerEmail");

        // load ans how poster from base64
        // read the poster base64 string that we saved when creating and updating the event
        String posterBase64 = doc.getString("Event_posterBase64");

        if (posterBase64 != null && !posterBase64.isEmpty()) {
            //convert base64 text back into a bitmap using helper ImageProcessor class
            Bitmap posterBitmap = ImageProcessor.base64ToBitmap(posterBase64);

            if (posterBitmap != null) {
                imgEventPoster.setImageBitmap(posterBitmap);
            } else {
                imgEventPoster.setImageResource(
                        EventAdapter.getPlaceholderForId(eventId)
                );
            }
        } else {
            // no poster → deterministic placeholder
            imgEventPoster.setImageResource(
                    EventAdapter.getPlaceholderForId(eventId)
            );
        }

        tvEventTitle.setText(title != null ? title : "Event Details");
        tvEventDescription.setText(description != null ? description : "No description available");
        tvEventLocation.setText("Location: " + (location != null ? location : "TBA"));
        tvEventStatus.setText("Status: " + (status != null ? status : "Unknown"));

        if (signupStart != null) {
            tvSignupStart.setText("Signup Starts: " + formatDate(signupStart));
        }
        if (signupEnd != null) {
            tvSignupEnd.setText("Signup Ends: " + formatDate(signupEnd));
        }

        if (organizerEmail != null) {
            loadOrganizerInfo(organizerEmail);
        }

        // Load coordinates for map
        Object latObj = doc.get("Event_lat");
        Object lngObj = doc.get("Event_lng");
        
        android.util.Log.d("EventDetail", "Lat object: " + latObj + ", Lng object: " + lngObj);
        
        if (latObj instanceof Number && lngObj instanceof Number) {
            eventLat = ((Number) latObj).doubleValue();
            eventLng = ((Number) lngObj).doubleValue();
            android.util.Log.d("EventDetail", "Loaded coordinates: " + eventLat + ", " + eventLng);
            showLocationOnMap();
        } else {
            android.util.Log.d("EventDetail", "No valid coordinates in Firestore");
        }

        checkEntrantStatus();
    }

    private void loadOrganizerInfo(String email) {
        db.collection("Organizers")
                .whereEqualTo("Organizer_email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        String name = doc.getString("Organizer_name");
                        String phone = doc.getString("Organizer_phone");
                        String orgEmail = doc.getString("Organizer_email");

                        tvOrganizerName.setText("Organizer: " + (name != null ? name : "Unknown"));
                        tvOrganizerPhone.setText("Phone: " + (phone != null ? phone : "N/A"));
                        tvOrganizerEmail.setText("Email: " + (orgEmail != null ? orgEmail : "N/A"));
                    }
                });
    }

    private void checkEntrantStatus() {
        db.collection("Entrants").document(deviceId).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        btnJoinWaitlist.setVisibility(View.VISIBLE);
                        layoutAcceptDecline.setVisibility(View.GONE);
                        return;
                    }

                    List<String> joinedIds = new ArrayList<>();
                    Object joinedObj = doc.get("Entrant_joinedEventIDs");
                    if (joinedObj instanceof List) {
                        joinedIds = (List<String>) joinedObj;
                    } else if (joinedObj instanceof Map) {
                        joinedIds.addAll(((Map<String, Object>) joinedObj).keySet());
                    }

                    List<String> selectedIds = new ArrayList<>();
                    Object selectedObj = doc.get("Entrant_selectedEventIDs");
                    if (selectedObj instanceof List) {
                        selectedIds = (List<String>) selectedObj;
                    } else if (selectedObj instanceof Map) {
                        selectedIds.addAll(((Map<String, Object>) selectedObj).keySet());
                    }

                    List<String> acceptedIds = new ArrayList<>();
                    Object acceptedObj = doc.get("Entrant_acceptedEventIDs");
                    if (acceptedObj instanceof List) {
                        acceptedIds = (List<String>) acceptedObj;
                    } else if (acceptedObj instanceof Map) {
                        acceptedIds.addAll(((Map<String, Object>) acceptedObj).keySet());
                    }

                    boolean hasJoined = joinedIds != null && joinedIds.contains(eventId);
                    boolean isSelected = selectedIds != null && selectedIds.contains(eventId);
                    boolean hasAccepted = acceptedIds != null && acceptedIds.contains(eventId);

                    if (hasAccepted) {
                        btnJoinWaitlist.setText("Already Accepted");
                        btnJoinWaitlist.setEnabled(false);
                        layoutAcceptDecline.setVisibility(View.GONE);

                    } else if (isSelected) {
                        btnJoinWaitlist.setVisibility(View.GONE);
                        layoutAcceptDecline.setVisibility(View.VISIBLE);

                    } else if (hasJoined) {
                        btnJoinWaitlist.setVisibility(View.VISIBLE);
                        btnJoinWaitlist.setEnabled(true);
                        btnJoinWaitlist.setText("Leave Waitlist");
                        layoutAcceptDecline.setVisibility(View.GONE);

                    } else {
                        btnJoinWaitlist.setVisibility(View.VISIBLE);
                        btnJoinWaitlist.setEnabled(true);
                        btnJoinWaitlist.setText("Join Waiting List");
                        layoutAcceptDecline.setVisibility(View.GONE);
                    }
                });
    }

    private void setupButtons() {
        btnJoinWaitlist.setOnClickListener(v -> {
            CharSequence text = btnJoinWaitlist.getText();
            if ("Leave Waitlist".contentEquals(text)) {
                leaveWaitlist();
            } else {
                joinWaitlist();
            }
        });

        btnAccept.setOnClickListener(v -> acceptInvitation());
        btnDecline.setOnClickListener(v -> declineInvitation());
    }

    private void joinWaitlist() {
        db.collection("Entrants").document(deviceId).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        toast("Please create a profile first");
                        return;
                    }

                    List<String> joinedIds = new ArrayList<>();
                    Object joinedObj = doc.get("Entrant_joinedEventIDs");
                    if (joinedObj instanceof List) {
                        joinedIds = (List<String>) joinedObj;
                    } else if (joinedObj instanceof Map) {
                        joinedIds.addAll(((Map<String, Object>) joinedObj).keySet());
                    }
                    
                    if (joinedIds != null && joinedIds.contains(eventId)) {
                        toast("Already on waitlist");
                        return;
                    }

                    db.collection("Entrants").document(deviceId)
                            .update("Entrant_joinedEventIDs", com.google.firebase.firestore.FieldValue.arrayUnion(eventId))
                            .addOnSuccessListener(aVoid -> {
                                toast("Joined waitlist successfully");
                                checkEntrantStatus();
                            })
                            .addOnFailureListener(e -> toast("Failed to join: " + e.getMessage()));
                });
    }

    private void leaveWaitlist() {
        db.collection("Entrants").document(deviceId)
                .update("Entrant_joinedEventIDs",
                        com.google.firebase.firestore.FieldValue.arrayRemove(eventId))
                .addOnSuccessListener(aVoid -> {
                    toast("Removed from waitlist");
                    checkEntrantStatus();
                })
                .addOnFailureListener(e -> toast("Failed to remove: " + e.getMessage()));
    }

    private void acceptInvitation() {
        db.collection("Entrants").document(deviceId)
                .update("Entrant_acceptedEventIDs", com.google.firebase.firestore.FieldValue.arrayUnion(eventId))
                .addOnSuccessListener(aVoid -> {
                    toast("Invitation accepted");
                    checkEntrantStatus();
                })
                .addOnFailureListener(e -> toast("Failed to accept: " + e.getMessage()));
    }

    private void declineInvitation() {
        // When declining, add to declined list AND remove from selected list
        // This keeps them on the waiting list for potential replacement draws
        Map<String, Object> updates = new HashMap<>();
        updates.put("Entrant_declinedEventIDs", com.google.firebase.firestore.FieldValue.arrayUnion(eventId));
        updates.put("Entrant_selectedEventIDs", com.google.firebase.firestore.FieldValue.arrayRemove(eventId));
        
        db.collection("Entrants").document(deviceId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    toast("Invitation declined");
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> toast("Failed to decline: " + e.getMessage()));
    }

    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    private void toast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
