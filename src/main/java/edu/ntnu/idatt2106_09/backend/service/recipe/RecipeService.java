package edu.ntnu.idatt2106_09.backend.service.recipe;

import edu.ntnu.idatt2106_09.backend.dto.GroceryItemFridgeAlgoDto;
import edu.ntnu.idatt2106_09.backend.dto.GroceryItemRecipeDto;
import edu.ntnu.idatt2106_09.backend.dto.recipe.RecipeDTO;
import edu.ntnu.idatt2106_09.backend.dto.recipe.RecipeResponseDTO;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.NotFoundException;
import edu.ntnu.idatt2106_09.backend.model.Recipe;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

/**
 * Service interface for handling recipe related operations. Provides methods for fetching, creating, and updating
 * recipes, as well as for calculating recommended recipes based on the contents of a fridge. Also provides methods
 * for converting between Recipe and RecipeDTO and between GroceryItemRecipe and GroceryItemRecipeDto.
 */
@Service
public interface RecipeService {

    /**
     * Adds a new recipe to the repository and returns a ResponseEntity object containing the added recipe in DTO form.
     *
     * @param recipe The RecipeDTO object containing the recipe data to be added.
     * @return ResponseEntity<Object> containing the added recipe in RecipeDTO form if successful, or an error message
     *         and HTTP status if unsuccessful.
     * @throws Exception If an error occurs while adding the recipe to the repository.
     */
    public ResponseEntity<Object> addRecipe(RecipeDTO recipe);

    /**
     * Retrieves a recipe by its ID from the repository.
     *
     * @param recipeId The ID of the recipe to be retrieved.
     * @return Optional<Recipe> containing the recipe if found, or an empty Optional if the recipe with the specified ID does not exist.
     */
    public Optional<Recipe> getRecipeById(Long recipeId);

    /**
     * Retrieves a recipe and its associated ingredients by the recipe ID.
     *
     * @param recipeId The ID of the recipe to be retrieved along with its ingredients.
     * @return ResponseEntity<Object> containing the recipe and its ingredients in a RecipeResponseDTO format if found,
     *         or an HTTP status of NOT_FOUND if the recipe with the specified ID does not exist.
     * @throws NotFoundException If the recipe with the specified ID is not found in the repository.
     */
    public ResponseEntity<Object> getRecipeAndAllIngredients(Long recipeId);

    /**
     * Retrieves all the recipes available and returns them as a set of RecipeDTO objects wrapped in a ResponseEntity.
     *
     * @return A ResponseEntity containing a set of RecipeDTO objects representing all the recipes available, with an HTTP status of OK.
     */
    public ResponseEntity<Set<RecipeDTO>> getAllRecipe();

    /**
     * Deletes the recipe with the given recipeId from the database.
     *
     * @param recipeId the id of the recipe to be deleted
     * @return ResponseEntity<Object> with HTTP status OK and the deleted recipe as a RecipeDTO if the recipe was
     *         successfully deleted, ResponseEntity<Object> with HTTP status NOT_FOUND and an error message if the
     *         recipe with the given recipeId was not found
     */
    public ResponseEntity<Object> deleteRecipe(Long recipeId);

    /**
     * Retrieves all grocery items in a fridge and returns them as a HashMap with the grocery item ID as the key
     * and a GroceryItemFridgeAlgoDto object containing the grocery item details as the value.
     *
     * @param fridgeId The ID of the fridge containing the grocery items.
     * @return HashMap<Long, GroceryItemFridgeAlgoDto> containing grocery items in the fridge as GroceryItemFridgeAlgoDto objects, with grocery item IDs as keys.
     * @throws NotFoundException If the fridge with the specified ID is not found in the repository.
     */
    public Map<Long, GroceryItemFridgeAlgoDto> retrieveFridgeItemsHashMap(long fridgeId);

    /**
     * Retrieves all recipes and their associated grocery items from the repository, and returns them as a list of lists
     * of GroceryItemRecipeDto objects.
     * Each inner list represents a recipe's grocery items, and the outer list contains all recipes.
     *
     * @return List<List<GroceryItemRecipeDto>> containing all recipes and their associated grocery items as
     *         GroceryItemRecipeDto objects.
     */
    public List<List<GroceryItemRecipeDto>> getAllRecipeList();

    /**
     * Compares the contents of a HashMap representing the fridge to a list of grocery items required to make a recipe,
     * and returns a double value between 0 and 1 indicating the level of matching between the two.
     * This method calculates the ratio of the number of grocery items required for the recipe that are present in the
     * fridge, to the total number of grocery items required for the recipe.
     *
     * @return a double value between 0 and 1 representing the level of matching between the fridge contents and the recipe
     */
    public double compareFridgeAndRecipeList
            (Map<Long, GroceryItemFridgeAlgoDto> fridge, List<GroceryItemRecipeDto> recipe);

    /**
     * Filters the recipes based on the comparison between fridge contents and recipe grocery items. Only returns
     * recipes that meet or exceed the defined threshold in RecipeRecommenderConstants.RECOMMENDATION_THRESHOLD.
     *
     * @param fridge A Hashtable containing the contents of the fridge with key as GroceryItem ID and value as
     *               GroceryItemFridgeDTO.
     * @param recipeList A list of lists of GroceryItemRecipeDTO objects representing the recipes and their required
     *                   grocery items.
     * @return A list of lists of GroceryItemRecipeDTO objects representing the filtered recipes that meet or exceed
     *         the threshold.
     */
    public List<List<GroceryItemRecipeDto>> getRecipesOverThreshold(
            Map<Long, GroceryItemFridgeAlgoDto> fridge,
            List<List<GroceryItemRecipeDto>> recipeList);

    /**
     * Calculates the weight of each recipe in the recipesOverThreshold list based on the available fridge items.
     * The weight represents how closely the fridge items match the recipe's required grocery items.
     *
     * @param fridgeMap A HashMap containing the contents of the fridge with key as GroceryItem ID and value as
     *                  GroceryItemFridgeDTO.
     * @param recipesOverThreshold A list of lists of GroceryItemRecipeDTO objects representing the filtered recipes
     *                             that meet or exceed the threshold.
     * @return A double array containing the weight of each recipe in the recipesOverThreshold list.
     */
    public double[] getWeightListOfRecipeList(Map<Long, GroceryItemFridgeAlgoDto> fridgeMap,
                                              List<List<GroceryItemRecipeDto>> recipesOverThreshold);

    /**
     * Sorts the double[] weight and List<List<GroceryItemRecipeDTO>> recipe in descending order based on the values in
     * weight using the Quick Sort algorithm.
     *
     * @param weight A double array containing the weight of each recipe in the recipes list.
     * @param recipes A list of lists of GroceryItemRecipeDTO objects representing the recipes to be sorted.
     * @param low The starting index of the subarray to be sorted.
     * @param high The ending index of the subarray to be sorted.
     */
    public void quickSort(double[] weight, List<List<GroceryItemRecipeDto>> recipes, int low, int high);

    /**
     * A helper method that partitions the given recipes and their associated weights according to the Quick Sort
     * algorithm. The partitioning process is done in-place, modifying the original recipes and weight arrays.
     *
     * @param weight An array of double values representing the weights of the recipes.
     * @param recipes A list of lists containing GroceryItemRecipeDto objects, where each inner list represents a
     *                recipe's grocery items.
     * @param low The starting index for the partitioning process in the weight array and recipes list.
     * @param high The ending index for the partitioning process in the weight array and recipes list.
     * @return int The index of the pivot element after the partitioning process is complete.
     */
    public int partition(double[] weight, List<List<GroceryItemRecipeDto>> recipes, int low, int high);

    /**
     * Retrieves recommended recipes based on the available items in the specified fridge. The recommendations are
     * sorted in descending order of the calculated weight (based on item availability and freshness).
     * Each inner list represents a recipe's grocery items.
     *
     * @param fridgeId The ID of the fridge for which the recommended recipes are to be retrieved.
     * @return List<List<GroceryItemRecipeDto>> A list of lists containing recommended recipes and their associated
     *         grocery items as GroceryItemRecipeDto objects.
     */
    public List<List<GroceryItemRecipeDto>> getRecommendedRecipes(Long fridgeId, int portions);

    /**
     * Converts a list of lists containing GroceryItemRecipeDto objects into a list of RecipeResponseDTO objects.
     * Each RecipeResponseDTO object will contain the recipe details and a list of ingredients (IngredientDTO).
     *
     * @param listOfGroceryItemRecipeLists A list of lists containing GroceryItemRecipeDto objects, where each inner
     *                                     list represents a recipe's grocery items.
     * @return List<RecipeResponseDTO> A list of RecipeResponseDTO objects containing the recipe details and a list of
     *                                 ingredients.
     */
    public List<RecipeResponseDTO> convertToRecipeResponseDTO(List<List<GroceryItemRecipeDto>> listOfGroceryItemRecipeLists);

    /**
     * Updates the fridge's grocery items after using a recipe. The fridge is represented by a map where the key is
     * the grocery item ID and the value is a GroceryItemFridgeAlgoDto object. This method adjusts the amounts of
     * each grocery item in the fridge based on the amounts used in the recipe.
     *
     * @param fridge A map representing the fridge's grocery items with the key being the grocery item ID and the value
     *               being a GroceryItemFridgeAlgoDto object.
     * @param recipe A list of GroceryItemRecipeDto objects representing the grocery items used in the recipe.
     */
    public void updateFridgeAfterRecipe(Map<Long, GroceryItemFridgeAlgoDto> fridge, List<GroceryItemRecipeDto> recipe);

    /**
     * Retrieves a list of recommended recipes for a week based on the ingredients available in the fridge.
     * The method aims to create a week's menu of recipes that can be prepared using the available ingredients.
     * If there are not enough recipes that can be created, filler recipes are added to complete the week's menu.
     *
     * @param fridgeId The ID of the fridge containing the ingredients.
     * @return A list of lists containing GroceryItemRecipeDto objects, representing the week's menu of recipes.
     */
    public List<List<GroceryItemRecipeDto>> retrieveRecommendedWeekMenu(Long fridgeId, int portions);

    /**
     * Calculates the expiration date weight for a given item.
     * The weight is determined based on the difference between the current date and the expiration date.
     * If the item has not expired, a weight less than 1 is assigned, with smaller values for items closer to expiration.
     * If the item has expired, a weight greater than 1 is assigned, with larger values for items that expired longer
     * ago.
     *
     * @param dateToday      The current date.
     * @param expirationDate The expiration date of the item.
     * @return The expiration date weight of the item.
     */
    public double getExpirationDateWeight(LocalDate dateToday, LocalDate expirationDate);
}
