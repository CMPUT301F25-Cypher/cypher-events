package com.example.cypher_events.domain.service;

import com.example.cypher_events.data.repository.EntrantRepository;
import com.example.cypher_events.util.Result;

public class RemoveOrganizerService {

    private final EntrantRepository entrantRepository;

    public RemoveOrganizerService(EntrantRepository repo) {
        this.entrantRepository = repo;
    }

    public boolean removeOrganizer(String organizerEmail) {

        if (organizerEmail == null || organizerEmail.trim().isEmpty()) {
            return false;
        }

        Result<Boolean> result = entrantRepository.deleteEntrant(organizerEmail);

        if (result == null || !result.isOk()) {
            return false;
        }

        Boolean deleted = result.getData();
        return Boolean.TRUE.equals(deleted);
    }
}