package com.example.cypher_events;

import com.example.cypher_events.data.repository.fake.FakeEntrantRepository;
import com.example.cypher_events.domain.service.RemoveProfileService;
import org.junit.Test;
import static org.junit.Assert.*;

public class RemoveProfileServiceTest {

    @Test
    public void testRemoveExistingProfile() {
        FakeEntrantRepository repo = new FakeEntrantRepository();
        RemoveProfileService service = new RemoveProfileService(repo);

        assertTrue(service.removeProfile("jane@example.com"));
        assertFalse(service.removeProfile("jane@example.com")); // already deleted
    }

    @Test
    public void testRemoveInvalidProfile() {
        FakeEntrantRepository repo = new FakeEntrantRepository();
        RemoveProfileService service = new RemoveProfileService(repo);

        assertFalse(service.removeProfile(""));
        assertFalse(service.removeProfile("nonexistent@example.com"));
    }
}