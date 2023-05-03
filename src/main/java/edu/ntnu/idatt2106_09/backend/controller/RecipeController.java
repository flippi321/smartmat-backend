package edu.ntnu.idatt2106_09.backend.controller;

import edu.ntnu.idatt2106_09.backend.dto.FridgeDto;
import edu.ntnu.idatt2106_09.backend.dto.GroceryItemRecipeDto;
import edu.ntnu.idatt2106_09.backend.dto.recipe.RecipeDTO;
import edu.ntnu.idatt2106_09.backend.dto.recipe.RecipeResponseDTO;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.NotFoundException;
import edu.ntnu.idatt2106_09.backend.model.Recipe;
import edu.ntnu.idatt2106_09.backend.service.RecipeService;
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


@Slf4j
@Tag(name = "Recipe Controller", description = "Recipe management operations")
@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/recipes")

public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private ModelMapper modelMapper;



    @PostMapping
    @Operation(summary = "Add a new recipe", description = "Add a new recipe to the database")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Invalid json object provided")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<Object> addRecipe(@RequestBody RecipeDTO recipeDTO) {
        log.debug("Adding a new Recipe named: {} ", recipeDTO.getName());
        return recipeService.addRecipe(recipeDTO);
    }


    @GetMapping("/simplified/{recipeId}")
    @Operation(summary = "Get a simplified recipe by ID", description = "Get a simplified recipe with the specified ID")
    public ResponseEntity<RecipeDTO> getSimplifiedRecipeById(@PathVariable Long recipeId) {
        log.debug("Fetching recipe with id: {}", recipeId);
        Recipe recipe = recipeService.getRecipeById(recipeId)
                .orElseThrow(() -> new NotFoundException("recipe with id " + recipeId + " not found"));
        log.debug("Recipe with id {} found", recipeId);
        RecipeDTO recipeDTO = modelMapper.map(recipe, RecipeDTO.class);
        return new ResponseEntity<>(recipeDTO, HttpStatus.OK);
    }

    @GetMapping("/{recipeId}")
    @Operation(summary = "Get a recipe by ID", description = "Get a recipe with the specified ID along with all its ingredients")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Invalid input provided")
    @ApiResponse(responseCode = "404", description = "Recipe not found")
    public ResponseEntity<Object> getRecipeById(@PathVariable Long recipeId) {
        return recipeService.getRecipeAndAllIngredients(recipeId);
    }

    @GetMapping
    @ApiResponse(responseCode = "200", description = "OK")
    @Operation(summary = "Get all recipes", description = "Get all recipes stored in the database")
    public ResponseEntity<Set<RecipeDTO>> getAllRecipes() {
        log.debug("Retrieving all recipes");
        return recipeService.getAllRecipe();
    }


    @DeleteMapping("/{recipeId}")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Invalid input provided")
    @ApiResponse(responseCode = "404", description = "Recipe not found")
    @Operation(summary = "Delete a recipe by ID", description = "Delete a recipe with the specified ID from the database")
    public ResponseEntity<Object> deleteRecipe(@PathVariable Long recipeId) {
        log.debug("Deleting Recipe with id:" + recipeId);
        return recipeService.deleteRecipe(recipeId);
    }

    @GetMapping("/recommender/{fridgeId}")
    @Operation(summary = "Get a recommended recipe", description = "Receive a Recipe based on the contents of the fridge")
    public ResponseEntity<List<RecipeResponseDTO>> getRecipeRecommendedList(@PathVariable Long fridgeId) {
        // Retrieve the List<List<GroceryItemRecipeDTO>> based on the fridgeId (implement this in your service layer)
        List<List<GroceryItemRecipeDto>> listOfGroceryItemRecipeLists = recipeService.getRecommendedRecipes(fridgeId);
        // Convert the list of GroceryItemRecipeDTO to the desired format
        List<RecipeResponseDTO> response = recipeService.convertToRecipeResponseDTOList(listOfGroceryItemRecipeLists);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/weekRecommender/{fridgeId}")
    @Operation(summary = "Get a list of recommended recipes", description = "Get a list of recommended recipes based on the contents of a fridge")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Invalid input provided")
    @ApiResponse(responseCode = "404", description = "Fridge not found")
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
    @Operation(summary = "Get missing ingredients of a recipe", description = "Returns two items in list. One represents the missing ingredients, while the other is the original recipe")    @ApiResponse(responseCode = "200", description = "Fridge updated successfully", content = @Content(schema = @Schema(implementation = FridgeDto.class)))
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Invalid input provided")
    @ApiResponse(responseCode = "404", description = "Fridge or Recipe not found")
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
