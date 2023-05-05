package edu.ntnu.idatt2106_09.backend.controller;

import edu.ntnu.idatt2106_09.backend.dto.*;
import edu.ntnu.idatt2106_09.backend.dto.recipe.IngredientDTO;
import edu.ntnu.idatt2106_09.backend.service.groceryItem.GroceryItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


@Slf4j
@RestController
@Tag(name = "Grocery Item Controller", description = "Grocery item management operations")
@RequestMapping("/api/groceryItems")
public class GroceryItemController {

    @Autowired
    private GroceryItemService groceryItemService;

    public GroceryItemController(GroceryItemService groceryItemService) {
        this.groceryItemService = groceryItemService;
    }

    @PostMapping("/transfer/{shoppinglistId}/{fridgeId}")
    @Operation(summary = "Transfer grocery items to fridge", description = "Transfers a list of grocery items from shopping list to fridge")
    public void transferGroceryItemsToFridge(@PathVariable("shoppinglistId") Long shoppinglistId, @PathVariable("fridgeId") Long fridgeId, @RequestBody Set<GroceryItemDto> groceryItems) {
        log.debug("[X] Call to remove groceries from shoppinglist and add them to fridge");
        groceryItemService.transferGroceryItemsToFridge(shoppinglistId, fridgeId, groceryItems);
    }




    @PostMapping("/shoppinglist/add/{shoppinglistId}")
    @Operation(summary = "Add grocery items to a shopping list", description = "Adds a list of grocery items to a shopping list with the specified amount")
    public ResponseEntity<Set<GroceryItemDto>> addGroceryItemToShoppinglist(@PathVariable("shoppinglistId") Long shoppinglistId, @RequestBody Set<GroceryItemDto> groceryItems){
        log.debug("[X] Call to add a list of grocery items to shoppinglist");
        return groceryItemService.addGroceryItemsToShoppinglist(shoppinglistId, groceryItems);
    }

    @GetMapping("/shoppinglist/{shoppinglistId}")
    @Operation(summary = "Get all grocery items from a shopping list", description = "Returns all grocery items from a shopping list")
    public ResponseEntity<Set<GroceryItemShoppinglistDto>> getAllGroceryItemsInShoppinglist( @PathVariable("shoppinglistId") Long shoppinglistId){
        log.debug("[X] Call to return all grocery items in a given shoppinglist");
        return groceryItemService.getAllGroceryItemsInShoppinglist(shoppinglistId);
    }

    @GetMapping("/shoppinglist/{shoppinglistId}/{groceryItemId}")
    @Operation(summary = "Get a grocery item from a shopping list", description = "Returns a grocery item by ID from a shopping list")
    public ResponseEntity<GroceryItemShoppinglistDto> getGroceryItemsByIdInShoppinglist(@PathVariable("shoppinglistId") Long shoppinglistId, @PathVariable("groceryItemId") Long groceryItemId){
        log.debug("[X] Call to return aa grocery items by id in a given shoppinglist");
        return groceryItemService.getGroceryItemsByIdInShoppinglist(shoppinglistId, groceryItemId);
    }

    @DeleteMapping("/shoppinglist/deleteAll/{shoppinglistId}")
    @Operation(summary = "Delete all grocery items in a shopping list", description = "Deletes all grocery items in a shopping list")
    public ResponseEntity<Void> deleteAllGroceryItemsInShoppinglist(@PathVariable("shoppinglistId") Long shoppinglistId){
        log.debug("[X] Call to delete all groceries in shoppinglist = {}", shoppinglistId);
        return groceryItemService.deleteAllGroceryItemsInShoppinglist(shoppinglistId);
    }

    @DeleteMapping("/shoppinglist/deleteItems/{shoppinglistId}")
    @Operation(summary = "Remove grocery items from a shopping list", description = "Removes a list of grocery items from a shopping list")
    public void removeGroceryItemsFromShoppinglist(@PathVariable("shoppinglistId") Long shoppinglistId, @RequestBody Set<GroceryItemDto> groceryItems){
        log.debug("[X] Call to delete a list of grocery items from shoppinglist");
        groceryItemService.removeGroceryItemsFromShoppinglist(shoppinglistId, groceryItems);
    }

    @PutMapping("/shoppinglist/updateItem/{shoppinglistId}")
    @Operation(summary = "Update grocery item in shoppinglist", description = "Updates a grocery item with a given id in shoppinglist")
    public ResponseEntity<GroceryItemDto> updateGroceryItemsInShoppinglist(@PathVariable("shoppinglistId") Long shoppinglistId, @RequestBody GroceryItemDto groceryItemDto){
        log.debug("[X] Call to update a given grocery in shoppinglist");
        return groceryItemService.updateGroceryItemInShoppinglist(shoppinglistId, groceryItemDto);
    }





    @PostMapping("/fridge/add/{fridgeId}")
    @Operation(summary = "Add grocery items to a fridge", description = "Add a list of grocery items to a fridge with the specified amount")
    public ResponseEntity<Set<GroceryItemDto>> addGroceryItemsToFridge(@PathVariable("fridgeId") Long fridgeId, @RequestBody Set<GroceryItemDto> groceryItems) {
        log.debug("[X] Call to add grocery to fridge");
        return groceryItemService.addGroceryItemsToFridge(fridgeId, groceryItems);
    }

    @GetMapping("/fridge/{fridgeId}")
    @Operation(summary = "Get all grocery items from a fridge", description = "Returns all grocery items from a fridge")
    public ResponseEntity<Set<GroceryItemFridgeDto>> getAllGroceryItemsInFridge(@PathVariable Long fridgeId) {
        log.debug("[X] Call to return all grocery items in a given fridge");
        return groceryItemService.getAllGroceryItemsInFridge(fridgeId);
    }

    @GetMapping("/fridge/{fridgeId}/{groceryItemId}")
    @Operation(summary = "Get a grocery item from a fridge", description = "Returns a grocery item by ID from a shopping list")
    public ResponseEntity<GroceryItemFridgeDto> getGroceryItemsByIdInFridge(@PathVariable Long fridgeId, @PathVariable Long groceryItemId) {
        log.debug("[X] Call to return aa grocery items by id in a given fridge");
        return groceryItemService.getGroceryItemsByIdInFridge(fridgeId, groceryItemId);
    }

    @DeleteMapping("/fridge/deleteAll/{fridgeId}")
    @Operation(summary = "Delete all grocery items from a fridge", description = "Removes all grocery items from a fridge")
    public ResponseEntity<Void> deleteAllGroceryItemsInFridge(@PathVariable Long fridgeId) {
        log.debug("[X] Call to delete all groceries in fridge = {}", fridgeId);
        return groceryItemService.deleteAllGroceryItemsInFridge(fridgeId);
    }

    @DeleteMapping("/fridge/deleteItem/{fridgeId}")
    @Operation(summary = "Remove grocery items from a fridge", description = "Removes a list of grocery items from a fridge")
    public ResponseEntity<FridgeDto> removeGroceryItemFromFridge(@PathVariable Long fridgeId, @RequestBody GroceryItemDto groceryItemDto) {
        log.debug("[X] Call to delete a given grocery from fridge");
        return groceryItemService.removeGroceryItemFromFridge(fridgeId, groceryItemDto);
    }

    @DeleteMapping("/fridge/removeAmountFromMultipleItems/{fridgeId}")
    @Operation(summary = "Modifies or removes amount from items in fridge based on List given", description = "Removes or modifies amount of multiple groceryItems based on IngredientDto list.")
    public ResponseEntity<Object> removeGroceryItemFromFridgeBasedOnList(@PathVariable Long fridgeId ,
                                                                                      @RequestBody List<IngredientDTO> ingredients) {
        log.debug("[X] Call to delete a list of grocery from fridge");
        return groceryItemService.removeGroceryItemsFromFridge(fridgeId, ingredients);

    }

    @PutMapping("/fridge/updateItem/{fridgeId}")
    @Operation(summary = "Update grocery item in fridge", description = "Updates a grocery item with a given id in fridge")
    public ResponseEntity<GroceryItemDto> updateGroceryItemInFridge(@PathVariable("fridgeId") Long shoppinglistId, @RequestBody GroceryItemDto groceryItemDto){
        log.debug("[X] Call to update a given grocery in fridge");
        return groceryItemService.updateGroceryItemInFridge(shoppinglistId, groceryItemDto);
    }




    @GetMapping("/all")
    @Operation(summary = "Get all the grocery items", description = "Returns all the grocery items")
    public ResponseEntity<Set<GroceryItemDto>>  getAllGroceryItems() {
        log.debug("[X] Call to return all grocery items");
        return groceryItemService.getAllGroceryItems();
    }

    @GetMapping("/{groceryItemId}")
    @Operation(summary = "Get a specific grocery item", description = "Returns a specific grocery item")
    public ResponseEntity<GroceryItemDto> getGroceryItemById(@PathVariable Long groceryItemId) {
        log.debug("[X] Call to return a grocery item by id");
        return groceryItemService.getGroceryItemById(groceryItemId);
    }

    @PutMapping("/update/{groceryItemId}")
    @Operation(summary = "Update a groceryItem", description = "Updates a groceryItem based on the json object given")
    public ResponseEntity<GroceryItemDto> updateGroceryItem(@PathVariable Long groceryItemId, @RequestBody GroceryItemDto updatedGroceryItemDto) {
        log.debug("[X] Call to update a grocery item with id = {}", groceryItemId);
        return groceryItemService.updateGroceryItem(groceryItemId, updatedGroceryItemDto);
    }

    @DeleteMapping("/delete/{groceryItemId}")
    @Operation(summary = "Delete a groceryItem", description = "Deletes a groceryItem based on the groceryyItemId and returns the item deleted")
    public ResponseEntity<GroceryItemDto> deleteGroceryItem(@PathVariable Long groceryItemId) {
        log.debug("[X] Call to delete a grocery item with id = {}", groceryItemId);
        return groceryItemService.deleteGroceryItem(groceryItemId);
    }


}
