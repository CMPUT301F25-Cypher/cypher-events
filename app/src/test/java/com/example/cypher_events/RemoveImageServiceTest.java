package com.example.cypher_events;

import com.example.cypher_events.domain.service.RemoveImageService;
import com.example.cypher_events.util.Result;
import org.junit.Test;
import static org.junit.Assert.*;

public class RemoveImageServiceTest {

    @Test
    public void testRemoveExistingImage() {
        RemoveImageService service = new RemoveImageService();
        service.addImage("img1", "https://sample.com/image1.jpg");

        Result<Boolean> result = service.removeImage("img1");
        assertTrue(result.isOk());
        assertFalse(service.hasImage("img1"));
    }

    @Test
    public void testRemoveInvalidImage() {
        RemoveImageService service = new RemoveImageService();

        Result<Boolean> result = service.removeImage("nonexistent");
        assertFalse(result.isOk());
    }
}