package com.example.cypher_events.data;

import org.junit.Test;
import static org.junit.Assert.*;

public class FakeEventRepositoryTest {

    @Test
    public void testRepositoryReturnsData() {
        FakeEventRepository repo = new FakeEventRepository();
        Result result = repo.getAllEvents();

        assertNotNull(result);
        assertTrue(result.value() != null || result.getValue() != null);
    }
}
