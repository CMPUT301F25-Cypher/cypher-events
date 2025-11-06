package com.example.cypher_events;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

import com.example.cypher_events.domain.service.UpdateProfileUseCase;

public class UpdateProfileUseCaseTest {

    private Map<String,Object> entrant(String name, String email, String phone) {
        Map<String,Object> m = new HashMap<>();
        m.put("Entrant_name",  name);
        m.put("Entrant_email", email);
        m.put("Entrant_phone", phone);
        return m;
    }

    @Test
    public void updatesAllThreeFields_whenProvided() {
        Map<String,Object> e = entrant("Old Name", "old@mail.com", "111");
        String msg = UpdateProfileUseCase.update(e, " New Name ", " new@mail.com ", " 222 ");
        assertEquals("Profile updated.", msg);
        assertEquals("New Name", e.get("Entrant_name"));
        assertEquals("new@mail.com", e.get("Entrant_email"));
        assertEquals("222", e.get("Entrant_phone"));
    }

    @Test
    public void partialUpdate_keepsUnspecifiedFields() {
        Map<String,Object> e = entrant("A", "a@mail.com", "999");
        String msg = UpdateProfileUseCase.update(e, null, "b@mail.com", null);
        assertEquals("Profile updated.", msg);
        assertEquals("A", e.get("Entrant_name"));                // unchanged
        assertEquals("b@mail.com", e.get("Entrant_email"));      // updated
        assertEquals("999", e.get("Entrant_phone"));             // unchanged
    }

    @Test
    public void invalidEmail_blocksUpdate() {
        Map<String,Object> e = entrant("A", "a@mail.com", "999");
        String msg = UpdateProfileUseCase.update(e, null, "bad-email", null);
        assertEquals("Invalid email.", msg);
        assertEquals("a@mail.com", e.get("Entrant_email")); // unchanged
    }

    @Test
    public void emptyNameBlocksUpdate_whenExplicitlyProvided() {
        Map<String,Object> e = entrant("A", "a@mail.com", "999");
        String msg = UpdateProfileUseCase.update(e, "   ", null, null);
        assertEquals("Name cannot be empty.", msg);
        assertEquals("A", e.get("Entrant_name")); // unchanged
    }
}