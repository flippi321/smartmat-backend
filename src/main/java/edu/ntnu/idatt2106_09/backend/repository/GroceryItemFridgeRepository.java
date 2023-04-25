package edu.ntnu.idatt2106_09.backend.repository;

import edu.ntnu.idatt2106_09.backend.model.Fridge;
import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
import edu.ntnu.idatt2106_09.backend.model.GroceryItemFridge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface GroceryItemFridgeRepository extends JpaRepository<GroceryItemFridge, Long> {
    @Query("SELECT f FROM GroceryItemFridge f WHERE f.fridge.fridgeId= :id")
    Set<Fridge> getAllGroceryItemFridge(@Param("id") long id);

    @Modifying
    @Query("DELETE FROM GroceryItemFridge g WHERE g.fridge.fridgeId = :id")
    void deleteAllGroceryItemsInFridge(@Param("id") long id);
}
