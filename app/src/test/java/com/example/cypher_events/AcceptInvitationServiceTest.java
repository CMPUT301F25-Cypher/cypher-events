package com.example.cypher_events;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.cypher_events.domain.service.AcceptInvitationService;

public class AcceptInvitationServiceTest {

    @Test
    public void firstAccept_setsAccepted_andReturnsAcceptedMessage() {
        AcceptInvitationService svc = new AcceptInvitationService();

        String deviceId = "dev-1";
        String userId   = "demoUser";
        String eventId  = "e1";

        // demo binding for the test (in app you'll fetch userId by deviceId)
        svc.registerDeviceUser(deviceId, userId);

        String msg = svc.acceptInvitation(deviceId, eventId);

        assertEquals("Invitation accepted.", msg);
        assertTrue(svc.isAccepted(userId, eventId));
    }

    @Test
    public void secondAccept_returnsAlreadyAccepted_andKeepsTrue() {
        AcceptInvitationService svc = new AcceptInvitationService();

        String deviceId = "dev-1";
        String userId   = "demoUser";
        String eventId  = "e1";

        svc.registerDeviceUser(deviceId, userId);

        svc.acceptInvitation(deviceId, eventId); // first time
        String msg2 = svc.acceptInvitation(deviceId, eventId); // second time

        assertEquals("Already accepted.", msg2);
        assertTrue(svc.isAccepted(userId, eventId));
    }

    @Test
    public void invalidDeviceOrEvent_returnsMessages_noCrash() {
        AcceptInvitationService svc = new AcceptInvitationService();

        assertEquals("Invalid device or event.", svc.acceptInvitation("", "e1"));
        assertEquals("Invalid device or event.", svc.acceptInvitation("dev-1", ""));
        assertEquals("No user for device.",     svc.acceptInvitation("unknown", "e1"));
    }
}
