package edu.ntnu.idatt2106_09.backend.service;

import edu.ntnu.idatt2106_09.backend.config.RecipeRecommenderConstants;
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
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private GroceryItemRecipeRepository groceryItemRecipeRepository;

    @Autowired
    private GroceryItemFridgeRepository groceryItemFridgeRepository;


    @Autowired
    private FridgeRepository fridgeRepository;

    @Autowired
    private ModelMapper modelMapper;

    public ResponseEntity<Object> addRecipe(RecipeDTO recipe) {
        try {
            Recipe savedRecipe = recipeRepository.save(modelMapper.map(recipe, Recipe.class));

            return new ResponseEntity<>(modelMapper.map(savedRecipe,RecipeDTO.class), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public Optional<Recipe> getRecipeById(Long recipeId){
        return recipeRepository.findById(recipeId);
    }

    public ResponseEntity<Object> getRecipeAndAllIngredients(Long recipeId) {
        try {
            Recipe recipe = recipeRepository.findById(recipeId)
                    .orElseThrow(() -> new NotFoundException("recipe with id " + recipeId + " not found"));

            RecipeResponseDTO recipeResponseDTO = new RecipeResponseDTO();

            recipeResponseDTO.setId(recipe.getRecipe_id());
            recipeResponseDTO.setName(recipe.getName());
            recipeResponseDTO.setDescription(recipe.getDescription());

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
            log.warn("[x] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }


    public ResponseEntity<Set<RecipeDTO>>  getAllRecipe() {

            Set<Recipe> recipes = recipeRepository.getAllRecipes();

            Set<RecipeDTO> recipeDTOs = recipes.stream()
                    .map(recipe -> modelMapper.map(recipe, RecipeDTO.class))
                    .collect(Collectors.toSet());

            return new ResponseEntity<>(recipeDTOs, HttpStatus.OK);

    }

    public ResponseEntity<Object> deleteRecipe(Long recipeId) {
        try {
            Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);
            if (recipeOptional.isPresent()) {
                Recipe recipe = recipeOptional.get();
                RecipeDTO recipeDTO = modelMapper.map(recipe, RecipeDTO.class);
                recipeRepository.deleteById(recipeId);
                return new ResponseEntity<>(recipeDTO, HttpStatus.OK);
            } else {
                throw new NotFoundException("recipe with id " + recipeId + " not found");
            }
        } catch (NotFoundException ex) {
            log.warn("[x] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Need to Change hashmap key from Long to add new values to hashmap. Cant have multiple of the sane product
    private HashMap<Long, GroceryItemFridgeAlgoDto> retrieveFridgeItemsHashMap(long fridgeId) {
        HashMap<Long, GroceryItemFridgeAlgoDto> map = new HashMap<>();

        Optional<Fridge> optionalFridge = fridgeRepository.findById(fridgeId);
        if (optionalFridge.isPresent()) {
            Fridge fridge = optionalFridge.get();
            FridgeDto fridgeDTO = modelMapper.map(fridge,FridgeDto.class);
            Set<GroceryItemFridge> groceryItemFridge = groceryItemFridgeRepository.findAllByFridgeId(fridgeId);

            GroceryItemFridgeAlgoDto currentGroceryItemFridgeDTO;

            for (GroceryItemFridge gif : groceryItemFridge) {
                currentGroceryItemFridgeDTO = new GroceryItemFridgeAlgoDto();
                currentGroceryItemFridgeDTO.setAmount(gif.getAmount());
                currentGroceryItemFridgeDTO.setExpirationDate(gif.getExpirationDate());
                currentGroceryItemFridgeDTO.setPurchaseDate(gif.getPurchaseDate());
                currentGroceryItemFridgeDTO.setFridgeDto(fridgeDTO);
                currentGroceryItemFridgeDTO.setGroceryItem(modelMapper.map(gif.getGroceryItem(), GroceryItemDto.class));

                map.put(gif.getGroceryItem().getGroceryItemId(), currentGroceryItemFridgeDTO);
            }
        } else {
            throw new NotFoundException("Fridge not found");
        }

        return map;
    }



    private List<List<GroceryItemRecipeDto>> getAllRecipeList() {

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
                    .build();

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
     * This method calculates the ratio of the number of grocery items required for the recipe that are present in the fridge,
     * to the total number of grocery items required for the recipe.
     * @return a double value between 0 and 1 representing the level of matching between the fridge contents and the recipe
     */
    private double compareFridgeAndRecipeList
            (HashMap<Long, GroceryItemFridgeAlgoDto> fridge, List<GroceryItemRecipeDto> recipe) {
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
     * Filters the recipes based on the comparison between fridge contents and recipe grocery items. Only returns recipes
     * that meet or exceed the defined threshold in RecipeRecommenderConstants.RECOMMENDATION_THRESHOLD.
     *
     * @param fridge A Hashtable containing the contents of the fridge with key as GroceryItem ID and value as GroceryItemFridgeDTO.
     * @param recipeList A list of lists of GroceryItemRecipeDTO objects representing the recipes and their required grocery items.
     * @return A list of lists of GroceryItemRecipeDTO objects representing the filtered recipes that meet or exceed the threshold.
     */


    private List<List<GroceryItemRecipeDto>> getRecipesOverThreshold(
                    HashMap<Long, GroceryItemFridgeAlgoDto> fridge,
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
     * @param fridgeMap A HashMap containing the contents of the fridge with key as GroceryItem ID and value as GroceryItemFridgeDTO.
     * @param recipesOverThreshold A list of lists of GroceryItemRecipeDTO objects representing the filtered recipes that meet or exceed the threshold.
     * @return A double array containing the weight of each recipe in the recipesOverThreshold list.
     */
    private double[] getWeightListOfRecipeList(HashMap<Long, GroceryItemFridgeAlgoDto> fridgeMap, List<List<GroceryItemRecipeDto>> recipesOverThreshold) {
        int numRecipes = recipesOverThreshold.size();
        double[] weights = new double[numRecipes];

        for (int i = 0; i < numRecipes; i++) {
            List<GroceryItemRecipeDto> recipe = recipesOverThreshold.get(i);
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
    public void quickSort(double[] weight, List<List<GroceryItemRecipeDto>> recipes, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(weight, recipes, low, high);
            quickSort(weight, recipes, low, pivotIndex - 1);
            quickSort(weight, recipes, pivotIndex + 1, high);
        }
    }

    private int partition(double[] weight, List<List<GroceryItemRecipeDto>> recipes, int low, int high) {
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


    public List<List<GroceryItemRecipeDto>> getRecommendedRecipes(Long fridgeId) {
        HashMap<Long, GroceryItemFridgeAlgoDto> fridge = retrieveFridgeItemsHashMap(fridgeId);
        List<List<GroceryItemRecipeDto>> recipeList = getAllRecipeList();
        List<List<GroceryItemRecipeDto>> recommendedRecipeList;

        recommendedRecipeList = getRecipesOverThreshold(fridge, recipeList);
        double[] weightList = getWeightListOfRecipeList(fridge, recommendedRecipeList);
        quickSort(weightList, recommendedRecipeList, 0, weightList.length - 1);


        return recommendedRecipeList;
    }


    public List<RecipeResponseDTO> convertToRecipeResponseDTOList(List<List<GroceryItemRecipeDto>> listOfGroceryItemRecipeLists) {
        List<RecipeResponseDTO> response = new ArrayList<>();

        for (List<GroceryItemRecipeDto> groceryItemRecipeList : listOfGroceryItemRecipeLists) {
            RecipeDTO recipe = groceryItemRecipeList.get(0).getRecipe();
            RecipeResponseDTO recipeResponseDTO = new RecipeResponseDTO();
            recipeResponseDTO.setId(recipe.getRecipe_id());
            recipeResponseDTO.setName(recipe.getName());
            recipeResponseDTO.setDescription(recipe.getDescription());

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

    public void updateFridgeAfterRecipe(Map<Long, GroceryItemFridgeAlgoDto> fridge, List<GroceryItemRecipeDto> recipe) {
        GroceryItemRecipeDto recipeItem;
        int fridgeItemAmount;
        int updatedAmount;

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




    public List<List<GroceryItemRecipeDto>> retrieveRecommendedWeekMenu(Long fridgeId) {
        HashMap<Long, GroceryItemFridgeAlgoDto> fridge = retrieveFridgeItemsHashMap(fridgeId);
        List<List<GroceryItemRecipeDto>> recipeList = getAllRecipeList();
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
     * @param fridge
     * @param recipe
     * @return
     */
    public List<GroceryItemRecipeDto> getMissingIngredient(HashMap<Long, GroceryItemFridgeAlgoDto> fridge,
                                                           Recipe recipe) {

        List<GroceryItemRecipeDto> groceryItemRecipe = getRecipeGroceryList(recipe);
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
            System.out.println(currentFridgeItem==null);
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
     * Return a random long between min and max also excludes idList numbers.
     * @param min
     * @param max
     * @param idList
     * @return
     */
    public Long getRandomNumberAndExcludeSome(Long min, Long max, List<Long> idList){
        if(idList.size()>(max-min)) throw new BadRequestException("getRandomRecipeAndIgnoreSomeId given too large idList");
        Random random = new Random();
        Long randomNumber;

        do{
            randomNumber = random.nextLong(max-min) + min;
        } while (idList.contains(randomNumber));

        return randomNumber;
    }


    /**
     * Return a list containing two RecipeResponseDto. The first Dto represents the missing ingredients while
     * the second Dto represents the original Recipe.
     * @param fridgeId
     * @param recipeId
     * @return
     */
    public List<RecipeResponseDTO> getMissingIngredientsAndOriginalRecipe(Long fridgeId, Long recipeId){
        List<RecipeResponseDTO> response = new ArrayList<>();

        HashMap<Long, GroceryItemFridgeAlgoDto> fridge = retrieveFridgeItemsHashMap(fridgeId);
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() ->  new NotFoundException("No Recipe with id " + recipeId));

        // Create the Missing Ingredient Response
        RecipeResponseDTO missingIngredientsResponse = new RecipeResponseDTO();
        List<GroceryItemRecipeDto> ingredients = getMissingIngredient(fridge,recipe);
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
        missingIngredientsResponse.setId(recipeId);

        response.add(missingIngredientsResponse);


        //The original recipe
        RecipeResponseDTO originalRecipe = new RecipeResponseDTO();


        originalRecipe.setId(recipe.getRecipe_id());
        originalRecipe.setName(recipe.getName());
        originalRecipe.setDescription(recipe.getDescription());
        Set<GroceryItemRecipe> originalIngredients = groceryItemRecipeRepository.findGroceryItemRecipeByRecipeId(recipeId);

        ingredientList = new ArrayList<>();

        for (GroceryItemRecipe gir : originalIngredients) {
            currentIngredient = new IngredientDTO();
            currentIngredient.setAmount(gir.getAmount());
            currentIngredient.setName(gir.getGroceryItem().getName());
            currentIngredient.setId(gir.getGroceryItem().getGroceryItemId());
            currentIngredient.setUnit(gir.getGroceryItem().getCategory().getUnit());
            ingredientList.add(currentIngredient);
        }
        originalRecipe.setIngredients(ingredientList);
        response.add(originalRecipe);


        return response;
    }
}

