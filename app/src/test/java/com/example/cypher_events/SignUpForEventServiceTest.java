package com.example.cypher_events;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.cypher_events.domain.service.SignUpForEventService;

public class SignUpForEventServiceTest {

    @Test
    public void firstSignUp_addsUser_returnsSignedUp() {
        SignUpForEventService svc = new SignUpForEventService();

        String deviceId = "dev-1";
        String userId   = "demoUser";
        String eventId  = "e1";

        svc.registerDeviceUser(deviceId, userId);

        String msg = svc.signUp(deviceId, eventId);

        assertEquals("Signed up for event.", msg);
        assertTrue(svc.isSignedUp(eventId, userId));
    }

    @Test
    public void secondSignUp_sameUser_sameEvent_returnsAlready() {
        SignUpForEventService svc = new SignUpForEventService();

        String deviceId = "dev-1";
        String userId   = "demoUser";
        String eventId  = "e1";

        svc.registerDeviceUser(deviceId, userId);

        svc.signUp(deviceId, eventId);           // first time
        String msg2 = svc.signUp(deviceId, eventId); // duplicate

        assertEquals("Already signed up.", msg2);
        assertTrue(svc.isSignedUp(eventId, userId));
    }

    @Test
    public void invalidOrUnknownDevice_returnsMessages_noCrash() {
        SignUpForEventService svc = new SignUpForEventService();

        assertEquals("Invalid device or event.", svc.signUp("", "e1"));
        assertEquals("Invalid device or event.", svc.signUp("dev-1", ""));
        assertEquals("No user for device.",     svc.signUp("unknown", "e1"));
    }
}
