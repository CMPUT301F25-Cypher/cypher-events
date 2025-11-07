package com.example.cypher_events.domain.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Enterant_actions {
    public void add_enterant_to_event_array(ArrayList<Map<String,Object>> event, String name, String email, int phone, String event_name, ArrayList<String> history){
        Map<String, Object> Ent_info_map = new HashMap<>();
        Ent_info_map.put("name", name);
        Ent_info_map.put("email", email);
        Ent_info_map.put("phone", phone);
        event.add(Ent_info_map);
        history.add(event_name);
    }
    public void remove_enterant_to_event_array(ArrayList<Map<String,Object>> event,String name, String email, int phone,String event_name,ArrayList<String> history){
        //call pull to db
        int num_event_parti=event.size();
        int val=0;
        while (val<event.size()){
            Map<String,Object> entrant=event.get(val);
            String storedName = (String) entrant.get("name");
            String storedEmail = (String) entrant.get("email");
            int storedPhone = ((Number) entrant.get("phone")).intValue();
            if (storedName.equals(name)
                    && storedEmail.equals(email)
                    && storedPhone == phone) {
                event.remove(val);
                break;
            }
            history.add(event_name);
            //call push to db
        }
    }
}
