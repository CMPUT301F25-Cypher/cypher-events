package com.example.cypher_events.ui.organizer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cypher_events.R;
import com.example.cypher_events.util.ImageProcessor;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import androidx.core.content.FileProvider;

public class EventManagementFragment extends Fragment {

    private static final String ARG_EVENT_ID = "EventId";

    private TextView tvEventTitle;
    private TextView tvEventDescription;
    private ImageView imgEventPoster;
    private TextView tvOrganizerName;
    private TextView tvOrganizerPhone;
    private TextView tvOrganizerEmail;

    private Button btnGenerateQR;
    private Button btnUpdateEvent;
    private Button btnDrawWinner;
    private Button btnDrawReplacement;
    private Button btnExportCSV;
    private Button btnViewEntrantMap;
    private ImageButton btnBack;

    private List<WaitingListAdapter.EntrantItem> currentEntrantList = new ArrayList<>();

    private RecyclerView rvWaitingList;
    private TextView tvNoWaitingList;

    private TextView tvOrganizerName;
    private TextView tvOrganizerPhone;
    private TextView tvOrganizerEmail;
    private WaitingListAdapter waitingListAdapter;

    private FirebaseFirestore db;
    private String eventId;
    private String deviceId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_detail_organizer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        deviceId = android.provider.Settings.Secure.getString(
                requireContext().getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID
        );

        tvEventTitle = view.findViewById(R.id.tvEventTitle);
        tvEventDescription = view.findViewById(R.id.tvEventDescription);
        imgEventPoster = view.findViewById(R.id.imgEventPoster);
        tvOrganizerName = view.findViewById(R.id.tvOrganizerName);
        tvOrganizerPhone = view.findViewById(R.id.tvOrganizerPhone);
        tvOrganizerEmail = view.findViewById(R.id.tvOrganizerEmail);

        btnGenerateQR = view.findViewById(R.id.btnGenerateQR);
        btnUpdateEvent = view.findViewById(R.id.btnUpdateEvent);
        btnDrawWinner = view.findViewById(R.id.btnDrawWinner);
        btnDrawReplacement = view.findViewById(R.id.btnDrawReplacement);
        btnExportCSV = view.findViewById(R.id.btnExportCSV);
        btnViewEntrantMap = view.findViewById(R.id.btnViewEntrantMap);
        btnBack = view.findViewById(R.id.btnBack);

        tvOrganizerName = view.findViewById(R.id.tvOrganizerName);
        tvOrganizerPhone = view.findViewById(R.id.tvOrganizerPhone);
        tvOrganizerEmail = view.findViewById(R.id.tvOrganizerEmail);

        rvWaitingList = view.findViewById(R.id.rvWaitingList);
        tvNoWaitingList = view.findViewById(R.id.tvNoWaitingList);

        waitingListAdapter = new WaitingListAdapter();
        rvWaitingList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvWaitingList.setAdapter(waitingListAdapter);

        eventId = getArguments() != null ? getArguments().getString(ARG_EVENT_ID) : null;

        if (eventId == null || eventId.trim().isEmpty()) {
            Toast.makeText(getContext(), "No event selected.", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
            return;
        }

        loadEventDetails();
        loadWaitingList();

        btnGenerateQR.setOnClickListener(v -> openGenerateQR());
        btnUpdateEvent.setOnClickListener(v -> openUpdateEvent());
        btnDrawWinner.setOnClickListener(v -> openDrawWinner());
        btnDrawReplacement.setOnClickListener(v -> openDrawReplacement());
        btnExportCSV.setOnClickListener(v -> exportEntrantListToCSV());
        btnViewEntrantMap.setOnClickListener(v -> openEntrantLocationsMap());
    }

    private void openEntrantLocationsMap() {
        Bundle b = new Bundle();
        b.putString(ARG_EVENT_ID, eventId);
        EntrantLocationsMapFragment f = new EntrantLocationsMapFragment();
        f.setArguments(b);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, f)
                .addToBackStack(null)
                .commit();
    }

    private void loadEventDetails() {
        db.collection("Events").document(eventId).get()
                .addOnSuccessListener(this::handleEventLoaded)
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Failed to load event: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }

    private void handleEventLoaded(DocumentSnapshot doc) {
        if (!doc.exists()) {
            Toast.makeText(getContext(), "Event not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = doc.getString("Event_title");
        String description = doc.getString("Event_description");
        String posterBase64 = doc.getString("Event_posterBase64");
        String organizerEmail = doc.getString("Event_organizerEmail");

        String organizerEmail = doc.getString("Event_organizerEmail");
        String organizerName = doc.getString("Event_organizerName");
        String OrganizerPhone = doc.getString("Event_organizerPhone");

        tvOrganizerName.setText("Organizer: " + (organizerName != null ? organizerName : "Unknown"));
        tvOrganizerPhone.setText("Phone: " + (OrganizerPhone != null ? OrganizerPhone : "N/A"));
        tvOrganizerEmail.setText("Email: " + (organizerEmail != null ? organizerEmail : "N/A"));

        tvEventTitle.setText(title != null ? title : "Event");

        tvEventDescription.setText(
                description != null && !description.isEmpty()
                        ? description : "No description available."
        );

        if (posterBase64 != null && !posterBase64.isEmpty()) {
            Bitmap bmp = ImageProcessor.base64ToBitmap(posterBase64);
            if (bmp != null) imgEventPoster.setImageBitmap(bmp);
        }

        // Load organizer information
        if (organizerEmail != null && !organizerEmail.isEmpty()) {
            loadOrganizerInfo(organizerEmail);
        } else {
            // Fallback: load current user's organizer info
            loadCurrentOrganizerInfo();
        }
    }

    private void loadOrganizerInfo(String email) {
        db.collection("Organizers")
                .whereEqualTo("Organizer_email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        displayOrganizerInfo(doc);
                    } else {
                        loadCurrentOrganizerInfo();
                    }
                })
                .addOnFailureListener(e -> loadCurrentOrganizerInfo());
    }

    private void loadCurrentOrganizerInfo() {
        db.collection("Organizers").document(deviceId).get()
                .addOnSuccessListener(this::displayOrganizerInfo)
                .addOnFailureListener(e -> {
                    tvOrganizerName.setText("Name: Unknown");
                    tvOrganizerPhone.setText("Phone: N/A");
                    tvOrganizerEmail.setText("Email: N/A");
                });
    }

    private void displayOrganizerInfo(DocumentSnapshot doc) {
        if (doc.exists()) {
            String name = doc.getString("Organizer_name");
            String phone = doc.getString("Organizer_phone");
            String email = doc.getString("Organizer_email");

            tvOrganizerName.setText("Name: " + (name != null && !name.isEmpty() ? name : "Not set"));
            tvOrganizerPhone.setText("Phone: " + (phone != null && !phone.isEmpty() ? phone : "Not set"));
            tvOrganizerEmail.setText("Email: " + (email != null && !email.isEmpty() ? email : "Not set"));
        } else {
            tvOrganizerName.setText("Name: Unknown");
            tvOrganizerPhone.setText("Phone: N/A");
            tvOrganizerEmail.setText("Email: N/A");
        }
    }

    private void openDrawWinner() {
        Bundle b = new Bundle();
        b.putString(ARG_EVENT_ID, eventId);
        DrawWinnerFragment f = new DrawWinnerFragment();
        f.setArguments(b);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, f)
                .addToBackStack(null)
                .commit();
    }

    private void openDrawReplacement() {
        Bundle b = new Bundle();
        b.putString(ARG_EVENT_ID, eventId);
        DrawReplacementFragment f = new DrawReplacementFragment();
        f.setArguments(b);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, f)
                .addToBackStack(null)
                .commit();
    }

    private void openGenerateQR() {
        Bundle b = new Bundle();
        b.putString(ARG_EVENT_ID, eventId);
        GenerateQRFragment f = new GenerateQRFragment();
        f.setArguments(b);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, f)
                .addToBackStack(null)
                .commit();
    }

    private void openUpdateEvent() {
        Bundle b = new Bundle();
        b.putString(ARG_EVENT_ID, eventId);
        UpdateEventFragment f = new UpdateEventFragment();
        f.setArguments(b);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, f)
                .addToBackStack(null)
                .commit();
    }

    private void loadWaitingList() {
        db.collection("Entrants").get()
                .addOnSuccessListener(query -> {
                    List<WaitingListAdapter.EntrantItem> result = new ArrayList<>();

                    for (DocumentSnapshot doc : query.getDocuments()) {

                        Object raw = doc.get("Entrant_joinedEventIDs");
                        List<String> joined = new ArrayList<>();

                        if (raw instanceof List<?>) {
                            for (Object o : (List<?>) raw) {
                                if (o != null) joined.add(o.toString());
                            }
                        } else if (raw instanceof Map<?, ?>) {
                            Map<?, ?> map = (Map<?, ?>) raw;
                            for (Object key : map.keySet()) {
                                Object value = map.get(key);
                                if (value != null) joined.add(value.toString());
                            }
                        }

                        if (joined.contains(eventId)) {
                            WaitingListAdapter.EntrantItem item =
                                    new WaitingListAdapter.EntrantItem();
                            item.name = doc.getString("Entrant_name");
                            item.email = doc.getString("Entrant_email");
                            result.add(item);
                        }
                    }

                    currentEntrantList = result; // Save for CSV export

                    if (result.isEmpty()) {
                        rvWaitingList.setVisibility(View.GONE);
                        tvNoWaitingList.setVisibility(View.VISIBLE);
                    } else {
                        rvWaitingList.setVisibility(View.VISIBLE);
                        tvNoWaitingList.setVisibility(View.GONE);
                        waitingListAdapter.setData(result);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Failed to load waitlist: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }

    private void exportEntrantListToCSV() {
        if (currentEntrantList == null || currentEntrantList.isEmpty()) {
            Toast.makeText(getContext(), "No entrants to export", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Create filename with timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                    .format(new Date());
            String filename = "entrants_" + eventId + "_" + timestamp + ".csv";

            // Use app-specific directory (no permission needed on Android 10+)
            File downloadsDir = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "");
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }

            File csvFile = new File(downloadsDir, filename);
            FileWriter writer = new FileWriter(csvFile);

            // Write CSV header
            writer.append("Name,Email\n");

            // Write entrant data
            for (WaitingListAdapter.EntrantItem entrant : currentEntrantList) {
                writer.append(escapeCSV(entrant.name != null ? entrant.name : "N/A"));
                writer.append(",");
                writer.append(escapeCSV(entrant.email != null ? entrant.email : "N/A"));
                writer.append("\n");
            }

            writer.flush();
            writer.close();

            // Show success message and offer to open/share the file
            Toast.makeText(getContext(),
                    "CSV exported: " + csvFile.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();

            // Open the file with a file manager or share it
            shareCSVFile(csvFile);

        } catch (IOException e) {
            Toast.makeText(getContext(),
                    "Failed to export CSV: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        // Escape quotes and wrap in quotes if contains comma or quote
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private void shareCSVFile(File file) {
        try {
            Uri fileUri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".fileprovider",
                    file
            );

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/csv");
            intent.putExtra(Intent.EXTRA_STREAM, fileUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Share CSV file"));
        } catch (Exception e) {
            android.util.Log.e("CSV Export", "Error sharing file", e);
            Toast.makeText(getContext(),
                    "File saved but couldn't open share dialog",
                    Toast.LENGTH_SHORT).show();
        }
    }
}