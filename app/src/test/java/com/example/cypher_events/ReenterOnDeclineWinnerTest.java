package com.example.cypher_events;

import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.example.cypher_events.domain.service.ReenterOnDeclineWinner;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import com.example.cypher_events.domain.service.ReenterOnDeclineWinner;

public class ReenterOnDeclineWinnerTest {

    @Test
    public void notifiesAllExceptWinner_inOrder() {
        ReenterOnDeclineWinner svc = new ReenterOnDeclineWinner();

        List<String> entrants = Arrays.asList("winnerUser", "demoUser", "luigi", "mario");
        String winner = "winnerUser";

        List<String> seen = new ArrayList<>();
        ReenterOnDeclineWinner.ReentryHandler handler =
                new ReenterOnDeclineWinner.ReentryHandler() {
                    @Override public void openReentryScreen(String userId) {
                        seen.add(userId);
                    }
                };

        List<String> notified = svc.reenterIfWinnerDeclines(entrants, winner, handler);

        List<String> expected = Arrays.asList("demoUser", "luigi", "mario");
        assertEquals(expected, notified);
        assertEquals(expected, seen);
    }

    @Test
    public void emptyOrNullEntrants_returnsEmpty_andNoCalls() {
        ReenterOnDeclineWinner svc = new ReenterOnDeclineWinner();

        List<String> seen = new ArrayList<>();
        ReenterOnDeclineWinner.ReentryHandler handler =
                new ReenterOnDeclineWinner.ReentryHandler() {
                    @Override public void openReentryScreen(String userId) {
                        seen.add(userId);
                    }
                };

        assertTrue(svc.reenterIfWinnerDeclines(new ArrayList<String>(), "any", handler).isEmpty());
        assertTrue(svc.reenterIfWinnerDeclines(null, "any", handler).isEmpty());
        assertTrue(seen.isEmpty());
    }

    @Test
    public void winnerNotInList_notifiesAll() {
        ReenterOnDeclineWinner svc = new ReenterOnDeclineWinner();

        List<String> entrants = Arrays.asList("a", "b");
        String winner = "notPresent";

        List<String> seen = new ArrayList<>();
        ReenterOnDeclineWinner.ReentryHandler handler =
                new ReenterOnDeclineWinner.ReentryHandler() {
                    @Override public void openReentryScreen(String userId) {
                        seen.add(userId);
                    }
                };

        List<String> notified = svc.reenterIfWinnerDeclines(entrants, winner, handler);

        assertEquals(Arrays.asList("a", "b"), notified);
        assertEquals(notified, seen);
    }
}
