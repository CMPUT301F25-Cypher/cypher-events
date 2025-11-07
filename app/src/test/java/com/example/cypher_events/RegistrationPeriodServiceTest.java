package com.example.cypher_events;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.cypher_events.domain.service.RegistrationPeriodService;

public class RegistrationPeriodServiceTest {

    private RegistrationPeriodService service;

    @Before
    public void setup() {
        service = new RegistrationPeriodService();
    }

    @Test
    public void testSetWindowFirstTime() {
        long start = 1_700_000_000_000L; // sample millis
        long end   = 1_700_000_360_000L;

        String result = service.setRegistrationWindow("EVT_001", start, end);
        assertEquals("Window set.", result);

        assertTrue(service.isWindowSet("EVT_001"));
        assertEquals(start, service.getStartUtc("EVT_001"));
        assertEquals(end,   service.getEndUtc("EVT_001"));
    }

    @Test
    public void testUpdateWindow() {
        long start1 = 1_700_000_000_000L;
        long end1   = 1_700_000_360_000L;
        service.setRegistrationWindow("EVT_002", start1, end1);

        long start2 = 1_700_000_720_000L; // move later
        long end2   = 1_700_001_080_000L;

        String result = service.setRegistrationWindow("EVT_002", start2, end2);
        assertEquals("Window updated.", result);

        assertEquals(start2, service.getStartUtc("EVT_002"));
        assertEquals(end2,   service.getEndUtc("EVT_002"));
    }

    @Test
    public void testEndBeforeStart() {
        long start = 1_700_000_360_000L;
        long end   = 1_700_000_000_000L;

        String result = service.setRegistrationWindow("EVT_003", start, end);
        assertEquals("End must be after start.", result);
        assertFalse(service.isWindowSet("EVT_003"));
    }

    @Test
    public void testInvalidInputs() {
        String res1 = service.setRegistrationWindow("", 123L, 456L);
        assertEquals("Invalid event or times.", res1);

        String res2 = service.setRegistrationWindow("EVT_004", null, 456L);
        assertEquals("Invalid event or times.", res2);

        String res3 = service.setRegistrationWindow("EVT_004", 123L, null);
        assertEquals("Invalid event or times.", res3);
    }

    @Test
    public void testIsOpenAt() {
        long start = 1_700_000_000_000L;
        long end   = 1_700_000_360_000L;
        service.setRegistrationWindow("EVT_005", start, end);

        // inside
        assertTrue(service.isOpenAt("EVT_005", start));
        assertTrue(service.isOpenAt("EVT_005", (start + end) / 2));
        assertTrue(service.isOpenAt("EVT_005", end));

        // outside
        assertFalse(service.isOpenAt("EVT_005", start - 1));
        assertFalse(service.isOpenAt("EVT_005", end + 1));
    }
}
