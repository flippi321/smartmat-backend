package edu.ntnu.idatt2106_09.backend.service.recipe;

import edu.ntnu.idatt2106_09.backend.config.RecipeRecommenderConstants;
import edu.ntnu.idatt2106_09.backend.controller.RecipeController;
import edu.ntnu.idatt2106_09.backend.dto.*;

import edu.ntnu.idatt2106_09.backend.dto.recipe.IngredientDTO;
import edu.ntnu.idatt2106_09.backend.dto.recipe.RecipeDTO;
import edu.ntnu.idatt2106_09.backend.dto.recipe.RecipeResponseDTO;

import edu.ntnu.idatt2106_09.backend.exceptionHandling.BadRequestException;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.NotFoundException;
import edu.ntnu.idatt2106_09.backend.model.*;
import edu.ntnu.idatt2106_09.backend.repository.FridgeRepository;
import edu.ntnu.idatt2106_09.backend.repository.GroceryItemFridgeRepository;

import edu.ntnu.idatt2106_09.backend.repository.GroceryItemRecipeRepository;
import edu.ntnu.idatt2106_09.backend.repository.RecipeRepository;
import edu.ntnu.idatt2106_09.backend.service.fridge.FridgeServiceImplementation;
import edu.ntnu.idatt2106_09.backend.service.groceryItem.GroceryItemService;
import edu.ntnu.idatt2106_09.backend.service.groceryItem.GroceryItemServiceImplementation;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * The RecipeServiceImplementation class is responsible for implementing the logic for managing Recipes in the
 * application. Its main functionality is generating recipes for the user that are most suited, based on what
 * groceryitems are present in the fridge and their expiration dates.
 */
@Service
@Slf4j
public class RecipeServiceImplementation implements RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private GroceryItemRecipeRepository groceryItemRecipeRepository;

    @Autowired
    private GroceryItemFridgeRepository groceryItemFridgeRepository;

    @Autowired
    private FridgeServiceImplementation fridgeService;

    @Autowired
    private GroceryItemServiceImplementation groceryItemService;

    @Autowired
    private FridgeRepository fridgeRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Made for mocking in 'RecipeServiceTest'
     */
    public RecipeServiceImplementation(RecipeRepository recipeRepository){
        this.recipeRepository = recipeRepository;
    }
    public RecipeServiceImplementation(RecipeRepository recipeRepository, GroceryItemRecipeRepository groceryItemRecipeRepository){
        this.recipeRepository = recipeRepository;
        this.groceryItemRecipeRepository = groceryItemRecipeRepository;

    }

    public RecipeServiceImplementation(GroceryItemFridgeRepository groceryItemFridgeRepository, FridgeRepository fridgeRepository,
                         FridgeServiceImplementation fridgeService, GroceryItemServiceImplementation groceryItemService){
        this.groceryItemFridgeRepository = groceryItemFridgeRepository;
        this.fridgeRepository = fridgeRepository;
        this.fridgeService = fridgeService;
        this.groceryItemService = groceryItemService;

    }

    public RecipeServiceImplementation(GroceryItemFridgeRepository groceryItemFridgeRepository, FridgeRepository fridgeRepository,
                         FridgeServiceImplementation fridgeService, GroceryItemServiceImplementation groceryItemService,
                         ModelMapper modelMapper){
        this.groceryItemFridgeRepository = groceryItemFridgeRepository;
        this.fridgeRepository = fridgeRepository;
        this.fridgeService = fridgeService;
        this.groceryItemService = groceryItemService;
        this.modelMapper = modelMapper;

    }

    public RecipeServiceImplementation(RecipeRepository recipeRepository, GroceryItemServiceImplementation groceryItemService){
        this.recipeRepository = recipeRepository;
        this.groceryItemService = groceryItemService;
    }

    public RecipeServiceImplementation(RecipeRepository recipeRepository, GroceryItemServiceImplementation groceryItemService,
                         ModelMapper modelMapper){
        this.recipeRepository = recipeRepository;
        this.groceryItemService = groceryItemService;
        this.modelMapper = modelMapper;
    }

    public RecipeServiceImplementation(RecipeRepository recipeRepository, ModelMapper modelMapper){
        this.recipeRepository = recipeRepository;
        this.modelMapper = modelMapper;
    }

    public RecipeServiceImplementation() {}

    public RecipeServiceImplementation(RecipeRepository recipeRepository, FridgeRepository fridgeRepository) {
        this.recipeRepository = recipeRepository;
        this.fridgeRepository = fridgeRepository;
    }

    /**
     * Adds a new recipe to the repository and returns a ResponseEntity object containing the added recipe in DTO form.
     *
     * @param recipe The RecipeDTO object containing the recipe data to be added.
     * @return ResponseEntity<Object> containing the added recipe in RecipeDTO form if successful, or an error message
     *         and HTTP status if unsuccessful.
     * @throws Exception If an error occurs while adding the recipe to the repository.
     */
    @Override
    public ResponseEntity<Object> addRecipe(RecipeDTO recipe) {
        try {
            Recipe savedRecipe = recipeRepository.save(modelMapper.map(recipe, Recipe.class));

            return new ResponseEntity<>(modelMapper.map(savedRecipe,RecipeDTO.class), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a recipe by its ID from the repository.
     *
     * @param recipeId The ID of the recipe to be retrieved.
     * @return Optional<Recipe> containing the recipe if found, or an empty Optional if the recipe with the specified ID does not exist.
     */
    @Override
    public Optional<Recipe> getRecipeById(Long recipeId){
        return recipeRepository.findById(recipeId);
    }

    /**
     * Retrieves a recipe and its associated ingredients by the recipe ID.
     *
     * @param recipeId The ID of the recipe to be retrieved along with its ingredients.
     * @return ResponseEntity<Object> containing the recipe and its ingredients in a RecipeResponseDTO format if found,
     *         or an HTTP status of NOT_FOUND if the recipe with the specified ID does not exist.
     * @throws NotFoundException If the recipe with the specified ID is not found in the repository.
     */
    @Override
    public ResponseEntity<Object> getRecipeAndAllIngredients(Long recipeId) {
        try {
            Recipe recipe = recipeRepository.findById(recipeId)
                    .orElseThrow(() -> new NotFoundException("recipe with id " + recipeId + " not found"));

            RecipeResponseDTO recipeResponseDTO = new RecipeResponseDTO();

            recipeResponseDTO.setRecipe_id(recipe.getRecipe_id());
            recipeResponseDTO.setName(recipe.getName());
            recipeResponseDTO.setDescription(recipe.getDescription());
            recipeResponseDTO.setSteps(recipe.getSteps());
            recipeResponseDTO.setImageLink(recipe.getImageLink());

            Set<GroceryItemRecipe> ingredients = groceryItemRecipeRepository.findGroceryItemRecipeByRecipeId(recipeId);

            List<IngredientDTO> ingredientDTOList = new ArrayList<>();
            IngredientDTO currentIngredient;
            for (GroceryItemRecipe gir : ingredients) {
                currentIngredient = new IngredientDTO();
                currentIngredient.setAmount(gir.getAmount());
                currentIngredient.setName(gir.getGroceryItem().getName());
                currentIngredient.setId(gir.getGroceryItem().getGroceryItemId());
                currentIngredient.setUnit(gir.getGroceryItem().getCategory().getUnit());
                ingredientDTOList.add(currentIngredient);
            }
            recipeResponseDTO.setIngredients(ingredientDTOList);

            return new ResponseEntity<>(recipeResponseDTO, HttpStatus.OK);
        } catch (NotFoundException ex) {
            log.warn("[X] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves all the recipes available and returns them as a set of RecipeDTO objects wrapped in a ResponseEntity.
     *
     * @return A ResponseEntity containing a set of RecipeDTO objects representing all the recipes available, with an HTTP status of OK.
     */
    @Override
    public ResponseEntity<Set<RecipeDTO>>  getAllRecipe() {
            Set<Recipe> recipes = recipeRepository.getAllRecipes();

            Set<RecipeDTO> recipeDTOs = recipes.stream()
                    .map(recipe -> modelMapper.map(recipe, RecipeDTO.class))
                    .collect(Collectors.toSet());

            return new ResponseEntity<>(recipeDTOs, HttpStatus.OK);
    }

    /**
     * Deletes the recipe with the given recipeId from the database.
     *
     * @param recipeId the id of the recipe to be deleted
     * @return ResponseEntity<Object> with HTTP status OK and the deleted recipe as a RecipeDTO if the recipe was
     *         successfully deleted, ResponseEntity<Object> with HTTP status NOT_FOUND and an error message if the
     *         recipe with the given recipeId was not found
     */
    @Override
    public ResponseEntity<Object> deleteRecipe(Long recipeId) {
        try {
            Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);
            if (recipeOptional.isPresent()) {
                Recipe recipe = recipeOptional.get();
                RecipeDTO recipeDTO = modelMapper.map(recipe, RecipeDTO.class);
                recipeRepository.deleteById(recipeId);
                log.info("Recipe with id " + recipe.getRecipe_id() + " deleted");
                return new ResponseEntity<>(recipeDTO, HttpStatus.NO_CONTENT);
            } else {
                throw new NotFoundException("recipe with id " + recipeId + " not found");
            }
        } catch (NotFoundException ex) {
            log.warn("[X] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves all grocery items in a fridge and returns them as a HashMap with the grocery item ID as the key
     * and a GroceryItemFridgeAlgoDto object containing the grocery item details as the value.
     *
     * @param fridgeId The ID of the fridge containing the grocery items.
     * @return HashMap<Long, GroceryItemFridgeAlgoDto> containing grocery items in the fridge as GroceryItemFridgeAlgoDto objects, with grocery item IDs as keys.
     * @throws NotFoundException If the fridge with the specified ID is not found in the repository.
     */
    @Override
    public HashMap<Long, GroceryItemFridgeAlgoDto> retrieveFridgeItemsHashMap(long fridgeId) {
        HashMap<Long, GroceryItemFridgeAlgoDto> map = new HashMap<>();

        Optional<Fridge> optionalFridge = fridgeRepository.findById(fridgeId);
        if (optionalFridge.isPresent()) {
            Fridge fridge = optionalFridge.get();
            FridgeDto fridgeDTO = modelMapper.map(fridge,FridgeDto.class);
            Set<GroceryItemFridge> groceryItemFridge = groceryItemFridgeRepository.findAllByFridgeId(fridgeId);

            GroceryItemFridgeAlgoDto currentGroceryItemFridgeDTO;
            double currentAmount;

            for (GroceryItemFridge gif : groceryItemFridge) {
                currentGroceryItemFridgeDTO = new GroceryItemFridgeAlgoDto();
                currentGroceryItemFridgeDTO.setExpirationDate(gif.getExpirationDate());
                currentGroceryItemFridgeDTO.setPurchaseDate(gif.getPurchaseDate());
                currentGroceryItemFridgeDTO.setFridgeDto(fridgeDTO);
                currentGroceryItemFridgeDTO.setGroceryItem(modelMapper.map(gif.getGroceryItem(), GroceryItemDto.class));
                currentGroceryItemFridgeDTO.setTimeStamp(gif.getTimestamp());

                // If statements is used to handle multiple of the same type of groceries
                if(map.get(gif.getGroceryItemId()) == null) currentGroceryItemFridgeDTO.setAmount(gif.getAmount());

                // If the entry already exist. Just update the amount
                else {
                    currentAmount = map.get(gif.getGroceryItemId()).getAmount();
                    currentGroceryItemFridgeDTO.setAmount(gif.getAmount()+currentAmount);

                }
                map.put(gif.getGroceryItem().getGroceryItemId(), currentGroceryItemFridgeDTO);
            }
        } else {
            throw new NotFoundException("Fridge with id " + fridgeId + " not found");
        }
        return map;
    }

    /**
     * Retrieves all recipes and their associated grocery items from the repository, and returns them as a list of lists
     * of GroceryItemRecipeDto objects.
     * Each inner list represents a recipe's grocery items, and the outer list contains all recipes.
     *
     * @return List<List<GroceryItemRecipeDto>> containing all recipes and their associated grocery items as
     *         GroceryItemRecipeDto objects.
     */
    @Override
    public List<List<GroceryItemRecipeDto>> getAllRecipeList() {

        Set<Recipe> allRecipes = recipeRepository.getAllRecipes();
        Set<GroceryItemRecipe> allGroceryItemRecipe;
        List<List<GroceryItemRecipeDto>> groceryItemRecipeDTOList = new ArrayList<>();
        List<GroceryItemRecipeDto> currentGIRDTOList;
        GroceryItemRecipeDto currentGIRDTO;
        RecipeDTO currentRecipeDTO;

        for (Recipe recipe : allRecipes) {
            allGroceryItemRecipe = recipe.getGroceries();
            currentGIRDTOList = new ArrayList<>();

            currentRecipeDTO = RecipeDTO.builder()
                    .recipe_id(recipe.getRecipe_id())
                    .name(recipe.getName())
                    .description(recipe.getDescription())
                    .imageLink(recipe.getImageLink())
                    .steps(recipe.getSteps())
                    .build();

            System.out.println("Image link here !" + recipe.getImageLink());

            for(GroceryItemRecipe groceryItemRecipe : allGroceryItemRecipe) {
                currentGIRDTO = new GroceryItemRecipeDto();
                currentGIRDTO.setAmount(groceryItemRecipe.getAmount());
                currentGIRDTO.setRecipe(currentRecipeDTO);
                currentGIRDTO.setGroceryItem(modelMapper.map(groceryItemRecipe.getGroceryItem(), GroceryItemDto.class));
                currentGIRDTOList.add(currentGIRDTO);
            }
            groceryItemRecipeDTOList.add(currentGIRDTOList);
        }
        return groceryItemRecipeDTOList;
    }

    /**
     * Retrieves a specific recipe and its associated grocery items from the repository, and returns them as a list
     * of GroceryItemRecipeDto objects.
     *
     * @param id The ID of the recipe to be retrieved along with its associated grocery items.
     * @return List<GroceryItemRecipeDto> containing the specified recipe and its associated grocery items as
     *         GroceryItemRecipeDto objects.
     * @throws NotFoundException If the recipe with the specified ID is not found in the repository.
     */
    private List<GroceryItemRecipeDto> getRecipeList(Long id) {

        Recipe recipe = recipeRepository.findById(id).orElseThrow(() -> new NotFoundException("No recipe found with id " + id));
        Set<GroceryItemRecipe> allGroceryItemRecipe;
        List<GroceryItemRecipeDto> groceryItemRecipeDTOList = new ArrayList<>();
        RecipeDTO recipeDTO;
        GroceryItemRecipeDto currentGIRDTO;
        allGroceryItemRecipe = recipe.getGroceries();

        recipeDTO = RecipeDTO.builder()
                .recipe_id(recipe.getRecipe_id())
                .name(recipe.getName())
                .description(recipe.getDescription())
                .imageLink(recipe.getImageLink())
                .steps(recipe.getSteps())
                .build();

        for(GroceryItemRecipe groceryItemRecipe : allGroceryItemRecipe) {
            currentGIRDTO = new GroceryItemRecipeDto();
            currentGIRDTO.setAmount(groceryItemRecipe.getAmount());
            currentGIRDTO.setRecipe(recipeDTO);
            currentGIRDTO.setGroceryItem(modelMapper.map(groceryItemRecipe.getGroceryItem(), GroceryItemDto.class));
            groceryItemRecipeDTOList.add(currentGIRDTO);
        }
        return groceryItemRecipeDTOList;
    }

    /**
     * Compares the contents of a HashMap representing the fridge to a list of grocery items required to make a recipe,
     * and returns a double value between 0 and 1 indicating the level of matching between the two.
     * This method calculates the ratio of the number of grocery items required for the recipe that are present in the
     * fridge, to the total number of grocery items required for the recipe.
     *
     * @return a double value between 0 and 1 representing the level of matching between the fridge contents and the recipe
     */
    @Override
    public double compareFridgeAndRecipeList(Map<Long, GroceryItemFridgeAlgoDto> fridge, List<GroceryItemRecipeDto> recipe) {
        GroceryItemRecipeDto recipeItem;
        double fridgeItemAmount;
        double currentPercentage = 0.0;
        double ingredientPercentage;

        for(int i = 0; i<recipe.size(); i++){
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

    @Override
    public List<List<GroceryItemRecipeDto>> getRecipesOverThreshold (
                    Map<Long, GroceryItemFridgeAlgoDto> fridge,
                    List<List<GroceryItemRecipeDto>> recipeList) {

        List<List<GroceryItemRecipeDto>> recipesAboveTheThreshold = new ArrayList<>();

        for(int i = 0; i<recipeList.size(); i++){
            if(compareFridgeAndRecipeList(fridge, recipeList.get(i))>= RecipeRecommenderConstants.RECOMMENDATION_THRESHOLD)
                recipesAboveTheThreshold.add(recipeList.get(i));
        }
        return recipesAboveTheThreshold;
    }

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
    @Override
    public double[] getWeightListOfRecipeList(Map<Long, GroceryItemFridgeAlgoDto> fridgeMap, List<List<GroceryItemRecipeDto>> recipesOverThreshold) {
        int numRecipes = recipesOverThreshold.size();
        double[] weights = new double[numRecipes];
        LocalDate dateToday = LocalDate.now();

        for (int i = 0; i < numRecipes; i++) {
            List<GroceryItemRecipeDto> recipe = recipesOverThreshold.get(i);
            for(int j = 0; j<recipe.size(); j++) {
                GroceryItemFridgeAlgoDto currentFridgeItem = fridgeMap.get(recipe.get(j).getGroceryItem().getGroceryItemId());
                if(currentFridgeItem != null) weights[i]+=getExpirationDateWeight(dateToday,currentFridgeItem.getExpirationDate());
            }
            double weight = compareFridgeAndRecipeList(fridgeMap, recipe);
            weights[i] = weight;
        }
        return weights;
    }

    /**
     * Sorts the double[] weight and List<List<GroceryItemRecipeDTO>> recipe in descending order based on the values in
     * weight using the Quick Sort algorithm.
     *
     * @param weight A double array containing the weight of each recipe in the recipes list.
     * @param recipes A list of lists of GroceryItemRecipeDTO objects representing the recipes to be sorted.
     * @param low The starting index of the subarray to be sorted.
     * @param high The ending index of the subarray to be sorted.
     */
    @Override
    public void quickSort(double[] weight, List<List<GroceryItemRecipeDto>> recipes, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(weight, recipes, low, high);
            quickSort(weight, recipes, low, pivotIndex - 1);
            quickSort(weight, recipes, pivotIndex + 1, high);
        }
    }

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
    @Override
    public int partition(double[] weight, List<List<GroceryItemRecipeDto>> recipes, int low, int high) {
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
                List<GroceryItemRecipeDto> tempRecipe = recipes.get(i);
                recipes.set(i, recipes.get(j));
                recipes.set(j, tempRecipe);
            }
        }

        // Swap weight[i + 1] and weight[high]
        double tempWeight = weight[i + 1];
        weight[i + 1] = weight[high];
        weight[high] = tempWeight;

        // Swap recipes.get(i + 1) and recipes.get(high)
        List<GroceryItemRecipeDto> tempRecipe = recipes.get(i + 1);
        recipes.set(i + 1, recipes.get(high));
        recipes.set(high, tempRecipe);

        return i + 1;
    }

    /**
     * Retrieves recommended recipes based on the available items in the specified fridge. The recommendations are
     * sorted in descending order of the calculated weight (based on item availability and freshness).
     * Each inner list represents a recipe's grocery items.
     *
     * @param fridgeId The ID of the fridge for which the recommended recipes are to be retrieved.
     * @return List<List<GroceryItemRecipeDto>> A list of lists containing recommended recipes and their associated
     *         grocery items as GroceryItemRecipeDto objects.
     */
    @Override
    public List<List<GroceryItemRecipeDto>> getRecommendedRecipes(Long fridgeId, int portions) {
        HashMap<Long, GroceryItemFridgeAlgoDto> fridge = retrieveFridgeItemsHashMap(fridgeId);
        List<List<GroceryItemRecipeDto>> recipeList = getAllRecipeList();
        adjustAmountForMultipleRecipesBasedOnPortion(recipeList, portions);
        List<List<GroceryItemRecipeDto>> recommendedRecipeList;

        recommendedRecipeList = getRecipesOverThreshold(fridge, recipeList);
        double[] weightList = getWeightListOfRecipeList(fridge, recommendedRecipeList);
        quickSort(weightList, recommendedRecipeList, 0, weightList.length - 1);

        return recommendedRecipeList;
    }

    /**
     * Converts a list of lists containing GroceryItemRecipeDto objects into a list of RecipeResponseDTO objects.
     * Each RecipeResponseDTO object will contain the recipe details and a list of ingredients (IngredientDTO).
     *
     * @param listOfGroceryItemRecipeLists A list of lists containing GroceryItemRecipeDto objects, where each inner
     *                                     list represents a recipe's grocery items.
     * @return List<RecipeResponseDTO> A list of RecipeResponseDTO objects containing the recipe details and a list of
     *                                 ingredients.
     */
    @Override
    public List<RecipeResponseDTO> convertToRecipeResponseDTO(List<List<GroceryItemRecipeDto>> listOfGroceryItemRecipeLists) {
        List<RecipeResponseDTO> response = new ArrayList<>();

        for (List<GroceryItemRecipeDto> groceryItemRecipeList : listOfGroceryItemRecipeLists) {
            RecipeDTO recipe = groceryItemRecipeList.get(0).getRecipe();
            RecipeResponseDTO recipeResponseDTO = new RecipeResponseDTO();
            recipeResponseDTO.setRecipe_id(recipe.getRecipe_id());
            recipeResponseDTO.setName(recipe.getName());
            recipeResponseDTO.setDescription(recipe.getDescription());
            recipeResponseDTO.setImageLink(recipe.getImageLink());
            recipeResponseDTO.setSteps(recipe.getSteps());

            List<IngredientDTO> ingredients = new ArrayList<>();
            for (GroceryItemRecipeDto groceryItemRecipeDTO : groceryItemRecipeList) {
                IngredientDTO ingredientDTO = new IngredientDTO();
                ingredientDTO.setId(groceryItemRecipeDTO.getGroceryItem().getGroceryItemId());
                ingredientDTO.setName(groceryItemRecipeDTO.getGroceryItem().getName());
                ingredientDTO.setAmount(groceryItemRecipeDTO.getAmount());
                ingredientDTO.setUnit(groceryItemRecipeDTO.getGroceryItem().getCategory().getUnit());
                ingredients.add(ingredientDTO);
            }
            recipeResponseDTO.setIngredients(ingredients);
            response.add(recipeResponseDTO);
        }
        return response;
    }

    /**
     * Updates the fridge's grocery items after using a recipe. The fridge is represented by a map where the key is
     * the grocery item ID and the value is a GroceryItemFridgeAlgoDto object. This method adjusts the amounts of
     * each grocery item in the fridge based on the amounts used in the recipe.
     *
     * @param fridge A map representing the fridge's grocery items with the key being the grocery item ID and the value
     *               being a GroceryItemFridgeAlgoDto object.
     * @param recipe A list of GroceryItemRecipeDto objects representing the grocery items used in the recipe.
     */
    @Override
    public void updateFridgeAfterRecipe(Map<Long, GroceryItemFridgeAlgoDto> fridge, List<GroceryItemRecipeDto> recipe) {
        GroceryItemRecipeDto recipeItem;
        double fridgeItemAmount;
        double updatedAmount;

        for (int i = 0; i < recipe.size(); i++) {
            recipeItem = recipe.get(i);
            Long groceryItemId = recipeItem.getGroceryItem().getGroceryItemId();

            if (fridge.get(groceryItemId) != null) {
                fridgeItemAmount = fridge.get(groceryItemId).getAmount();
                updatedAmount = fridgeItemAmount - recipeItem.getAmount();

                if (updatedAmount <= 0) {
                    fridge.remove(groceryItemId);
                } else {
                    fridge.get(groceryItemId).setAmount(updatedAmount);
                }
            }
        }
    }

    /**
     * Retrieves the grocery list for a given recipe. Converts the grocery items in the recipe
     * to a list of GroceryItemRecipeDto objects.
     *
     * @param recipe A Recipe object containing the details of the recipe, including its grocery items.
     * @return A list of GroceryItemRecipeDto objects representing the grocery items in the recipe.
     */
    private List<GroceryItemRecipeDto> getRecipeGroceryList(Recipe recipe) {
        Set<GroceryItemRecipe> allGroceryItemRecipe = recipe.getGroceries();
        List<GroceryItemRecipeDto> groceryItemRecipeDTOList = new ArrayList<>();
        GroceryItemRecipeDto currentGIRDTO;
        RecipeDTO currentRecipeDTO;

        currentRecipeDTO = RecipeDTO.builder()
                .recipe_id(recipe.getRecipe_id())
                .name(recipe.getName())
                .description(recipe.getDescription())
                .build();

        for(GroceryItemRecipe groceryItemRecipe : allGroceryItemRecipe) {
            currentGIRDTO = new GroceryItemRecipeDto();
            currentGIRDTO.setAmount(groceryItemRecipe.getAmount());
            currentGIRDTO.setRecipe(currentRecipeDTO);
            currentGIRDTO.setGroceryItem(modelMapper.map(groceryItemRecipe.getGroceryItem(), GroceryItemDto.class));
            groceryItemRecipeDTOList.add(currentGIRDTO);
        }
        return groceryItemRecipeDTOList;
    }

    /**
     * Retrieves a list of recommended recipes for a week based on the ingredients available in the fridge.
     * The method aims to create a week's menu of recipes that can be prepared using the available ingredients.
     * If there are not enough recipes that can be created, filler recipes are added to complete the week's menu.
     *
     * @param fridgeId The ID of the fridge containing the ingredients.
     * @return A list of lists containing GroceryItemRecipeDto objects, representing the week's menu of recipes.
     */
    @Override
    public List<List<GroceryItemRecipeDto>>  retrieveRecommendedWeekMenu(Long fridgeId, int portions) {
        HashMap<Long, GroceryItemFridgeAlgoDto> fridge = retrieveFridgeItemsHashMap(fridgeId);
        List<List<GroceryItemRecipeDto>> recipeList = getAllRecipeList();
        adjustAmountForMultipleRecipesBasedOnPortion(recipeList, portions);

        List<List<GroceryItemRecipeDto>> weekMenu = new ArrayList<>();
        List<Long> recipeIdUsed = new ArrayList<>();
        int originalRecipeListSize = recipeList.size();

        int amountOfRecipesCreated = 0;

        for (int i = 0; i < 7; i++) {
            List<List<GroceryItemRecipeDto>> recommendedRecipeList = getRecipesOverThreshold(fridge, recipeList);
            double[] weightList = getWeightListOfRecipeList(fridge, recommendedRecipeList);
            quickSort(weightList, recommendedRecipeList, 0, weightList.length - 1);
            if(recommendedRecipeList.size()<=i) break;
             else {
                weekMenu.add(recommendedRecipeList.get(0));
                recipeIdUsed.add(recommendedRecipeList.get(0).get(0).getRecipe().getRecipe_id());
                updateFridgeAfterRecipe(fridge, weekMenu.get(i));
                recipeList.remove(recommendedRecipeList.get(0));
                amountOfRecipesCreated++;
            }
        }

        // Add filler recipes if there is none that can be created from the ingredients from the fridge
        Long currentRecipeId;
        List<GroceryItemRecipeDto> foundRecipe;

        for(int i = 0; i<(7-amountOfRecipesCreated); i++){
            currentRecipeId = getRandomNumberAndExcludeSome(1L, originalRecipeListSize + 0L, recipeIdUsed);
            foundRecipe = getRecipeList(currentRecipeId);
            weekMenu.add(foundRecipe);
            recipeIdUsed.add(currentRecipeId);
        }
        return weekMenu;
    }

    /**
     * Will compare fridge and recipe and returns a List of GroceryItemRecipeDto which will represent the missing
     * items to create the specific recipe.
     *
     * @param fridge A hashmap representing the fridge with grocery items
     * @param recipe The Recipe we are comparing
     * @return A list of Grocery items the fridge is missing
     */
    public List<GroceryItemRecipeDto> getMissingIngredient(HashMap<Long, GroceryItemFridgeAlgoDto> fridge,
                                                           Recipe recipe, int portion) {
        List<GroceryItemRecipeDto> groceryItemRecipe = getRecipeGroceryList(recipe);
        adjustAmountBasedOnPortion(groceryItemRecipe, portion);
        List<GroceryItemRecipeDto> missingRequiredGroceries = new ArrayList<>();
        GroceryItemRecipeDto currentGroceryItemDto;
        GroceryItemRecipeDto missingGroceryItemAmount;
        GroceryItemFridgeAlgoDto currentFridgeItem;

        for(int i = 0; i<groceryItemRecipe.size(); i++) {
            currentGroceryItemDto = groceryItemRecipe.get(i);
            missingGroceryItemAmount = new GroceryItemRecipeDto();
            missingGroceryItemAmount.setGroceryItem(currentGroceryItemDto.getGroceryItem());
            missingGroceryItemAmount.setRecipe(currentGroceryItemDto.getRecipe());

            //Check fridge for item
            currentFridgeItem = fridge.get(currentGroceryItemDto.getGroceryItem().getGroceryItemId());
            // If the value is null it means there items
            if(currentFridgeItem==null) {
                missingGroceryItemAmount.setAmount(currentGroceryItemDto.getAmount());
                missingRequiredGroceries.add(missingGroceryItemAmount);
            }
            else if(0>(currentFridgeItem.getAmount()-currentGroceryItemDto.getAmount())) {
                missingGroceryItemAmount.setAmount(currentGroceryItemDto.getAmount() - currentFridgeItem.getAmount());
                missingRequiredGroceries.add(missingGroceryItemAmount);
            }
        }
        return missingRequiredGroceries;
    }

    /**
     * Adjust the all GroceryItemRecipeDto amount based on portion size.
     * Will multiply GroceryItemRecipeDto amount with portion.
     *
     * @param groceryItemRecipeDtos A list of GroceryItemRecipeDto representing the recipe
     * @param portion int representing poriton
     */
    public void adjustAmountBasedOnPortion(List<GroceryItemRecipeDto> groceryItemRecipeDtos, int portion) {
        double currentAmount;
        for(int i = 0; i < groceryItemRecipeDtos.size(); i++) {
            currentAmount = groceryItemRecipeDtos.get(i).getAmount();
            currentAmount *= portion;
            groceryItemRecipeDtos.get(i).setAmount(currentAmount);
        }
    }

    /**
     * Adjust all recipes amount based on portion size.
     * Will multiply GroceryItemRecipeDto amount with portion size
     *
     * @param groceryItemRecipeDtos A double nested listed representing a RecipeList
     * @param portion int representing portion
     */
    public void adjustAmountForMultipleRecipesBasedOnPortion(List<List<GroceryItemRecipeDto>> groceryItemRecipeDtos, int portion) {
        for(int i = 0; i < groceryItemRecipeDtos.size(); i++) {
            adjustAmountBasedOnPortion(groceryItemRecipeDtos.get(i), portion);
        }
    }

    /**
     * Return a random long between min and max, excluding numbers from the specified idList.
     *
     * @param min     the minimum value for the random number (inclusive)
     * @param max     the maximum value for the random number (exclusive)
     * @param idList  a list of Long values to be excluded from the random number generation
     * @return        a random Long between min and max, excluding numbers from idList
     * @throws BadRequestException if the size of idList is greater than the range between min and max
     */
    public Long getRandomNumberAndExcludeSome(Long min, Long max, List<Long> idList){
        if(idList.size()>(max-min)) throw new BadRequestException("getRandomRecipeAndIgnoreSomeId given too large idList");
        Random random = new Random();
        Long randomNumber;

        do {
            randomNumber = random.nextLong(max-min) + min;
        } while (idList.contains(randomNumber));

        return randomNumber;
    }

    /**
     * Return a list containing two RecipeResponseDto. The first Dto represents the missing ingredients while
     * the second Dto represents the original Recipe.
     *
     * @param fridgeId the id of the fridge
     * @param recipeId the id of the recipe
     * @return a list containing two RecipeResponseDto: the first Dto contains the missing ingredients, and the second
     *         Dto contains the original Recipe
     */
    public List<RecipeResponseDTO> getMissingIngredientsAndOriginalRecipe(Long fridgeId, Long recipeId, int portion){
        List<RecipeResponseDTO> response = new ArrayList<>();

        HashMap<Long, GroceryItemFridgeAlgoDto> fridge = retrieveFridgeItemsHashMap(fridgeId);
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() ->  new NotFoundException("No Recipe with id "
                + recipeId));

        // Create the Missing Ingredient Response
        RecipeResponseDTO missingIngredientsResponse = new RecipeResponseDTO();
        List<GroceryItemRecipeDto> ingredients = getMissingIngredient(fridge,recipe,portion);
        List<IngredientDTO> ingredientList = new ArrayList<>();
        IngredientDTO currentIngredient;

        for(int i = 0; i<ingredients.size(); i++) {
            currentIngredient = new IngredientDTO();
            currentIngredient.setName(ingredients.get(i).getGroceryItem().getName());
            currentIngredient.setUnit(ingredients.get(i).getGroceryItem().getCategory().getUnit());
            currentIngredient.setAmount(ingredients.get(i).getAmount());
            currentIngredient.setId(ingredients.get(i).getGroceryItem().getGroceryItemId());
            ingredientList.add(currentIngredient);
        }
        missingIngredientsResponse.setIngredients(ingredientList);
        missingIngredientsResponse.setName("Missing Ingredients of: " + recipe.getName());
        missingIngredientsResponse.setDescription("Missing items");
        missingIngredientsResponse.setRecipe_id(recipeId);

        response.add(missingIngredientsResponse);

        //The original recipe
        RecipeResponseDTO originalRecipe = new RecipeResponseDTO();

        originalRecipe.setRecipe_id(recipe.getRecipe_id());
        originalRecipe.setName(recipe.getName());
        originalRecipe.setDescription(recipe.getDescription());
        originalRecipe.setImageLink(recipe.getImageLink());
        originalRecipe.setSteps(recipe.getSteps());
        Set<GroceryItemRecipe> originalIngredients = groceryItemRecipeRepository.findGroceryItemRecipeByRecipeId(recipeId);

        ingredientList = new ArrayList<>();

        for (GroceryItemRecipe gir : originalIngredients) {
            currentIngredient = new IngredientDTO();
            currentIngredient.setAmount(gir.getAmount()*portion);
            currentIngredient.setName(gir.getGroceryItem().getName());
            currentIngredient.setId(gir.getGroceryItem().getGroceryItemId());
            currentIngredient.setUnit(gir.getGroceryItem().getCategory().getUnit());
            ingredientList.add(currentIngredient);
        }
        originalRecipe.setIngredients(ingredientList);
        response.add(originalRecipe);

        return response;
    }

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
    @Override
    public double getExpirationDateWeight(LocalDate dateToday, LocalDate expirationDate) {
        long diff = ChronoUnit.DAYS.between(dateToday, expirationDate);

        double weight;

        if (diff >= 0) {
            // Item has not expired yet
            weight = 1 / (1 + (RecipeRecommenderConstants.EXPIRATION_WEIGHT_CONSTANT * diff));
        } else {
            // Item has expired
            weight = 1 + (-RecipeRecommenderConstants.EXPIRED_WEIGHT_CONSTANT * diff);
        }
        return weight;
    }
}
