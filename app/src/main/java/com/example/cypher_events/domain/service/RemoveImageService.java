package com.example.cypher_events.domain.service;

import com.example.cypher_events.util.Result;
import java.util.Map;
import java.util.HashMap;

public class RemoveImageService {
    private final Map<String, String> imageStore = new HashMap<>();

    // mock method to add images (for tests)
    public void addImage(String id, String url) { imageStore.put(id, url); }

    public Result<Boolean> removeImage(String id) {
        if (id == null || id.isEmpty()) return Result.err(new Exception("Invalid ID"));
        return imageStore.remove(id) != null
                ? Result.ok(true)
                : Result.err(new Exception("Image not found"));
    }

    public boolean hasImage(String id) { return imageStore.containsKey(id); }
}