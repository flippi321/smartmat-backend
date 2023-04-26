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

@Repository
public interface FridgeRepository extends JpaRepository<Fridge, Long> {

    @Query("SELECT f FROM Fridge f")
    Set<Fridge> getAllFridges();

    @Query("SELECT f FROM Fridge f WHERE f.name = :name AND f.household.householdId = :householdId")
    Fridge findByNameAndHouseholdId(@Param("name") String name, @Param("householdId") Long householdId);

    @Query("SELECT f FROM Fridge f WHERE f.name = :name AND f.household.householdId = :householdId AND f.fridgeId != :fridgeId")
    Fridge findByNameAndHouseholdIdExcludeFridgeId(String name, Long householdId, Long fridgeId);
}
