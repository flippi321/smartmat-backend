package edu.ntnu.idatt2106_09.backend.service;

import edu.ntnu.idatt2106_09.backend.dto.GroceryItemFridgeDTO;

import edu.ntnu.idatt2106_09.backend.model.Recipe;
import edu.ntnu.idatt2106_09.backend.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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

    // 1. Retrive fridge items
    // 2. Retrive Recipe List
    // 4. Compute Best recipes based on items in fridge
    // 5. Compute recipe list based on exp date


    private HashMap<String, GroceryItemFridgeDTO> retriveFridgeItemsHashMap(){

        HashMap<String, GroceryItemFridgeDTO> map = new HashMap<>();

        return map;
    }

    private void getIngredientSetFromRecipe() {

    }

    private void compareFridgeAndRecipeList(){

    }

    private void WeightListBasedOnExpirationDate() {

    }

    /*
    Bubble sort needs to be modified

    public static void quickSort(double[] arr, ArrayList<ArrayList<Item>> recipes, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(arr, recipes, low, high); // Partition the array
            quickSort(arr, recipes, low, pivotIndex - 1);  // Recursively sort the left subarray
            quickSort(arr, recipes, pivotIndex + 1, high); // Recursively sort the right subarray
        }

    }

    private static int partition(double[] arr, ArrayList<ArrayList<Item>> recipes, int low, int high) {
        double pivot = arr[high];
        int i = low - 1; // Index of smaller element
        for (int j = low; j < high; j++) {
            // If current element is greater than or equal to pivot
            if (arr[j] >= pivot) {
                i++;

                // Swap arr[i] and arr[j]
                double temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;

                ArrayList<Item> recipeTemp = recipes.get(i);
                recipes.set(i, recipes.get(j));
                recipes.set(j, recipeTemp);

            }
        }
                // Swap arr[i + 1] and arr[high]
        double temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;

        ArrayList<Item> recipeTemp = recipes.get(i + 1);
        recipes.set(i + 1, recipes.get(high));
        recipes.set(high, recipeTemp);

        return i + 1;
    }
     */


    }
