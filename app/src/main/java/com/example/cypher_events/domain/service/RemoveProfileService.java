package com.example.cypher_events.domain.service;

import com.example.cypher_events.data.repository.EntrantRepository;
import com.example.cypher_events.util.Result;

public class RemoveProfileService {

    private final EntrantRepository entrantRepository;


    public RemoveProfileService(EntrantRepository repo) {
        this.entrantRepository = repo;
    }


    public boolean removeProfile(String email) {


        if (email == null || email.trim().isEmpty()) {
            return false;
        }


        Result<Boolean> result = entrantRepository.deleteEntrant(email);


        if (result == null || !result.isOk()) {
            return false;
        }


        Boolean value = result.getData();
        return Boolean.TRUE.equals(value);
    }
}
