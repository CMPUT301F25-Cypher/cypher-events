package com.example.cypher_events;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.cypher_events.domain.model.Notification;
import com.example.cypher_events.domain.service.NotifyWinnerService;
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
public class NotifyWinnerServiceTest {

    private final NotifyWinnerService service = new NotifyWinnerService();
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Test
    public void testSendWinningNotifications() throws InterruptedException {
        List<String> winnerIds = List.of("testUser1", "testUser2");
        service.sendWinningNotifications(winnerIds);

        CountDownLatch latch = new CountDownLatch(winnerIds.size());

        db.getReference("notifications").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int found = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    Notification n = child.getValue(Notification.class);
                    if (n != null && winnerIds.contains(n.getEntrantId())) {
                        assertEquals("Lottery Result", n.getTitle());
                        assertEquals("Congratulations! You have won the lottery!", n.getMessage());
                        found++;
                    }
                }
                assertEquals(winnerIds.size(), found);
                latch.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("NotifyWinnerServiceTest", "Firebase error: " + error.getMessage());
                latch.countDown();
            }
        });

        // Wait up to 10 seconds for Firebase callback
        latch.await(10, TimeUnit.SECONDS);
    }
}
