package edu.ntnu.idatt2106_09.backend.repository;

import edu.ntnu.idatt2106_09.backend.model.Household;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * This interface provides access to the database for Household entities.
 */
@Repository
public interface HouseholdRepository extends JpaRepository<Household, Long> {

    /**
     * Retrieve all households from the database.
     *
     * @return A set of all the households in the database.
     */
    @Query("SELECT g FROM Household g")
    Set<Household> getAllHouseholds();

    /**
     * Find a household in the database by invitation number.
     *
     * @param invitationNr The invitation number of the household to be found.
     * @return An Optional containing the household if found, or an empty Optional if not.
     */
    Optional<Household> findByInvitationNr(Long invitationNr);
}

