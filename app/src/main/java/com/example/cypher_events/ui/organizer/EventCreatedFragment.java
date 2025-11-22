package com.example.cypher_events.ui.organizer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.cypher_events.R;
import com.example.cypher_events.util.QRCodeHelper;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Screen shown after an event is created.
 * Receives an eventId, loads that event from Firestore, shows the event name and description
 * Also Generates and displays a QR code for that event
 */
public class EventCreatedFragment extends Fragment {

    // key to pass event id into this fragment
    private static final String eventIdKey = "eventId";
    private FirebaseFirestore firestore;
    private String eventId;
    private TextView eventCreatedTitleText;
    private TextView eventCreatedSubtitleText;
    private TextView eventNameText;
    private TextView eventDescriptionText;
    private ImageView eventQrImageView;
    private Button seeDetailsButton;
    private Button backToDashboardButton;

    /** create a new EventCreatedFragment with and passes eventId  */
    public static EventCreatedFragment newInstance(String eventId) {
        EventCreatedFragment fragment = new EventCreatedFragment();
        Bundle args = new Bundle();
        args.putString(eventIdKey, eventId);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            eventId = getArguments().getString(eventIdKey);
        }
    }
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_event_created, container, false);
    }
    @Override
    public void onViewCreated(
            @NonNull View root,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(root, savedInstanceState);
        //views
        eventCreatedTitleText = root.findViewById(R.id.EventCreatedTitle);
        eventCreatedSubtitleText = root.findViewById(R.id.EventCreatedSubtitle);
        eventNameText = root.findViewById(R.id.EventName);
        eventDescriptionText = root.findViewById(R.id.EventDescript);
        eventQrImageView = root.findViewById(R.id.EventQr);
        seeDetailsButton = root.findViewById(R.id.buttonViewEventDetails);
        backToDashboardButton = root.findViewById(R.id.buttonReturnToDashboard);

        if (eventId==null||eventId.trim().isEmpty()) {
            Toast.makeText(getContext(), "No event ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        pullEventFromFirestore();
        showEventQrCode();

        //listeners for buttons
        seeDetailsButton.setOnClickListener(view -> {
            Toast.makeText(getContext(), "Not created yet", Toast.LENGTH_SHORT).show();
        });

        backToDashboardButton.setOnClickListener(view -> {
            OrganizerDashboardFragment dashboardFragment = new OrganizerDashboardFragment();

            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, dashboardFragment)
                    .commit();
        });
    }
    /** Pulls event document from Firestore using eventId and updates name and description
     * on the screen.*/
    private void pullEventFromFirestore() {
        firestore.collection("Events")
                .document(eventId)
                .get()
                .addOnSuccessListener(this::handleEventLoaded)
                .addOnFailureListener(error -> {
                    Toast.makeText(getContext(), "Failed to load event: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void handleEventLoaded(DocumentSnapshot snapshot) {
        if (!snapshot.exists()) {
            Toast.makeText(getContext(), "Event not found.", Toast.LENGTH_SHORT).show();
            return;
        }
        String titleFromDb = snapshot.getString("Event_title");
        String descriptionFromDb = snapshot.getString("Event_description");
        if (titleFromDb!=null &&!titleFromDb.isEmpty()) {
            eventNameText.setText(titleFromDb);
        }
        if (descriptionFromDb!=null && !descriptionFromDb.isEmpty()) {
            eventDescriptionText.setText(descriptionFromDb);
        }
    }
    /** Uses the QRCodeHelper to generate and display the QR code for this event.*/
    private void showEventQrCode() {
        Bitmap qrBitmap = QRCodeHelper.generateEventQr(eventId);
        if (qrBitmap != null) {
            eventQrImageView.setImageBitmap(qrBitmap);
        } else {
            Toast.makeText(getContext(), "QR code generation not successful.", Toast.LENGTH_SHORT).show();
        }
    }
}
