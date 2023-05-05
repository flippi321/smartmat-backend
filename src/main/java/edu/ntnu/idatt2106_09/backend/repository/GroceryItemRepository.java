package edu.ntnu.idatt2106_09.backend.repository;

import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Repository interface for managing  GroceryItem entities in the database.
 */
@Repository
public interface GroceryItemRepository extends JpaRepository<GroceryItem, Long> {

    /**
     * Retrieves all grocery items from the database.
     *
     * @return A set of all grocery items in the database.
     */
    @Query("SELECT g FROM GroceryItem g")
    Set<GroceryItem> getAllGroceryItems();
}
