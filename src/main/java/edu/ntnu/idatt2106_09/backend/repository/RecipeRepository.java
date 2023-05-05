package edu.ntnu.idatt2106_09.backend.repository;

import edu.ntnu.idatt2106_09.backend.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 This interface is responsible for handling database operations related to the Recipe entity.
 */
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    /**
     * Returns all recipes present in the database.
     *
     * @return a set containing all the recipes present in the database.
     */
    @Query("SELECT r FROM Recipe r")
    Set<Recipe> getAllRecipes();
}
