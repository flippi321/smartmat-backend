package edu.ntnu.idatt2106_09.backend.service;

import edu.ntnu.idatt2106_09.backend.model.Household;
import edu.ntnu.idatt2106_09.backend.repository.HouseholdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HouseholdService {
    // Add the HouseholdRepository dependency
    @Autowired
    private HouseholdRepository householdRepository;

    // Get a household by ID
    public Optional<Household> getHouseholdById(Long householdId) {
        return householdRepository.findById(householdId);
    }
}
