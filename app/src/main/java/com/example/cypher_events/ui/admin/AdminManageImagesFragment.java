package com.example.cypher_events.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cypher_events.R;
import com.example.cypher_events.domain.model.Event;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class AdminManageImagesFragment extends Fragment {

    private static final String COLLECTION_EVENTS = "Events";
    private static final String FIELD_ID          = "Event_id";
    private static final String FIELD_TITLE       = "Event_title";
    private static final String FIELD_IMAGE_URL   = "Event_imageUrl";

    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private TextInputEditText etSearch;
    private ListView listView;
    private MaterialButton btnDelete;

    private final List<EventWithImage> allItems = new ArrayList<>();
    private final List<EventWithImage> filteredItems = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private int selectedIndex = -1;

    private static class EventWithImage {
        String eventId;
        String title;
        String imageUrl;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_admin_manage_images, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(
                    v -> requireActivity().getOnBackPressedDispatcher().onBackPressed()
            );
        }

        etSearch = view.findViewById(R.id.Image_search);
        listView = view.findViewById(R.id.Image_view);
        btnDelete = view.findViewById(R.id.btnDeleteSelectedImage);

        adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_single_choice,
                new ArrayList<>()
        );
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listView.setOnItemClickListener((parent, v, position, id) -> {
            selectedIndex = position;
        });

        btnDelete.setOnClickListener(v -> deleteSelectedImage());

        if (etSearch != null) {
            etSearch.setOnEditorActionListener((tv, actionId, event) -> {
                String q = tv.getText() != null ? tv.getText().toString().trim() : "";
                filterImages(q);
                return true;
            });
        }

        loadImages();
    }

    private void loadImages() {
        db.collection(COLLECTION_EVENTS)
                .get()
                .addOnSuccessListener(snaps -> {
                    allItems.clear();
                    for (QueryDocumentSnapshot doc : snaps) {
                        String url = doc.getString(FIELD_IMAGE_URL);
                        if (url == null || url.isEmpty()) {
                            continue; // no image for this event
                        }

                        EventWithImage item = new EventWithImage();
                        item.eventId = doc.contains(FIELD_ID)
                                ? doc.getString(FIELD_ID)
                                : doc.getId();
                        item.title = doc.getString(FIELD_TITLE);
                        if (item.title == null) item.title = "(Untitled event)";
                        item.imageUrl = url;

                        allItems.add(item);
                    }
                    filterImages(""); // load all
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(),
                                "Failed to load images: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }

    private void filterImages(String query) {
        filteredItems.clear();
        List<String> titles = new ArrayList<>();

        String q = query == null ? "" : query.toLowerCase();
        for (EventWithImage item : allItems) {
            if (q.isEmpty() ||
                    (item.title != null && item.title.toLowerCase().contains(q))) {
                filteredItems.add(item);
                titles.add(item.title);
            }
        }

        selectedIndex = -1;
        adapter.clear();
        adapter.addAll(titles);
        adapter.notifyDataSetChanged();
    }

    private void deleteSelectedImage() {
        if (selectedIndex < 0 || selectedIndex >= filteredItems.size()) {
            Toast.makeText(requireContext(),
                    "No image selected", Toast.LENGTH_SHORT).show();
            return;
        }

        EventWithImage item = filteredItems.get(selectedIndex);

        // 1) Remove from Storage (if URL is a valid gs:// or https:// path)
        if (item.imageUrl != null && !item.imageUrl.isEmpty()) {
            try {
                storage.getReferenceFromUrl(item.imageUrl)
                        .delete()
                        .addOnSuccessListener(unused ->
                                Toast.makeText(requireContext(),
                                        "Image file deleted", Toast.LENGTH_SHORT).show()
                        )
                        .addOnFailureListener(e ->
                                Toast.makeText(requireContext(),
                                        "File delete failed: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show()
                        );
            } catch (IllegalArgumentException ex) {
                // If URL is not a storage URL; ignore or handle as needed
            }
        }

        // 2) Remove reference from Event document
        db.collection(COLLECTION_EVENTS)
                .document(item.eventId)
                .update(FIELD_IMAGE_URL, null)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(requireContext(),
                            "Image reference removed", Toast.LENGTH_SHORT).show();
                    allItems.remove(item);
                    filterImages(etSearch != null && etSearch.getText() != null
                            ? etSearch.getText().toString()
                            : "");
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(),
                                "Failed to update event: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }
}
