package com.example.cypher_events;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.cypher_events.domain.model.Notification;
import com.example.cypher_events.domain.service.NotifyNotChosenService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class NotifyNotChosenServiceTest {

    private final NotifyNotChosenService service = new NotifyNotChosenService();
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Test
    public void testSendNotChosenNotifications() throws InterruptedException {
        // Test entrant IDs
        List<String> notChosenIds = List.of("testUser3", "testUser4");

        // Send notifications
        service.sendNotChosenNotifications(notChosenIds);

        CountDownLatch latch = new CountDownLatch(1);

        db.getReference("notifications").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int found = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    Notification n = child.getValue(Notification.class);
                    if (n != null && notChosenIds.contains(n.getEntrantId())) {
                        assertEquals("Better Luck Next Time", n.getTitle());
                        assertEquals("Unfortunately, you were not selected in the lottery.", n.getMessage());
                        found++;
                    }
                }
                assertEquals(notChosenIds.size(), found);
                latch.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("NotifyNotChosenServiceTest", "Firebase error: " + error.getMessage());
                latch.countDown();
            }
        });

        // Wait up to 10 seconds for Firebase callback
        latch.await(10, TimeUnit.SECONDS);
    }
}
