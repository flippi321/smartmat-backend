package edu.ntnu.idatt2106_09.backend.repository;

import edu.ntnu.idatt2106_09.backend.model.Category;
import edu.ntnu.idatt2106_09.backend.model.Household;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface HouseholdRepository extends JpaRepository<Household, Long> {
    @Query("SELECT g FROM Household g")
    Set<Household> getAllHouseholds();

    Optional<Household> findByInvitationNr(Long invitationNr);
}

