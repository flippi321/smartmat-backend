package edu.ntnu.idatt2106_09.backend.repository;

import edu.ntnu.idatt2106_09.backend.model.Category;
import edu.ntnu.idatt2106_09.backend.model.Fridge;
import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * This interface represents the repository for the Category model, extending JpaRepository.
 * It provides methods for basic CRUD operations, and a custom method for retrieving all categories.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Retrieves all categories in the database.
     *
     * @return A set of Category objects representing all categories.
     */
    @Query("SELECT g FROM Category g")
    Set<Category> getAllCategories();
}
