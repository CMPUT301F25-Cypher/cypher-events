package com.example.cypher_events;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;

import com.example.cypher_events.domain.service.AcceptInvitationService;

public class AcceptInvitationServiceTest {

    private AcceptInvitationService service;

    @Before
    public void setup() {
        service = new AcceptInvitationService();
        service.registerDeviceUser("DEVICE_XYZ", "USER_123");
    }

    @Test
    public void testAcceptInvitationFirstTime() {
        String result = service.acceptInvitation("DEVICE_XYZ", "EVT_001");
        assertEquals("Invitation accepted.", result);
        assertTrue(service.isAccepted("USER_123", "EVT_001"));
    }

    @Test
    public void testAcceptInvitationTwice() {
        service.acceptInvitation("DEVICE_XYZ", "EVT_001");
        String result = service.acceptInvitation("DEVICE_XYZ", "EVT_001");
        assertEquals("Already accepted.", result);
    }

    @Test
    public void testInvalidDevice() {
        String result = service.acceptInvitation("UNKNOWN_DEVICE", "EVT_001");
        assertEquals("No user for device.", result);
    }

    @Test
    public void testInvalidInputs() {
        String result = service.acceptInvitation("", "");
        assertEquals("Invalid device or event.", result);
    }
}