package com.example.cypher_events.domain.service;

import java.util.ArrayList;
import java.util.Map;

public class ViewCanceledAndFinalEnrolledServive {
    public ArrayList<String> enterantArrayView(ArrayList<Map<String, Object>> eventList) {

        ArrayList<String> enterantshow = new ArrayList<>();
        for (Map<String, Object> entrant : eventList) {
            String name = (String) entrant.get("name");
            String email = (String) entrant.get("email");

            enterantshow.add(name + " : " + email);


        }
        return enterantshow;
    }
}