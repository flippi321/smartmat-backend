package edu.ntnu.idatt2106_09.backend.repository;

import edu.ntnu.idatt2106_09.backend.model.GroceryItemRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Set;

@Repository
public interface GroceryItemRecipeRepository extends JpaRepository<GroceryItemRecipe, Long> {
    @Query("SELECT g FROM GroceryItemRecipe g WHERE g.recipe.recipe_id = :recipeId")
    Set<GroceryItemRecipe> findGroceryItemRecipeByRecipeId(Long recipeId);
}
