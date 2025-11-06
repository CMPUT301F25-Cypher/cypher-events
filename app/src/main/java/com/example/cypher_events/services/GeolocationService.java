package com.example.cypher_events.services;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.GeoPoint;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Service for handling geolocation operations
 * Implements US 02.02.02 - View entrants on map
 * Implements US 02.02.03 - Enable/disable geolocation requirement
 *
 * Outstanding issues: Need to handle location permission denials gracefully
 */
public class GeolocationService {

    private Context context;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    public GeolocationService(Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    /**
     * Checks if location permissions are granted
     */
    public boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests location permissions from user
     */
    public void requestLocationPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    /**
     * Gets the current device location
     */
    public void getCurrentLocation(LocationCallback callback) {
        if (!hasLocationPermission()) {
            callback.onFailure("Location permission not granted");
            return;
        }

        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                            callback.onSuccess(geoPoint);
                        } else {
                            callback.onFailure("Unable to get location");
                        }
                    })
                    .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
        } catch (SecurityException e) {
            callback.onFailure("Location permission denied");
        }
    }

    /**
     * Converts GeoPoint to human-readable address
     */
    public String getAddressFromLocation(GeoPoint geoPoint) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(
                    geoPoint.getLatitude(),
                    geoPoint.getLongitude(),
                    1
            );

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder addressText = new StringBuilder();

                // Build address string
                if (address.getLocality() != null) {
                    addressText.append(address.getLocality()).append(", ");
                }
                if (address.getAdminArea() != null) {
                    addressText.append(address.getAdminArea()).append(", ");
                }
                if (address.getCountryName() != null) {
                    addressText.append(address.getCountryName());
                }

                return addressText.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Unknown location";
    }

    /**
     * Callback interface for location retrieval
     */
    public interface LocationCallback {
        void onSuccess(GeoPoint location);
        void onFailure(String error);
    }
}