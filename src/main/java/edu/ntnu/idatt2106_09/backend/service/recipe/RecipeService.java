package edu.ntnu.idatt2106_09.backend.service.recipe;

import edu.ntnu.idatt2106_09.backend.dto.GroceryItemFridgeAlgoDto;
import edu.ntnu.idatt2106_09.backend.dto.GroceryItemRecipeDto;
import edu.ntnu.idatt2106_09.backend.dto.recipe.RecipeDTO;
import edu.ntnu.idatt2106_09.backend.dto.recipe.RecipeResponseDTO;
import edu.ntnu.idatt2106_09.backend.model.Recipe;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public interface RecipeService {

    public ResponseEntity<Object> addRecipe(RecipeDTO recipe);

    public Optional<Recipe> getRecipeById(Long recipeId);

    public ResponseEntity<Object> getRecipeAndAllIngredients(Long recipeId);

    public ResponseEntity<Set<RecipeDTO>> getAllRecipe();

    public ResponseEntity<Object> deleteRecipe(Long recipeId);

    public Map<Long, GroceryItemFridgeAlgoDto> retrieveFridgeItemsHashMap(long fridgeId);

    public List<List<GroceryItemRecipeDto>> getAllRecipeList();

    public double compareFridgeAndRecipeList
            (Map<Long, GroceryItemFridgeAlgoDto> fridge, List<GroceryItemRecipeDto> recipe);

    public List<List<GroceryItemRecipeDto>> getRecipesOverThreshold(
            Map<Long, GroceryItemFridgeAlgoDto> fridge,
            List<List<GroceryItemRecipeDto>> recipeList);

    public double[] getWeightListOfRecipeList(Map<Long, GroceryItemFridgeAlgoDto> fridgeMap,
                                              List<List<GroceryItemRecipeDto>> recipesOverThreshold);

    public void quickSort(double[] weight, List<List<GroceryItemRecipeDto>> recipes, int low, int high);

    public int partition(double[] weight, List<List<GroceryItemRecipeDto>> recipes, int low, int high);

    public List<List<GroceryItemRecipeDto>> getRecommendedRecipes(Long fridgeId);

    public List<RecipeResponseDTO> convertToRecipeResponseDTO(List<List<GroceryItemRecipeDto>> listOfGroceryItemRecipeLists);

    public void updateFridgeAfterRecipe(Map<Long, GroceryItemFridgeAlgoDto> fridge, List<GroceryItemRecipeDto> recipe);

    public List<List<GroceryItemRecipeDto>> retrieveRecommendedWeekMenu(Long fridgeId);

    public double getExpirationDateWeight(LocalDate dateToday, LocalDate expirationDate);
}
