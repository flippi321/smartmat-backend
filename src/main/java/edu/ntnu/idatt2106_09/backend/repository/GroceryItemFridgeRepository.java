package edu.ntnu.idatt2106_09.backend.repository;

import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
import edu.ntnu.idatt2106_09.backend.model.GroceryItemFridge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Set;

@Repository
public interface GroceryItemFridgeRepository extends JpaRepository<GroceryItemFridge, Long> {
    @Query("SELECT g FROM GroceryItemFridge g WHERE g.fridge.fridgeId = :fridgeId")
    Set<GroceryItemFridge> findAllByFridgeId(Long fridgeId);
}
