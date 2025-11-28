package com.example.cypher_events;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;

import com.example.cypher_events.domain.service.DeclineInvitationService;

public class DeclineInvitationServiceTest {

    private DeclineInvitationService service;

    @Before
    public void setup() {
        service = new DeclineInvitationService();
        service.registerDeviceUser("DEVICE_XYZ", "USER_123");
    }

    @Test
    public void testDeclineInvitationFirstTime() {
        String result = service.declineInvitation("DEVICE_XYZ", "EVT_001");
        assertEquals("Invitation declined.", result);
        assertTrue(service.isDeclined("USER_123", "EVT_001"));
    }

    @Test
    public void testDeclineInvitationTwice() {
        service.declineInvitation("DEVICE_XYZ", "EVT_001");
        String result = service.declineInvitation("DEVICE_XYZ", "EVT_001");
        assertEquals("Already declined.", result);
    }

    @Test
    public void testInvalidDevice() {
        String result = service.declineInvitation("UNKNOWN_DEVICE", "EVT_001");
        assertEquals("No user for device.", result);
    }

    @Test
    public void testInvalidInputs() {
        String result = service.declineInvitation("", "");
        assertEquals("Invalid device or event.", result);
    }
}
