package edu.ntnu.idatt2106_09.backend.repository;

import edu.ntnu.idatt2106_09.backend.model.Fridge;
import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 This interface represents the repository for the Fridge model, extending JpaRepository to inherit basic CRUD
 functionalities. It provides custom queries for finding a fridge by its id, name and household id, as well as for
 finding all fridges, and a few variations of the find by household id query.
 */
@Repository
public interface FridgeRepository extends JpaRepository<Fridge, Long> {
    /**
     Returns a set containing all fridges.
     *
     @return a set containing all fridges
     */
    @Query("SELECT f FROM Fridge f")
    Set<Fridge> getAllFridges();
    /**
     Returns an Optional containing the fridge with the given id, or an empty Optional if no such fridge exists.
     *
     @param fridgeId the id of the fridge to retrieve
     @return an Optional containing the fridge with the given id, or an empty Optional if no such fridge exists
     */
    @Query("SELECT f FROM Fridge f WHERE f.fridgeId = :fridgeId")
    Optional<Fridge> findByFridgeId(@Param("fridgeId") Long fridgeId);
    /**
     Returns the fridge with the given name and household id.
     *
     @param name the name of the fridge to retrieve
     @param householdId the id of the household the fridge belongs to
     @return the fridge with the given name and household id
     */
    @Query("SELECT f FROM Fridge f WHERE f.name = :name AND f.household.householdId = :householdId")
    Fridge findByNameAndHouseholdId(@Param("name") String name, @Param("householdId") Long householdId);

    /**
     Returns the fridge with the given name, household id, and excluding the fridge with the given id.
     *
     @param name the name of the fridge to retrieve
     @param householdId the id of the household the fridge belongs to
     @param fridgeId the id of the fridge to exclude
     @return the fridge with the given name, household id, and excluding the fridge with the given id
     */
    @Query("SELECT f FROM Fridge f WHERE f.name = :name AND f.household.householdId = :householdId AND f.fridgeId != :fridgeId")
    Fridge findByNameAndHouseholdIdExcludeFridgeId(String name, Long householdId, Long fridgeId);

    /**
     Returns an Optional containing the fridge with the given household id, or an empty Optional if no such fridge exists.
     *
     @param householdId the id of the household to retrieve the fridge from
     @return an Optional containing the fridge with the given household id, or an empty Optional if no such fridge exists
     */
    @Query("SELECT f FROM Fridge f WHERE f.household.householdId = :householdId")
    Optional<Fridge> findByHouseholdId(@Param("householdId") Long householdId);

    /**
     Returns the fridge with the given household id.
     *
     @param householdId the id of the household to retrieve the fridge from
     @return the fridge with the given household id
     */
    @Query("SELECT f FROM Fridge f WHERE f.household.householdId = :householdId")
    Fridge findByHouseholdIdAsFridge(@Param("householdId") Long householdId);
}
