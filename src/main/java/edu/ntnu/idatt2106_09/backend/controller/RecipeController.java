package edu.ntnu.idatt2106_09.backend.controller;

import edu.ntnu.idatt2106_09.backend.dto.GroceryItemRecipeDto;
import edu.ntnu.idatt2106_09.backend.dto.recipe.RecipeDTO;
import edu.ntnu.idatt2106_09.backend.dto.recipe.RecipeResponseDTO;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.NotFoundException;
import edu.ntnu.idatt2106_09.backend.model.Recipe;
import edu.ntnu.idatt2106_09.backend.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;


@Slf4j
@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private ModelMapper modelMapper;



    @PostMapping
    @Operation(summary = "Add a new recipe", description = "Add a new recipe to the database")
    public ResponseEntity<Object> addRecipe(@RequestBody RecipeDTO recipeDTO) {
        log.debug("Adding a new Recipe named: {} ", recipeDTO.getName());
        return recipeService.addRecipe(recipeDTO);
    }


    @GetMapping("/simplified/{recipeId}")
    public ResponseEntity<RecipeDTO> getSimplifiedRecipeById(@PathVariable Long recipeId) {
        log.debug("Fetching recipe with id: {}", recipeId);
        Recipe recipe = recipeService.getRecipeById(recipeId)
                .orElseThrow(() -> new NotFoundException("recipe with id " + recipeId + " not found"));
        log.debug("Recipe with id {} found", recipeId);
        RecipeDTO recipeDTO = modelMapper.map(recipe, RecipeDTO.class);
        return new ResponseEntity<>(recipeDTO, HttpStatus.OK);
    }

    @GetMapping("/{recipeId}")
    public ResponseEntity<Object> getRecipeById(@PathVariable Long recipeId) {
        log.debug("Fetching recipe with id: {}", recipeId);
        return recipeService.getRecipeAndAllIngredients(recipeId);
    }

    @GetMapping
    public ResponseEntity<Set<RecipeDTO>> getAllRecipes() {
        log.debug("Retrieving all recipes");
        return recipeService.getAllRecipe();
    }


    @DeleteMapping("/{recipeId}")
    @Operation(summary = "Delete a recipe by ID", description = "Delete a recipe with the specified ID from the database")
    public ResponseEntity<Object> deleteRecipe(@PathVariable Long recipeId) {
        log.debug("Deleting Recipe with id:" + recipeId);
        return recipeService.deleteRecipe(recipeId);
    }

    @GetMapping("/recommender/{fridgeId}")
    public ResponseEntity<List<RecipeResponseDTO>> getRecipeRecommendedList(@PathVariable Long fridgeId) {
        // Retrieve the List<List<GroceryItemRecipeDTO>> based on the fridgeId (implement this in your service layer)
        List<List<GroceryItemRecipeDto>> listOfGroceryItemRecipeLists = recipeService.getRecommendedRecipes(fridgeId);
        // Convert the list of GroceryItemRecipeDTO to the desired format
        List<RecipeResponseDTO> response = recipeService.convertToRecipeResponseDTOList(listOfGroceryItemRecipeLists);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/weekRecommender/{fridgeId}")
    public ResponseEntity<Object> getRecommendedWeekMenuList(@PathVariable Long fridgeId) {
        try{
            List<List<GroceryItemRecipeDto>> listOfGroceryItemRecipeLists = recipeService.retrieveRecommendedWeekMenu(fridgeId);
            List<RecipeResponseDTO> response = recipeService.convertToRecipeResponseDTOList(listOfGroceryItemRecipeLists);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (NotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/missingIngredients/{fridgeId}/{recipeId}")
    public ResponseEntity<Object> getMissingIngredients(@PathVariable Long fridgeId, @PathVariable Long recipeId) {
        try{
            List<RecipeResponseDTO> responseDto = recipeService.getMissingIngredientsAndOriginalRecipe(fridgeId,recipeId);
            return new ResponseEntity<>(responseDto,HttpStatus.OK);
        }
        catch (NotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }


}
