package edu.ntnu.idatt2106_09.backend.service;

import edu.ntnu.idatt2106_09.backend.config.RecipeRecommenderConstants;
import edu.ntnu.idatt2106_09.backend.dto.*;

import edu.ntnu.idatt2106_09.backend.model.*;
import edu.ntnu.idatt2106_09.backend.repository.FridgeRepository;
import edu.ntnu.idatt2106_09.backend.repository.GroceryItemRecipeRepository;
import edu.ntnu.idatt2106_09.backend.repository.GroceryItemRepository;
import edu.ntnu.idatt2106_09.backend.repository.RecipeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private GroceryItemRecipeRepository groceryItemRecipeRepository;


    @Autowired
    private FridgeService fridgeService;

    @Autowired
    private FridgeRepository fridgeRepository;

    @Autowired
    private GroceryItemService groceryItemService;

    @Autowired
    private ModelMapper modelMapper;

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


    private HashMap<Long, GroceryItemFridgeDTO> retrieveFridgeItemsHashMap(long fridgeId) {
        HashMap<Long, GroceryItemFridgeDTO> map = new HashMap<>();

        Fridge fridge = fridgeRepository.findById(fridgeId).get();
        FridgeDTO fridgeDTO = fridgeService.castFridgeDTO(fridge);
        Set<GroceryItemFridge> groceryItemFridge = fridge.getGroceries();


        GroceryItemFridgeDTO currentGroceryItemFridgeDTO;

        for (GroceryItemFridge gif : groceryItemFridge) {

            currentGroceryItemFridgeDTO = new GroceryItemFridgeDTO();
            currentGroceryItemFridgeDTO.setAmount(gif.getAmount());
            currentGroceryItemFridgeDTO.setExpirationDate(gif.getExpirationDate());
            currentGroceryItemFridgeDTO.setPurchaseDate(gif.getPurchaseDate());
            currentGroceryItemFridgeDTO.setFridgeDTO(fridgeDTO);
            currentGroceryItemFridgeDTO.setGroceryItem(groceryItemService.castGroceryItemDto(gif.getGroceryItem()));


            map.put(gif.getGroceryItem().getGroceryItemId(), currentGroceryItemFridgeDTO);
        }

        return map;
    }


    private List<List<GroceryItemRecipeDTO>> getAllRecipeList() {

        Set<Recipe> allRecipes = recipeRepository.getAllRecipes();

        Set<GroceryItemRecipe> allGroceryItemRecipe;

        List<List<GroceryItemRecipeDTO>> GroceryItemRecipeDTOList = new ArrayList<>();
        List<GroceryItemRecipeDTO> currentGIRDTOList;

        GroceryItemRecipeDTO currentGIRDTO;
        RecipeDTO currentRecipeDTO;


        for (Recipe recipe : allRecipes) {
            allGroceryItemRecipe = recipe.getGroceries();
            currentGIRDTOList = new ArrayList<>();


            currentRecipeDTO = RecipeDTO.builder()
                    .recipe_id(recipe.getRecipe_id())
                    .name(recipe.getName())
                    .description(recipe.getDescription())
                    .build();

            for(GroceryItemRecipe groceryItemRecipe : allGroceryItemRecipe) {
                currentGIRDTO = new GroceryItemRecipeDTO();
                currentGIRDTO.setAmount(groceryItemRecipe.getAmount());
                currentGIRDTO.setRecipe(currentRecipeDTO);
                currentGIRDTO.setGroceryItem(groceryItemService.castGroceryItemDto(
                        groceryItemRecipeRepository.findById(recipe.getRecipe_id())
                                .get().getGroceryItem()
                ));
                currentGIRDTOList.add(currentGIRDTO);
            }

            GroceryItemRecipeDTOList.add(currentGIRDTOList);
        }

        return GroceryItemRecipeDTOList;
    }


    /**
     * Compares the contents of a HashMap representing the fridge to a list of grocery items required to make a recipe,
     * and returns a double value between 0 and 1 indicating the level of matching between the two.
     * This method calculates the ratio of the number of grocery items required for the recipe that are present in the fridge,
     * to the total number of grocery items required for the recipe.
     * @return a double value between 0 and 1 representing the level of matching between the fridge contents and the recipe
     */
    private double compareFridgeAndRecipeList
            (HashMap<Long, GroceryItemFridgeDTO> fridge, List<GroceryItemRecipeDTO> recipe) {
        GroceryItemRecipeDTO recipeItem;
        double fridgeItemAmount;
        double currentPercentage = 0.0;
        double ingredientPercentage;


        for(int i = 0; i<recipe.size(); i++){
            ingredientPercentage = 0.0;
            recipeItem = recipe.get(i);


            if(fridge.get(recipeItem.getGroceryItem().getGroceryItemId()) != null){
                fridgeItemAmount = fridge.get(recipeItem.getGroceryItem().getGroceryItemId()).getAmount();

                // Percentage calculation (Maybe change later)
                if(fridgeItemAmount>=recipeItem.getAmount()) fridgeItemAmount = recipeItem.getAmount();

                ingredientPercentage = fridgeItemAmount / recipeItem.getAmount();
                currentPercentage += ingredientPercentage;
            }

        }

        return currentPercentage/(recipe.size());
    }

    /**
     * Filters the recipes based on the comparison between fridge contents and recipe grocery items. Only returns recipes
     * that meet or exceed the defined threshold in RecipeRecommenderConstants.RECOMMENDATION_THRESHOLD.
     *
     * @param fridge A Hashtable containing the contents of the fridge with key as GroceryItem ID and value as GroceryItemFridgeDTO.
     * @param recipeList A list of lists of GroceryItemRecipeDTO objects representing the recipes and their required grocery items.
     * @return A list of lists of GroceryItemRecipeDTO objects representing the filtered recipes that meet or exceed the threshold.
     */


    private List<List<GroceryItemRecipeDTO>> getRecipesOverThreshold(
                    HashMap<Long, GroceryItemFridgeDTO> fridge,
                    List<List<GroceryItemRecipeDTO>> recipeList) {

        List<List<GroceryItemRecipeDTO>> recipesAboveTheThreshold = new ArrayList<>();

        for(int i = 0; i<recipeList.size(); i++){
            if(compareFridgeAndRecipeList(fridge, recipeList.get(i))>= RecipeRecommenderConstants.RECOMMENDATION_THRESHOLD)
                recipesAboveTheThreshold.add(recipeList.get(i));
        }

        if(recipeList.isEmpty()) return null;
        return recipesAboveTheThreshold;

    }




    /**
     * Calculates the weight of each recipe in the recipesOverThreshold list based on the available fridge items.
     * The weight represents how closely the fridge items match the recipe's required grocery items.
     *
     * @param fridgeMap A HashMap containing the contents of the fridge with key as GroceryItem ID and value as GroceryItemFridgeDTO.
     * @param recipesOverThreshold A list of lists of GroceryItemRecipeDTO objects representing the filtered recipes that meet or exceed the threshold.
     * @return A double array containing the weight of each recipe in the recipesOverThreshold list.
     */
    private double[] WeightList(HashMap<Long, GroceryItemFridgeDTO> fridgeMap, List<List<GroceryItemRecipeDTO>> recipesOverThreshold) {
        int numRecipes = recipesOverThreshold.size();
        double[] weights = new double[numRecipes];

        for (int i = 0; i < numRecipes; i++) {
            List<GroceryItemRecipeDTO> recipe = recipesOverThreshold.get(i);
            double weight = compareFridgeAndRecipeList(fridgeMap, recipe);
            weights[i] = weight;
        }

        return weights;
    }


    /**
     * Sorts the double[] weight and List<List<GroceryItemRecipeDTO>> recipe in descending order based on the values in weight
     * using the Quick Sort algorithm.
     *
     * @param weight A double array containing the weight of each recipe in the recipes list.
     * @param recipes A list of lists of GroceryItemRecipeDTO objects representing the recipes to be sorted.
     * @param low The starting index of the subarray to be sorted.
     * @param high The ending index of the subarray to be sorted.
     */
    public void quickSort(double[] weight, List<List<GroceryItemRecipeDTO>> recipes, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(weight, recipes, low, high);
            quickSort(weight, recipes, low, pivotIndex - 1);
            quickSort(weight, recipes, pivotIndex + 1, high);
        }
    }

    private int partition(double[] weight, List<List<GroceryItemRecipeDTO>> recipes, int low, int high) {
        double pivot = weight[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (weight[j] > pivot) {
                i++;

                // Swap weight[i] and weight[j]
                double tempWeight = weight[i];
                weight[i] = weight[j];
                weight[j] = tempWeight;

                // Swap recipes.get(i) and recipes.get(j)
                List<GroceryItemRecipeDTO> tempRecipe = recipes.get(i);
                recipes.set(i, recipes.get(j));
                recipes.set(j, tempRecipe);
            }
        }

        // Swap weight[i + 1] and weight[high]
        double tempWeight = weight[i + 1];
        weight[i + 1] = weight[high];
        weight[high] = tempWeight;

        // Swap recipes.get(i + 1) and recipes.get(high)
        List<GroceryItemRecipeDTO> tempRecipe = recipes.get(i + 1);
        recipes.set(i + 1, recipes.get(high));
        recipes.set(high, tempRecipe);

        return i + 1;
    }





    }
