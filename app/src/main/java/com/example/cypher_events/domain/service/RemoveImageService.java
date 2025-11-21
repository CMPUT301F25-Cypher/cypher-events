package com.example.cypher_events.domain.service;

import com.example.cypher_events.util.Result;
import java.util.Map;
import java.util.HashMap;

public class RemoveImageService {


    private final Map<String, String> imageStore = new HashMap<>();


    public void addImage(String id, String url) {
        imageStore.put(id, url);
    }


    public Result<Boolean> removeImage(String id) {


        if (id == null || id.trim().isEmpty()) {
            return Result.err(new Exception("Invalid ID"));
        }


        boolean removed = (imageStore.remove(id) != null);


        if (removed) {
            return Result.ok(true);
        } else {
            return Result.err(new Exception("Image not found"));
        }
    }


    public boolean hasImage(String id) {
        return imageStore.containsKey(id);
    }
}
