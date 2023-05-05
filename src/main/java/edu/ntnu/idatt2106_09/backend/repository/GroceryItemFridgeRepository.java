package edu.ntnu.idatt2106_09.backend.repository;

import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
import edu.ntnu.idatt2106_09.backend.model.GroceryItemFridge;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface GroceryItemFridgeRepository extends JpaRepository<GroceryItemFridge, Long> {
    @Query("SELECT g FROM GroceryItemFridge g WHERE g.fridge.fridgeId = :fridgeId")
    Set<GroceryItemFridge> findAllByFridgeId(Long fridgeId);

    @Query("SELECT g FROM GroceryItemFridge g WHERE g.id.fridgeId = :fridgeId AND g.id.groceryItemId = :groceryItemId ORDER BY g.expirationDate ASC")
    List<GroceryItemFridge> findByFridgeIdAndGroceryItemId(Long fridgeId, Long groceryItemId);

    @Modifying
    @Transactional
    @Query("DELETE FROM GroceryItemFridge g WHERE g.id.fridgeId = :fridgeId AND g.id.groceryItemId = :groceryItemId AND g.id.timestamp = :timestamp")
    void deleteByFridgeIdAndGroceryItemIdAndTimestamp(Long fridgeId, Long groceryItemId, LocalDateTime timestamp);


    @Modifying
    @Transactional
    @Query("UPDATE GroceryItemFridge g SET g.amount = :amount WHERE g.id.fridgeId = :fridgeId AND g.id.groceryItemId = :groceryItemId AND g.id.timestamp = :timestamp")
    void updateAmountByFridgeIdAndGroceryItemIdAndTimestamp(Long fridgeId, Long groceryItemId, LocalDateTime timestamp, double amount);
}
