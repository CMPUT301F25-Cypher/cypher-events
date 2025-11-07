package com.example.cypher_events.domain.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Enterant_actions {
    public ArrayList<Object> add_enterant_to_event_array(ArrayList<Map<String,Object>> event, String name, String email, int phone, String event_name, ArrayList<String> history, String Obj_id){
        ArrayList<Object> ret= new ArrayList<>();
        Map<String, Object> Ent_info_map = new HashMap<>();
        Ent_info_map.put("name", name);
        Ent_info_map.put("email", email);
        Ent_info_map.put("phone", phone);
        event.add(Ent_info_map);
        history.add(event_name);
        ret.add(history);
        ret.add(event);
        return ret;

    }
    public ArrayList<Object> remove_enterant_to_event_array(ArrayList<Map<String,Object>> event,String name, String email, int phone,String event_name,ArrayList<String> history){
        ArrayList<Object> ret= new ArrayList<>();
        int val=0;
        while (val<event.size()){
            Map<String,Object> entrant=event.get(val);
            String storedName = (String) entrant.get("name");
            String storedEmail = (String) entrant.get("email");
            int PhoneNo = ((Number) entrant.get("phone")).intValue();
            if (storedName.equals(name)
                    && storedEmail.equals(email)
                    && PhoneNo == phone) {
                event.remove(val);
                break;

            }
            val++;
            history.add(event_name);
            ret.add(history);
            ret.add(event);
        }
        return ret;

    }
}
