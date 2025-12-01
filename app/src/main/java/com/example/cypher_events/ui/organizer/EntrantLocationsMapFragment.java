package com.example.cypher_events.ui.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cypher_events.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntrantLocationsMapFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_EVENT_ID = "EventId";
    
    private GoogleMap googleMap;
    private FirebaseFirestore db;
    private String eventId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_entrant_locations_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        eventId = getArguments() != null ? getArguments().getString(ARG_EVENT_ID) : null;

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> 
                requireActivity().getSupportFragmentManager().popBackStack());
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.map, mapFragment)
                    .commit();
        }
        
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        loadEntrantLocations();
    }

    private void loadEntrantLocations() {
        if (eventId == null) return;

        db.collection("Events").document(eventId).get()
                .addOnSuccessListener(eventDoc -> {
                    String actualEventId = eventDoc.getString("Event_id");
                    if (actualEventId == null) actualEventId = eventId;
                    
                    final String searchEventId = actualEventId;

                    db.collection("Entrants").get()
                            .addOnSuccessListener(querySnapshot -> {
                                List<EntrantLocation> locations = new ArrayList<>();
                                
                                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                    List<String> joinedIds = new ArrayList<>();
                                    Object joinedObj = doc.get("Entrant_joinedEventIDs");
                                    if (joinedObj instanceof List) {
                                        for (Object item : (List<?>) joinedObj) {
                                            if (item != null) joinedIds.add(item.toString());
                                        }
                                    } else if (joinedObj instanceof Map) {
                                        joinedIds.addAll(((Map<String, Object>) joinedObj).keySet());
                                    }

                                    if (joinedIds.contains(eventId) || joinedIds.contains(searchEventId)) {
                                        String name = doc.getString("Entrant_name");
                                        
                                        // For demo: random locations around Edmonton
                                        double lat = 53.5461 + (Math.random() - 0.5) * 0.1;
                                        double lng = -113.4938 + (Math.random() - 0.5) * 0.1;
                                        
                                        locations.add(new EntrantLocation(name, lat, lng));
                                    }
                                }

                                displayLocationsOnMap(locations);
                            });
                });
    }

    private void displayLocationsOnMap(List<EntrantLocation> locations) {
        if (googleMap == null || locations.isEmpty()) {
            Toast.makeText(getContext(), "No entrant locations to display", Toast.LENGTH_SHORT).show();
            return;
        }

        for (EntrantLocation loc : locations) {
            LatLng position = new LatLng(loc.lat, loc.lng);
            googleMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(loc.name != null ? loc.name : "Entrant"));
        }

        if (!locations.isEmpty()) {
            EntrantLocation first = locations.get(0);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(first.lat, first.lng), 11f));
        }

        Toast.makeText(getContext(), 
            "Showing " + locations.size() + " entrant location(s)", 
            Toast.LENGTH_SHORT).show();
    }

    private static class EntrantLocation {
        String name;
        double lat;
        double lng;

        EntrantLocation(String name, double lat, double lng) {
            this.name = name;
            this.lat = lat;
            this.lng = lng;
        }
    }
}
