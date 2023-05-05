package edu.ntnu.idatt2106_09.backend.controller;


import edu.ntnu.idatt2106_09.backend.dto.FridgeDto;
import edu.ntnu.idatt2106_09.backend.dto.GroceryItemRecipeDto;
import edu.ntnu.idatt2106_09.backend.dto.recipe.RecipeDTO;
import edu.ntnu.idatt2106_09.backend.dto.recipe.RecipeResponseDTO;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.NotFoundException;
import edu.ntnu.idatt2106_09.backend.model.Recipe;
import edu.ntnu.idatt2106_09.backend.service.recipe.RecipeServiceImplementation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Tag(name = "Recipe Controller", description = "Recipe management operations")
@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/recipes")

public class RecipeController {

    @Autowired
    private RecipeServiceImplementation recipeServiceImplementation;

    @Autowired
    private ModelMapper modelMapper;



    @PostMapping
    @Operation(summary = "Add a new recipe", description = "Add a new recipe to the database")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Invalid json object provided")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<Object> addRecipe(@RequestBody RecipeDTO recipeDTO) {
        log.debug("Adding a new Recipe named: {} ", recipeDTO.getName());
        return recipeServiceImplementation.addRecipe(recipeDTO);
    }


    @GetMapping("/simplified/{recipeId}")
    @Operation(summary = "Get a simplified recipe by ID", description = "Get a simplified recipe with the specified ID")
    public ResponseEntity<RecipeDTO> getSimplifiedRecipeById(@PathVariable Long recipeId) {
        log.debug("Fetching recipe with id: {}", recipeId);
        Recipe recipe = recipeServiceImplementation.getRecipeById(recipeId)
                .orElseThrow(() -> new NotFoundException("recipe with id " + recipeId + " not found"));
        log.debug("Recipe with id {} found", recipeId);
        RecipeDTO recipeDTO = modelMapper.map(recipe, RecipeDTO.class);
        return new ResponseEntity<>(recipeDTO, HttpStatus.OK);
    }

    @GetMapping("/{recipeId}")
    @Operation(summary = "Get a recipe by ID", description = "Get a recipe with the specified ID along with all its ingredients")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Recipe not found")
    public ResponseEntity<Object> getRecipeById(@PathVariable Long recipeId) {
        log.debug("Fetching recipe with id: {}", recipeId);
        return recipeServiceImplementation.getRecipeAndAllIngredients(recipeId);
    }

    @GetMapping
    @ApiResponse(responseCode = "200", description = "OK")
    @Operation(summary = "Get all recipes", description = "Get all recipes stored in the database")
    public ResponseEntity<Set<RecipeDTO>> getAllRecipes() {
        log.debug("Retrieving all recipes");
        return recipeServiceImplementation.getAllRecipe();
    }


    @DeleteMapping("/{recipeId}")
    @ApiResponse(responseCode = "204", description = "No Content")
    @ApiResponse(responseCode = "404", description = "Recipe not found")
    @Operation(summary = "Delete a recipe by ID", description = "Delete a recipe with the specified ID from the database")
    public ResponseEntity<Object> deleteRecipe(@PathVariable Long recipeId) {
        log.debug("Delete recipe by Id called");
        return recipeServiceImplementation.deleteRecipe(recipeId);
    }

    @GetMapping("/recommender/{fridgeId}/{portions}")
    @Operation(summary = "Get a list of recommended recipes", description = "Get a list of recommended recipes based on the contents of a fridge")
    @ApiResponse(responseCode = "204", description = "Ok")
    @ApiResponse(responseCode = "404", description = "Fridge not found")
    public ResponseEntity<Object> getRecipeRecommendedList(@PathVariable Long fridgeId, @PathVariable int portions) {
        try {
            List<List<GroceryItemRecipeDto>> listOfGroceryItemRecipeLists = recipeServiceImplementation.getRecommendedRecipes(fridgeId, portions);
            List<RecipeResponseDTO> response = recipeServiceImplementation.convertToRecipeResponseDTO(listOfGroceryItemRecipeLists);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/weekRecommender/{fridgeId}/{portions}")
    @Operation(summary = "Get a list of 7 recommended recipes", description = "Returns a list of 7 recommended recipes. If there is not any recipes to be recommended, it will also add filler recipes")
    @ApiResponse(responseCode = "204", description = "Ok")
    @ApiResponse(responseCode = "404", description = "Fridge not found")
    public ResponseEntity<Object> getRecommendedWeekMenuList(@PathVariable Long fridgeId, @PathVariable int portions) {
        try{
            List<List<GroceryItemRecipeDto>> listOfGroceryItemRecipeLists = recipeServiceImplementation.retrieveRecommendedWeekMenu(fridgeId, portions);
            List<RecipeResponseDTO> response = recipeServiceImplementation.convertToRecipeResponseDTO(listOfGroceryItemRecipeLists);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (NotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/missingIngredients/{fridgeId}/{recipeId}/{portions}")
    @Operation(summary = "Get a list of both ingredients missing from a recipe and the original recipe", description = "Returns a list which contains a list of ingredients that is missing from a fridge to make a recipe, and also returns the original recipe ")
    @ApiResponse(responseCode = "204", description = "Ok")
    @ApiResponse(responseCode = "404", description = "Recipe or Fridge not found")
    public ResponseEntity<Object> getMissingIngredients(@PathVariable Long fridgeId, @PathVariable Long recipeId,@PathVariable int portions) {
        try{
            List<RecipeResponseDTO> responseDto = recipeServiceImplementation.getMissingIngredientsAndOriginalRecipe(fridgeId,recipeId, portions);
            return new ResponseEntity<>(responseDto,HttpStatus.OK);
        }
        catch (NotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }


}
