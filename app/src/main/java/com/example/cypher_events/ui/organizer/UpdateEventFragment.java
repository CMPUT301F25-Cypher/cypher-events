package com.example.cypher_events.ui.organizer;

import android.graphics.Bitmap;
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
import com.example.cypher_events.util.ImageProcessor;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpdateEventFragment extends Fragment {

    private static final String ARG_EVENT_ID = "EventId";

    private EditText etEventName;
    private EditText etDescription;
    private EditText etLocation;
    private EditText etCategory;

    private Button btnSaveChanges;
    private Button btnChangePoster;
    private ImageButton btnBackUpdate;
    private ImageView imgPosterPreviewUpdate;

    private FirebaseFirestore db;
    private String eventId;

    //holds the base64 version of the poster currently associated with this event
    //this can come from firestore (existing image) or from a new selection by the user
    private String posterBase64 = "";

    //se this launcher to open the image picker when the user taps "update poster"
    //it will give back a Uri which we turn into a base64 string
    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    handleNewPosterSelection(uri);
                } else {
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
        return inflater.inflate(R.layout.fragment_update_event, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        //text fields
        etEventName = view.findViewById(R.id.etEventName);
        etDescription = view.findViewById(R.id.etDescription);
        etLocation = view.findViewById(R.id.etLocation);
        etCategory = view.findViewById(R.id.etCategory);

        //buttons and image view
        btnSaveChanges = view.findViewById(R.id.btnSaveChanges);
        btnBackUpdate = view.findViewById(R.id.btnBackUpdate);
        btnChangePoster = view.findViewById(R.id.btnChangePoster);
        imgPosterPreviewUpdate = view.findViewById(R.id.imgPosterPreviewUpdate);

        //get the event id that was passed into this fragment
        Bundle args = getArguments();
        eventId = (args != null) ? args.getString(ARG_EVENT_ID) : null;

        if (eventId == null || eventId.trim().isEmpty()) {
            Toast.makeText(getContext(), "No event selected.", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
            return;
        }

        //load current event data (including existing poster) from firestore
        loadEventDetails();

        //back button takes the organizer back to their events list
        if (btnBackUpdate != null) {
            btnBackUpdate.setOnClickListener(v ->
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.container, new MyEventsFragment())
                            .commit()
            );
        }

        //save button writes the text fields (and poster base64) back to firestore
        if (btnSaveChanges != null) {
            btnSaveChanges.setOnClickListener(v -> saveChanges());
        }

        //change poster button opens the system image picker
        if (btnChangePoster != null) {
            btnChangePoster.setOnClickListener(v -> {
                //this will show a "choose image" dialog (gallery &files, etc.)
                imagePickerLauncher.launch("image/*");
            });
        }
    }

    /**
     * Reads the event document from firestore to fill in the text fields and
     * show the existing poster (if one is stored) */
    private void loadEventDetails() {
        db.collection("Events").document(eventId).get()
                .addOnSuccessListener(this::populateFields)
                .addOnFailureListener(e ->
                        Toast.makeText(
                                getContext(),
                                "Failed to load event: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    /**
     * Populates the ui from the firestore document like title, description, location, category
     * and poster if there is an Event_posterBase64 field*/
    private void populateFields(DocumentSnapshot doc) {
        if (!doc.exists()) {
            Toast.makeText(getContext(), "Event not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = doc.getString("Event_title");
        String desc = doc.getString("Event_description");
        String loc = doc.getString("Event_location");
        String cat = doc.getString("Event_category");

        //read any stored poster base64 so we can keep it unless the user changes it
        String existingPosterBase64 = doc.getString("Event_posterBase64");
        if (existingPosterBase64 != null && !existingPosterBase64.isEmpty()) {
            posterBase64 = existingPosterBase64;

            //decode the base64 text into a bitmap and show it in the preview image view using
            //our Image processor
            Bitmap posterBitmap = ImageProcessor.base64ToBitmap(existingPosterBase64);
            if (posterBitmap != null && imgPosterPreviewUpdate != null) {
                imgPosterPreviewUpdate.setImageBitmap(posterBitmap);
            }
        }

        etEventName.setText(name != null ? name : "");
        etDescription.setText(desc != null ? desc : "");
        etLocation.setText(loc != null ? loc : "");
        etCategory.setText(cat != null ? cat : "");
    }
    /**
     * Called when the user picks a new poster image from the system picker.
     * It shows a local preview of the chosen imagea and converts it to compressed base64
     * using our ImageProcessor helper class
     * It then stores the new base64 string so it can be saved to firestore
     */
    private void handleNewPosterSelection(Uri imageUri) {
        if (imgPosterPreviewUpdate != null) {
            // show a direct preview from the uri right away (fast feedback for the user)
            imgPosterPreviewUpdate.setImageURI(imageUri);
        }

        // now do the heavier work: compress and encode as base64 text
        String resultBase64 = ImageProcessor.uriToBase64(requireContext(), imageUri);

        if (resultBase64 != null && !resultBase64.isEmpty()) {
            // remember the new base64 string so we can include it when saving
            posterBase64 = resultBase64;
            Toast.makeText(getContext(), "Poster updated.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(
                    getContext(),
                    "Could not process selected image.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
    /**
     * Reads fields from the ui and updates the firestore document.
     * Also writes the current posterBase64 value, which might be the original value from
     * firestore (if the user did not change poster) or a new value from handleNewPosterSelection
     * (if the user did chose a new poster)
     */
    private void saveChanges() {
        String name = etEventName.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String loc = etLocation.getText().toString().trim();
        String cat = etCategory.getText().toString().trim();

        if (name.isEmpty() || desc.isEmpty() || loc.isEmpty() || cat.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all details.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("Event_title", name);
        updates.put("Event_description", desc);
        updates.put("Event_location", loc);
        updates.put("Event_category", cat);
        //include the poster base64 in the updates:
        //if the user never changed the poster, this will be the existing value from firestore
        //if they picked a new image, this will be the new base64
        //if there was no poster and they still did not choose one, this will be ""
        updates.put("Event_posterBase64", posterBase64);

        db.collection("Events").document(eventId)
                .update(updates)
                .addOnSuccessListener(a -> {
                    Toast.makeText(
                            getContext(),
                            "Changes saved successfully!",
                            Toast.LENGTH_SHORT
                    ).show();

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.container, new MyEventsFragment())
                            .commit();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                getContext(),
                                "Failed to save changes: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }
}

