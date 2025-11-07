package com.example.cypher_events;

import static org.junit.Assert.assertEquals;

import com.example.cypher_events.domain.service.TotalEnterantEventList;
import com.example.cypher_events.domain.service.Enterant_actions;
import com.example.cypher_events.domain.service.Enterant_actions;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;

public class TotalEnterantEventListTest {
    @Test
    public void enterantTest(){
        TotalEnterantEventList totalEventval = new TotalEnterantEventList();
        ArrayList<Map<String, Object>> event = new ArrayList<>();
        Enterant_actions actions = new Enterant_actions();
        ArrayList<String> his = new ArrayList<>();
        actions.add_enterant_to_event_array(event, "Bruce", "Bruce@gmail.com", 1234567, "301Class", his,"301Class310");
        actions.add_enterant_to_event_array(event, "Peter", "peter@gmail.com", 23456, "301Class", his,"301Class310");
        int val=totalEventval.TotalEnterants(event);
        assertEquals(2, val);
    }

}
