package com.example.cypher_events.ui.entrant;

import java.util.ArrayList;

public class EnterantFunction {
    public void UpdateIn_fireStore(ArrayList<Enterant> Toupdate){
        //will write logic when receive firebase json file
    }

    public void join_Waiting_list(ArrayList<Enterant> event,Enterant Enterant){
        event.add(Enterant);
        UpdateIn_fireStore(event);

    }
    public void leave_Waiting_list(ArrayList<Enterant> event,Enterant Enterant){
        event.remove(Enterant);
        UpdateIn_fireStore(event);
    }
}
