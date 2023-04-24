package edu.ntnu.idatt2106_09.backend.service;

import edu.ntnu.idatt2106_09.backend.model.Fridge;
import edu.ntnu.idatt2106_09.backend.model.Recipe;
import edu.ntnu.idatt2106_09.backend.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    public Recipe addRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    public Optional<Recipe> getRecipeById(Long recipeId){
        return recipeRepository.findById(recipeId);
    }

    public Set<Recipe> getAllRecipe() {
        return recipeRepository.getAllRecipes();
    }

    public void deleteRecipe(Long recipeId) {
     recipeRepository.deleteById(recipeId);
    }

}
