package com.example.cypher_events.domain.service;

import com.example.cypher_events.data.repository.EntrantRepository;
import com.example.cypher_events.util.Result;

public class RemoveProfileService {

    private final EntrantRepository entrantRepository;

    // Inject repository dependency
    public RemoveProfileService(EntrantRepository repo) {
        this.entrantRepository = repo;
    }

    // Remove an entrant profile by email (administrator action)
    public boolean removeProfile(String email) {

        // Reject invalid email
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        // Ask repository to delete entrant
        Result<Boolean> result = entrantRepository.deleteEntrant(email);

        // Validate result object and ensure operation succeeded
        if (result == null || !result.isOk()) {
            return false;
        }

        // Return true only when deletion is confirmed
        Boolean value = result.getData();
        return Boolean.TRUE.equals(value);
    }
}
