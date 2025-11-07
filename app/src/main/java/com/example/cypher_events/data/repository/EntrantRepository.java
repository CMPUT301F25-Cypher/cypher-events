package com.example.cypher_events.data.repository;

import com.example.cypher_events.util.Result;

public interface EntrantRepository {
    Result<Boolean> joinWaitlist(String eventId, String uid);
    Result<Boolean> leaveWaitlist(String eventId, String uid);
    Result<Boolean> isJoined(String eventId, String uid);
}
