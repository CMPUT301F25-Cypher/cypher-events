package com.example.cypher_events;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.cypher_events.domain.service.NotificationOptOutService;

public class NotificationOptOutServiceTest {

    @Test
    public void firstOptOut_disablesAndReportsDisabled() {
        NotificationOptOutService svc = new NotificationOptOutService();

        String deviceId = "dev-1";
        String userId   = "demoUser";

        // bind device -> user (demo for test)
        svc.registerDeviceUser(deviceId, userId);

        // default is treated as enabled; after opt-out it should be false
        assertTrue(svc.isNotificationsEnabled(userId));

        String msg = svc.optOut(deviceId);

        assertEquals("Notifications disabled.", msg);
        assertFalse(svc.isNotificationsEnabled(userId));
    }

    @Test
    public void secondOptOut_reportsAlreadyDisabled() {
        NotificationOptOutService svc = new NotificationOptOutService();

        String deviceId = "dev-1";
        String userId   = "demoUser";

        svc.registerDeviceUser(deviceId, userId);

        svc.optOut(deviceId);              // first time
        String msg2 = svc.optOut(deviceId); // second time

        assertEquals("Already disabled.", msg2);
        assertFalse(svc.isNotificationsEnabled(userId));
    }

    @Test
    public void invalidOrUnknownDevice_messagesNoCrash() {
        NotificationOptOutService svc = new NotificationOptOutService();

        assertEquals("Invalid device.", svc.optOut(""));
        assertEquals("No user for device.", svc.optOut("unknown-device"));
    }

}
