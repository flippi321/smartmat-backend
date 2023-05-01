package edu.ntnu.idatt2106_09.backend.controller;

import edu.ntnu.idatt2106_09.backend.dto.GroceryItemRecipeDto;
import edu.ntnu.idatt2106_09.backend.dto.recipe.RecipeDTO;
import edu.ntnu.idatt2106_09.backend.dto.recipe.RecipeResponseDTO;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.NotFoundException;
import edu.ntnu.idatt2106_09.backend.model.Recipe;
import edu.ntnu.idatt2106_09.backend.service.recipe.RecipeServiceImplementation;
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
    private RecipeServiceImplementation recipeServiceImplementation;

    private ModelMapper modelMapper = new ModelMapper();



    @PostMapping
    public ResponseEntity<RecipeDTO> addRecipe(@RequestBody RecipeDTO recipeDTO) {
        log.debug("Adding a new Recipe named: {} ", recipeDTO.getName());
        return recipeServiceImplementation.addRecipe(recipeDTO);
    }


    @GetMapping("/simplified/{recipeId}")
    public ResponseEntity<RecipeDTO> getSimplifiedRecipeById(@PathVariable Long recipeId) {
        log.debug("Fetching recipe with id: {}", recipeId);
        Recipe recipe = recipeServiceImplementation.getRecipeById(recipeId)
                .orElseThrow(() -> new NotFoundException("recipe with id " + recipeId + " not found"));
        log.debug("Recipe with id {} found", recipeId);
        RecipeDTO recipeDTO = modelMapper.map(recipe, RecipeDTO.class);
        return new ResponseEntity<>(recipeDTO, HttpStatus.OK);
    }

    @GetMapping("/{recipeId}")
    public ResponseEntity<Object> getRecipeById(@PathVariable Long recipeId) {
        log.debug("Fetching recipe with id: {}", recipeId);
        return recipeServiceImplementation.getRecipeAndAllIngredients(recipeId);
    }

    @GetMapping
    public ResponseEntity<Set<RecipeDTO>> getAllRecipes() {
        log.debug("Retrieving all recipes");
        return recipeServiceImplementation.getAllRecipe();
    }


    @DeleteMapping("/{recipeId}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long recipeId) {
        recipeServiceImplementation.deleteRecipe(recipeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/recommender/{fridgeId}")
    public ResponseEntity<List<RecipeResponseDTO>> getRecipeRecommendedList(@PathVariable Long fridgeId) {
        // Retrieve the List<List<GroceryItemRecipeDTO>> based on the fridgeId (implement this in your service layer)
        List<List<GroceryItemRecipeDto>> listOfGroceryItemRecipeLists = recipeServiceImplementation.getRecommendedRecipes(fridgeId);
        // Convert the list of GroceryItemRecipeDTO to the desired format
        List<RecipeResponseDTO> response = recipeServiceImplementation.convertToRecipeResponseDTO(listOfGroceryItemRecipeLists);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
