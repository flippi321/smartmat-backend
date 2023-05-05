package edu.ntnu.idatt2106_09.backend.repository;

import edu.ntnu.idatt2106_09.backend.model.GroceryItemRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Set;

/**
 * This interface represents the repository class for the GroceryItemRecipe entity.
 * It provides methods to access and modify grocery item recipes in the database.
 */
@Repository
public interface GroceryItemRecipeRepository extends JpaRepository<GroceryItemRecipe, Long> {

    /**
     * Finds all the grocery items associated with a given recipe.
     *
     * @param recipeId The id of the recipe to search for.
     * @return A set of GroceryItemRecipe objects representing the grocery items associated with the recipe.
     */
    @Query("SELECT g FROM GroceryItemRecipe g WHERE g.recipe.recipe_id = :recipeId")
    Set<GroceryItemRecipe> findGroceryItemRecipeByRecipeId(Long recipeId);
}
