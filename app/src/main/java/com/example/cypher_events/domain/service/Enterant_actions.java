package com.example.cypher_events.domain.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Enterant_actions {

    /**
     * Helps an entrant join a waiting list
     *

     * @param event        The list of event entrants
     * @param name         The entrant's name.
     * @param email        The entrant's email address.
     * @param phone        The entrant's phone number.
     * @param event_name   The name of the event the entrant is joining.
     * @param history      The entrant's event participation history.
     *
     * @return An ArrayList containing elements of format[entrant_history Array,event entrant list] for each event
 */

    public ArrayList<Object> add_enterant_to_event_array(
            ArrayList<Map<String, Object>> event,
            String name,
            String email,
            int phone,
            String event_name,
            ArrayList<String> history,
            String Obj_id
    ) {


        if (event == null) {
            event = new ArrayList<>();
        }
        if (history == null) {
            history = new ArrayList<>();
        }

        // Create entrant info map
        Map<String, Object> entInfoMap = new HashMap<>();
        entInfoMap.put("name", name);
        entInfoMap.put("email", email);
        entInfoMap.put("phone", phone);

        // Add entrant to event list
        event.add(entInfoMap);

        // Record event in history
        history.add(event_name);

        // Prepare return structure
        ArrayList<Object> ret = new ArrayList<>();
        ret.add(history);
        ret.add(event);

        return ret;
    }
    /**
     * Helps an entrant leave a waiting list
     *

     * @param event        The list of event entrants
     * @param name         The entrant's name.
     * @param email        The entrant's email address.
     * @param phone        The entrant's phone number.
     * @param event_name   The name of the event the entrant is joining.
     * @param history      The entrant's event participation history.
     *
     * @return An ArrayList containing elements of format[entrant_history Array,event entrant list] for each event
     */
    // Remove an entrant from the event list and update their history
    public ArrayList<Object> remove_enterant_to_event_array(
            ArrayList<Map<String, Object>> event,
            String name,
            String email,
            int phone,
            String event_name,
            ArrayList<String> history
    ) {

        // Ensure lists are not null
        if (event == null) {
            event = new ArrayList<>();
        }
        if (history == null) {
            history = new ArrayList<>();
        }

        int index = 0;

        // Scan through event list to find matching entrant
        while (index < event.size()) {
            Map<String, Object> entrant = event.get(index);

            String storedName = (String) entrant.get("name");
            String storedEmail = (String) entrant.get("email");

            int storedPhone = 0;
            Object phoneObj = entrant.get("phone");
            if (phoneObj instanceof Number) {
                storedPhone = ((Number) phoneObj).intValue();
            }

            boolean nameMatches = storedName != null && storedName.equals(name);
            boolean emailMatches = storedEmail != null && storedEmail.equals(email);
            boolean phoneMatches = storedPhone == phone;

            if (nameMatches && emailMatches && phoneMatches) {
                event.remove(index);
                break;
            }

            index++;
        }

        // Record this event name in history after removal attempt
        history.add(event_name);

        // Prepare return structure
        ArrayList<Object> ret = new ArrayList<>();
        ret.add(history);
        ret.add(event);

        return ret;
    }
}
