package edu.ntnu.idatt2106_09.backend.repository;

import edu.ntnu.idatt2106_09.backend.model.GroceryItemFridge;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * The repository for managing  GroceryItemFridge entities in the database.
 */
@Repository
public interface GroceryItemFridgeRepository extends JpaRepository<GroceryItemFridge, Long> {

    /**
     * Finds all GroceryItemFridge objects belonging to a fridge with a given ID.
     *
     * @param fridgeId the ID of the fridge to search for
     * @return a set of all GroceryItemFridge objects found in the fridge
     */
    @Query("SELECT g FROM GroceryItemFridge g WHERE g.fridge.fridgeId = :fridgeId")
    Set<GroceryItemFridge> findAllByFridgeId(Long fridgeId);

    /**
     * Finds all GroceryItemFridge objects for a given fridge and grocery item, sorted by their expiration date in ascending order.
     *
     * @param fridgeId the ID of the fridge to search for
     * @param groceryItemId the ID of the grocery item to search for
     * @return a list of all GroceryItemFridge objects found in the fridge, sorted by expiration date
     */
    @Query("SELECT g FROM GroceryItemFridge g WHERE g.id.fridgeId = :fridgeId AND g.id.groceryItemId = :groceryItemId ORDER BY g.expirationDate ASC")
    List<GroceryItemFridge> findByFridgeIdAndGroceryItemId(Long fridgeId, Long groceryItemId);

    /**
     * Deletes a specific GroceryItemFridge object based on the given fridge ID, grocery item ID, and timestamp.
     *
     * @param fridgeId the ID of the fridge the object belongs to
     * @param groceryItemId the ID of the grocery item the object belongs to
     * @param timestamp the timestamp of the object to delete
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM GroceryItemFridge g WHERE g.id.fridgeId = :fridgeId AND g.id.groceryItemId = :groceryItemId AND g.id.timestamp = :timestamp")
    void deleteByFridgeIdAndGroceryItemIdAndTimestamp(Long fridgeId, Long groceryItemId, LocalDateTime timestamp);

    /**
     * Updates the amount of a specific GroceryItemFridge object based on the given fridge ID, grocery item ID, and timestamp.
     *
     * @param fridgeId the ID of the fridge the object belongs to
     * @param groceryItemId the ID of the grocery item the object belongs to
     * @param timestamp the timestamp of the object to update
     * @param amount the new amount to set for the object
     */
    @Modifying
    @Transactional
    @Query("UPDATE GroceryItemFridge g SET g.amount = :amount WHERE g.id.fridgeId = :fridgeId AND g.id.groceryItemId = :groceryItemId AND g.id.timestamp = :timestamp")
    void updateAmountByFridgeIdAndGroceryItemIdAndTimestamp(Long fridgeId, Long groceryItemId, LocalDateTime timestamp, double amount);
}
