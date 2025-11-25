package com.example.cypher_events.domain.service;

import java.util.ArrayList;
import java.util.Map;

/*
User Story:US 01.05.04
As an entrant, I want to know total entrants on the waiting list
 */
public class TotalEntWaitingListService {
    public int Total_ent_waitinglist(ArrayList<Map<String, Object>> LWait){
        return LWait.size();
    }
}
