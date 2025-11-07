package com.example.cypher_events.data.repository;

import com.example.cypher_events.domain.model.Entrant;
import com.example.cypher_events.util.Result;

public interface EntrantRepository {
    Result<Entrant> getEntrantByEmail(String email);
    Result<Boolean> deleteEntrant(String email);
    Result<Boolean> joinWaitlist(String entrantId, String eventId);
    Result<Boolean> leaveWaitlist(String entrantId, String eventId);
    Result<Boolean> isJoined(String entrantId, String eventId);
}
