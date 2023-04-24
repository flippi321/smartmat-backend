package edu.ntnu.idatt2106_09.backend.repository;

import edu.ntnu.idatt2106_09.backend.model.Fridge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface FridgeRepository extends JpaRepository<Fridge, Long> {

    @Query("SELECT f FROM Fridge f")
    Set<Fridge> getAllFridges();
}
