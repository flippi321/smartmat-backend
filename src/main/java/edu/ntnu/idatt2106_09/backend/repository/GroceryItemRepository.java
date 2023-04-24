package edu.ntnu.idatt2106_09.backend.repository;

import edu.ntnu.idatt2106_09.backend.model.Fridge;
import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface GroceryItemRepository extends JpaRepository<GroceryItem, Long> {

    @Query("SELECT g FROM GroceryItem g")
    Set<GroceryItem> getAllGroceryItems();
}
