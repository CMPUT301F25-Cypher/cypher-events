package com.example.cypher_events;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * AdminManageImagesFragment
 * Mirrors the Manage Events fragment structure.
 * Uses EditText for searching, Buttons for add/delete,
 * and a ListView for displaying image names.
 */
public class AdminManageImagesFragment extends Fragment {

    private EditText search;
    private Button addImageButton;
    private Button deleteSelectedButton;
    private ListView imageListView;

    private ArrayAdapter<String> adapter;
    private List<String> allImages;
    private List<String> filteredImages;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate layout
        View view = inflater.inflate(R.layout.fragment_admin_manage_images, container, false);

        // Initialize UI components
        search = view.findViewById(R.id.Image_search);
        addImageButton = view.findViewById(R.id.Admin_add_image);
        deleteSelectedButton = view.findViewById(R.id.btnDeleteSelectedImage);
        imageListView = view.findViewById(R.id.Image_view);

        // Initialize data lists
        allImages = new ArrayList<>();
        filteredImages = new ArrayList<>();

        filteredImages.addAll(allImages);

        // Set up adapter
        adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_single_choice,
                filteredImages);
        imageListView.setAdapter(adapter);
        imageListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // Search filter
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim().toLowerCase();
                filterImages(query);
            }
        });


        // Delete selected image
        deleteSelectedButton.setOnClickListener(v -> {
            int selectedPos = imageListView.getCheckedItemPosition();
            if (selectedPos == ListView.INVALID_POSITION) {
                Toast.makeText(getContext(), "Select an image to delete", Toast.LENGTH_SHORT).show();
            } else {
                String selectedImage = filteredImages.get(selectedPos);
                allImages.remove(selectedImage);
                filteredImages.remove(selectedImage);
                adapter.notifyDataSetChanged();
                imageListView.clearChoices();
                Toast.makeText(getContext(), "Deleted: " + selectedImage, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    /**
     * Filters the list of images based on a search query.
     */
    private void filterImages(String query) {
        filteredImages.clear();
        if (query.isEmpty()) {
            filteredImages.addAll(allImages);
        } else {
            for (String img : allImages) {
                if (img.toLowerCase().contains(query)) {
                    filteredImages.add(img);
                }
            }
        }
        adapter.notifyDataSetChanged();
        imageListView.clearChoices();
    }
}
