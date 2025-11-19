package com.example.cypher_events.domain.service;

import com.example.cypher_events.util.Result;
import java.util.Map;
import java.util.HashMap;

public class RemoveImageService {

    // In-memory mock image store for testing
    private final Map<String, String> imageStore = new HashMap<>();

    // Add an image to the store (used by tests)
    public void addImage(String id, String url) {
        imageStore.put(id, url);
    }

    // Remove an image by its ID
    public Result<Boolean> removeImage(String id) {

        // Validate image ID
        if (id == null || id.trim().isEmpty()) {
            return Result.err(new Exception("Invalid ID"));
        }

        // Attempt removal
        boolean removed = (imageStore.remove(id) != null);

        // Return success or error
        if (removed) {
            return Result.ok(true);
        } else {
            return Result.err(new Exception("Image not found"));
        }
    }

    // Check if an image exists
    public boolean hasImage(String id) {
        return imageStore.containsKey(id);
    }
}
