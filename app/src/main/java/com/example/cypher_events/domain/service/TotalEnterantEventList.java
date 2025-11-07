package com.example.cypher_events.domain.service;
import java.util.ArrayList;
import java.util.Map;

public class TotalEnterantEventList {
    public int TotalEnterants(ArrayList<Map<String, Object>> event){
        int val=event.size();
        return val;
    }
}
