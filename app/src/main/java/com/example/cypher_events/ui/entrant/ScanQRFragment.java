package com.example.cypher_events.ui.entrant;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.cypher_events.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class ScanQRFragment extends Fragment {

    private static final String ARG_EVENT_ID = "EventId";
    
    private FirebaseFirestore db;
    private String deviceId;
    private Button btnScanQR;

    private final ActivityResultLauncher<ScanOptions> qrScanLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    String scannedData = result.getContents();
                    processQRCode(scannedData);
                } else {
                    toast("Scan cancelled");
                }
            }
    );

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new androidx.activity.result.contract.ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    startQRScanner();
                } else {
                    toast("Camera permission is required to scan QR codes");
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scan_qr, container, false);
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

        btnScanQR = view.findViewById(R.id.btnScanQR);

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> 
                requireActivity().getSupportFragmentManager().popBackStack()
            );
        }

        if (btnScanQR != null) {
            btnScanQR.setOnClickListener(v -> checkCameraPermissionAndScan());
        }
    }

    private void checkCameraPermissionAndScan() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startQRScanner();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void startQRScanner() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan event QR code");
        options.setCameraId(0);
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(false);
        options.setOrientationLocked(true);
        
        qrScanLauncher.launch(options);
    }

    private void processQRCode(String qrData) {
        String eventId = extractEventId(qrData);
        
        if (eventId == null || eventId.isEmpty()) {
            toast("Invalid QR code");
            return;
        }

        joinEventById(eventId);
    }

    private String extractEventId(String qrData) {
        if (qrData.contains("event_id=")) {
            return qrData.substring(qrData.indexOf("event_id=") + 9);
        }
        return qrData;
    }

    private void joinEventById(String eventId) {
        db.collection("Events").document(eventId).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        toast("Event not found");
                        return;
                    }

                    String eventTitle = doc.getString("Event_title");

                    //toast to confirm event was found
                    toast("Opening: " + (eventTitle != null ? eventTitle : "Event"));

                    //go straight to the event detail screen, but doesnt add to waitlist
                    Bundle b = new Bundle();
                    b.putString(ARG_EVENT_ID, eventId);
                    EventDetailEntrantFragment fragment = new EventDetailEntrantFragment();
                    fragment.setArguments(b);

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, fragment)
                            .addToBackStack(null)
                            .commit();
                })
                .addOnFailureListener(e -> toast("Error: " + e.getMessage()));
    }


    private void toast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
