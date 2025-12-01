package com.example.cypher_events.ui.admin;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cypher_events.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdminManageImagesFragment extends Fragment {

    private ListView listView;
    private Button btnDeleteSelected;
    private ImageButton btnBack;

    private AdminImagesListAdapter adapter;
    private ArrayList<EventImage> images = new ArrayList<>();
    private int selectedPos = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_admin_manage_images, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.Image_view);
        btnDeleteSelected = view.findViewById(R.id.btnDeleteSelectedImage);
        btnBack = view.findViewById(R.id.btnBack);

        adapter = new AdminImagesListAdapter(getContext(), images);
        listView.setAdapter(adapter);

        loadImages();

        listView.setOnItemClickListener((parent, v, position, id) -> {
            selectedPos = position;
            adapter.setSelected(position);
        });

        btnDeleteSelected.setOnClickListener(v -> deleteSelected());

        btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );
    }

    private void loadImages() {
        FirebaseFirestore.getInstance()
                .collection("Events")
                .get()
                .addOnSuccessListener(result -> {

                    images.clear();

                    for (DocumentSnapshot doc : result.getDocuments()) {

                        String base64 = doc.getString("Event_posterBase64");
                        if (base64 != null && !base64.isEmpty()) {

                            // Decode base64 → Bitmap
                            byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                            images.add(new EventImage(doc.getId(), bitmap));
                        }
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load images", Toast.LENGTH_SHORT).show()
                );
    }
    private void deleteSelected() {
        if (selectedPos == -1) {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
            return;
        }

        EventImage selected = images.get(selectedPos);

        new AlertDialog.Builder(getContext())
                .setTitle("Delete Image")
                .setMessage("Are you sure you want to delete this image?")
                .setPositiveButton("Delete", (d, i) -> {

                    FirebaseFirestore.getInstance()
                            .collection("Events")
                            .document(selected.eventId)
                            .update("Event_posterBase64", "")
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();

                                images.remove(selectedPos);
                                selectedPos = -1;
                                adapter.setSelected(-1);
                                adapter.notifyDataSetChanged();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(getContext(),
                                            "Error: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show()
                            );

                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    public static class EventImage {
        public String eventId;
        public Bitmap bitmap;

        public EventImage(String eventId, Bitmap bitmap) {
            this.eventId = eventId;
            this.bitmap = bitmap;
        }
    }
}
