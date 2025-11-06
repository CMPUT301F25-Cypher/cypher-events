package com.example.cypher_events;

import com.example.cypher_events.models.Event;
import com.example.cypher_events.models.Entrant;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for Event Lottery System models
 * Tests the business logic for lottery and capacity management
 */
public class ExampleUnitTest {

    // ========== US 02.03.01 Tests - Limit Entrants ==========

    @Test
    public void testWaitingListFull() {
        Event event = new Event("Test Event", "Description", "org123");
        event.setMaxEntrants(10);
        event.setCurrentWaitingListCount(10);

        assertTrue("Waiting list should be full when at capacity",
                event.isWaitingListFull());
    }

    @Test
    public void testWaitingListNotFull() {
        Event event = new Event("Test Event", "Description", "org123");
        event.setMaxEntrants(10);
        event.setCurrentWaitingListCount(5);

        assertFalse("Waiting list should not be full when under capacity",
                event.isWaitingListFull());
    }

    @Test
    public void testUnlimitedWaitingList() {
        Event event = new Event("Test Event", "Description", "org123");
        event.setMaxEntrants(null); // Unlimited
        event.setCurrentWaitingListCount(1000);

        assertFalse("Unlimited list should never be full",
                event.isWaitingListFull());
    }

    @Test
    public void testAvailableSpots() {
        Event event = new Event("Test Event", "Description", "org123");
        event.setMaxEntrants(20);
        event.setCurrentWaitingListCount(15);

        assertEquals("Should have 5 spots available",
                5, event.getAvailableSpots());
    }

    @Test
    public void testAvailableSpotsUnlimited() {
        Event event = new Event("Test Event", "Description", "org123");
        event.setMaxEntrants(null); // Unlimited
        event.setCurrentWaitingListCount(100);

        assertEquals("Unlimited should return -1",
                -1, event.getAvailableSpots());
    }

    @Test
    public void testAvailableSpotsZero() {
        Event event = new Event("Test Event", "Description", "org123");
        event.setMaxEntrants(10);
        event.setCurrentWaitingListCount(10);

        assertEquals("Should have 0 spots available when full",
                0, event.getAvailableSpots());
    }

    @Test
    public void testCapacityDisplayLimited() {
        Event event = new Event("Test Event", "Description", "org123");
        event.setMaxEntrants(50);
        event.setCurrentWaitingListCount(25);

        assertEquals("Should display 25/50",
                "25/50", event.getCapacityDisplay());
    }

    @Test
    public void testCapacityDisplayUnlimited() {
        Event event = new Event("Test Event", "Description", "org123");
        event.setMaxEntrants(null);
        event.setCurrentWaitingListCount(100);

        assertEquals("Should display unlimited format",
                "100 (unlimited)", event.getCapacityDisplay());
    }

    // ========== US 02.02.03 Tests - Geolocation Settings ==========

    @Test
    public void testGeolocationRequiredDefault() {
        Event event = new Event("Test Event", "Description", "org123");

        assertFalse("Geolocation should be disabled by default",
                event.isGeolocationRequired());
    }

    @Test
    public void testGeolocationRequiredEnabled() {
        Event event = new Event("Test Event", "Description", "org123");
        event.setGeolocationRequired(true);

        assertTrue("Geolocation should be enabled when set",
                event.isGeolocationRequired());
    }

    @Test
    public void testGeolocationRequiredDisabled() {
        Event event = new Event("Test Event", "Description", "org123");
        event.setGeolocationRequired(false);

        assertFalse("Geolocation should be disabled when set to false",
                event.isGeolocationRequired());
    }

    // ========== Entrant Model Tests ==========

    @Test
    public void testEntrantDefaultStatus() {
        Entrant entrant = new Entrant("user123", "event456");

        assertEquals("Default status should be waiting",
                "waiting", entrant.getStatus());
    }

    @Test
    public void testEntrantNotificationsEnabledByDefault() {
        Entrant entrant = new Entrant("user123", "event456");

        assertTrue("Notifications should be enabled by default",
                entrant.isNotificationsEnabled());
    }

    @Test
    public void testEntrantStatusChange() {
        Entrant entrant = new Entrant("user123", "event456");
        entrant.setStatus("selected");

        assertEquals("Status should change to selected",
                "selected", entrant.getStatus());
    }

    // ========== Event Counter Tests ==========

    @Test
    public void testEventCountersInitialization() {
        Event event = new Event("Test Event", "Description", "org123");

        assertEquals("Current waiting list count should be 0",
                0, event.getCurrentWaitingListCount());
        assertEquals("Selected count should be 0",
                0, event.getSelectedCount());
        assertEquals("Enrolled count should be 0",
                0, event.getEnrolledCount());
        assertEquals("Cancelled count should be 0",
                0, event.getCancelledCount());
    }

    @Test
    public void testEventCounterIncrement() {
        Event event = new Event("Test Event", "Description", "org123");

        event.setCurrentWaitingListCount(5);
        event.setSelectedCount(2);
        event.setEnrolledCount(1);
        event.setCancelledCount(1);

        assertEquals(5, event.getCurrentWaitingListCount());
        assertEquals(2, event.getSelectedCount());
        assertEquals(1, event.getEnrolledCount());
        assertEquals(1, event.getCancelledCount());
    }

    // ========== Edge Cases ==========

    @Test
    public void testMaxEntrantsExactlyFull() {
        Event event = new Event("Test Event", "Description", "org123");
        event.setMaxEntrants(5);
        event.setCurrentWaitingListCount(5);

        assertTrue("Should be full at exact capacity",
                event.isWaitingListFull());
        assertEquals("Should have 0 available spots",
                0, event.getAvailableSpots());
    }

    @Test
    public void testMaxEntrantsOverCapacity() {
        Event event = new Event("Test Event", "Description", "org123");
        event.setMaxEntrants(5);
        event.setCurrentWaitingListCount(10); // Over capacity

        assertTrue("Should be full when over capacity",
                event.isWaitingListFull());
        assertEquals("Should have 0 available spots when over capacity",
                0, event.getAvailableSpots());
    }

    @Test
    public void testZeroMaxEntrants() {
        Event event = new Event("Test Event", "Description", "org123");
        event.setMaxEntrants(0);
        event.setCurrentWaitingListCount(0);

        assertTrue("Should be full when max is 0",
                event.isWaitingListFull());
    }
}