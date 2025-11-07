package com.example.cypher_events.ui.entrant;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.cypher_events.EventListFragment;
import com.example.cypher_events.R;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

@RunWith(AndroidJUnit4.class)
public class AcceptDeclineUITest {

    @Test
    public void testAcceptDeclineButtonsWork() {
        FragmentScenario.launchInContainer(EventListFragment.class);

        // Verify list is displayed
        onView(withId(R.id.recyclerViewEvents)).check(matches(isDisplayed()));

        // Simulate pressing "Accept" or "Decline"
        onView(withId(R.id.btnAccept)).perform(click());
        onView(withText("Accept")).check(matches(isDisplayed()));

        onView(withId(R.id.btnDecline)).perform(click());
        onView(withText("Decline")).check(matches(isDisplayed()));
    }
}
