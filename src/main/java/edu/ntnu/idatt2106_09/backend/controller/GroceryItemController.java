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
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Grocery Item Controller", description = "Grocery item management operations")
@RequestMapping("/api/groceryItems")
public class GroceryItemController {

    @Autowired
    private GroceryItemService groceryItemService;

    public GroceryItemController(GroceryItemService groceryItemService) {
        this.groceryItemService = groceryItemService;
    }


    //BELOW ARE API CALLS FOR GROCERYITEM IN RELATION TO A SHOPPINGLIST AND A FRIDGE

    // POST (transfer grocery item from fridge to shoppinglist)
    @PostMapping("/transfer/{shoppinglistId}/{fridgeId}")
    @Operation(summary = "Transfer grocery items to fridge", description = "Transfer grocery items from shopping list to fridge")
    public void transferGroceryItemsToFridge(@PathVariable("shoppinglistId") Long shoppinglistId, @PathVariable("fridgeId") Long fridgeId, @RequestBody Set<GroceryItemDto> groceryItems) {
        log.debug("[X] Call to remove groceries from shoppinglist and add them to fridge");
        groceryItemService.transferGroceryItemsToFridge(shoppinglistId, fridgeId, groceryItems);
    }


    //BELOW ARE API CALLS FOR GROCERYITEM IN RELATION TO A SHOPPINGLIST

    // POST (Add new grocery items to a shoppinglist)
    @PostMapping("/shoppinglist/add/{shoppinglistId}")
    @Operation(summary = "Add a list of grocery items to a shopping list", description = "Add a list of grocery items to a shopping list with the specified amount")
    public ResponseEntity<Set<GroceryItemDto>> addGroceryItemToShoppinglist(@PathVariable("shoppinglistId") Long shoppinglistId, @RequestBody Set<GroceryItemDto> groceryItems){
        log.debug("[X] Call to add a list of grocery items to shoppinglist");
        return groceryItemService.addGroceryItemsToShoppinglist(shoppinglistId, groceryItems);
    }

    // GET (Get all grocery items in a given shoppinglist)
    @GetMapping("/shoppinglist/{shoppinglistId}")
    @Operation(summary = "Get all grocery items in a shopping list", description = "Get all grocery items in a shopping list")
    public ResponseEntity<Set<GroceryItemShoppinglistDto>> getAllGroceryItemsInShoppinglist( @PathVariable("shoppinglistId") Long shoppinglistId){
        log.debug("[X] Call to return all grocery items in a given shoppinglist");
        return groceryItemService.getAllGroceryItemsInShoppinglist(shoppinglistId);
    }

    // GET (Get  grocery items whit given ids in a given shoppinglist)
    @GetMapping("/shoppinglist/{shoppinglistId}/{groceryItemId}")
    @Operation(summary = "Get a grocery item by ID in a shopping list", description = "Get a grocery item by ID in a shopping list")
    public ResponseEntity<GroceryItemShoppinglistDto> getGroceryItemsByIdInShoppinglist(@PathVariable("shoppinglistId") Long shoppinglistId, @PathVariable("groceryItemId") Long groceryItemId){
        log.debug("[X] Call to return aa grocery items by id in a given shoppinglist");
        return groceryItemService.getGroceryItemsByIdInShoppinglist(shoppinglistId, groceryItemId);
    }

    // DELETE (delete all grocery items in a shoppinglist)
    @DeleteMapping("/shoppinglist/deleteAll/{shoppinglistId}")
    @Operation(summary = "Delete all grocery items in a shopping list", description = "Delete all grocery items in a shopping list")
    public ResponseEntity<Void> deleteAllGroceryItemsInShoppinglist(@PathVariable("shoppinglistId") Long shoppinglistId){
        log.debug("[X] Call to delete all groceries in shoppinglist = {}", shoppinglistId);
        return groceryItemService.deleteAllGroceryItemsInShoppinglist(shoppinglistId);
    }

    // DELETE (delete a grocery item in a shoppinglist)
    @DeleteMapping("/shoppinglist/deleteItems/{shoppinglistId}")
    @Operation(summary = "Remove a list of grocery items from a shopping list", description = "Remove a list of grocery items from a shopping list")
    public void removeGroceryItemsFromShoppinglist(@PathVariable("shoppinglistId") Long shoppinglistId, @RequestBody Set<GroceryItemDto> groceryItems){
        log.debug("[X] Call to delete a list of grocery items from shoppinglist");
        groceryItemService.removeGroceryItemsFromShoppinglist(shoppinglistId, groceryItems);
    }

    /*
    // DELETE (delete grocery items with given ids in a shoppinglist)
    @DeleteMapping("/shoppinglist/deleteItems/{shoppinglistId}")
    @Operation(summary = "Remove multiple grocery item from a shopping list", description = "Removes multiple grocery items from a shoppinglist by using shoppingListId and a list of groceryItemsId")
    public ResponseEntity<ShoppinglistDto> removeGroceryItemsFromShoppinglist(@PathVariable("shoppinglistId") Long shoppinglistId, @RequestBody Long[] groceryItemIds){
        log.debug("[X] Call to delete a given grocery from shoppinglist");
        return groceryItemService.removeGroceryItemsFromShoppinglist(shoppinglistId, groceryItemIds);
    }
    */


/*
    {
        "groceryItemId": 16,
            "name": "Kylling",
            "expectedShelfLife": 14,
            "actualShelfLife": 0,
            "amount": 18,
            "category": {
        "category": 5,
                "name": "Fisk og sjømat",
                "unit": "kg"
    }
    }

 */
    // UPDATE (update grocery item with given id in a shoppinglist)
    @PutMapping("/shoppinglist/updateItem/{shoppinglistId}")
    @Operation(summary = "Update a given grocery item in shoppinglist", description = "Update a given grocery item in shoppinglist")
    public ResponseEntity<GroceryItemDto> updateGroceryItemsInShoppinglist(@PathVariable("shoppinglistId") Long shoppinglistId, @RequestBody GroceryItemDto groceryItemDto){
        log.debug("[X] Call to update a given grocery in shoppinglist");
        return groceryItemService.updateGroceryItemInShoppinglist(shoppinglistId, groceryItemDto);
    }



    //BELOW ARE API CALLS FOR GROCERYITEM IN RELATION TO A FRIDGE

    // POST (Add new grocery items to a fridge)
    @PostMapping("/fridge/add/{fridgeId}")
    public ResponseEntity<Set<GroceryItemDto>> addGroceryItemsToFridge(@PathVariable("fridgeId") Long fridgeId, @RequestBody Set<GroceryItemDto> groceryItems) {
        log.debug("[X] Call to add grocery to fridge");
        return groceryItemService.addGroceryItemsToFridge(fridgeId, groceryItems);
    }

    // GET (Get all grocery items in a given fridge)
    @GetMapping("/fridge/{fridgeId}")
    @Operation(summary = "Get all grocery items in a fridge", description = "Returns all grocery items in a fridge")
    public ResponseEntity<Set<GroceryItemFridgeDto>> getAllGroceryItemsInFridge(@PathVariable Long fridgeId) {
        log.debug("[X] Call to return all grocery items in a given fridge");
        return groceryItemService.getAllGroceryItemsInFridge(fridgeId);
    }

    // GET (Get grocery items with given ids in a given fridge)
    @GetMapping("/fridge/{fridgeId}/{groceryItemId}")
    public ResponseEntity<GroceryItemFridgeDto> getGroceryItemsByIdInFridge(@PathVariable Long fridgeId, @PathVariable Long groceryItemId) {
        log.debug("[X] Call to return aa grocery items by id in a given fridge");
        return groceryItemService.getGroceryItemsByIdInFridge(fridgeId, groceryItemId);
    }

    // DELETE (delete all grocery items in a fridge)
    @DeleteMapping("/fridge/deleteAll/{fridgeId}")
    @Operation(summary = "Delete all grocery items from a fridge", description = "Removes all grocery items from a fridge")
    public ResponseEntity<Void> deleteAllGroceryItemsInFridge(@PathVariable Long fridgeId) {
        log.debug("[X] Call to delete all groceries in fridge = {}", fridgeId);
        return groceryItemService.deleteAllGroceryItemsInFridge(fridgeId);
    }

    // DELETE (delete a grocery item in a fridge)
    @DeleteMapping("/fridge/deleteItem/{fridgeId}")
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


    /*
    // DELETE (delete grocery items with given ids in a fridge)
    @DeleteMapping("/fridge/deleteItems/{fridgeId}")
    @Operation(summary = "Delete a multiple grocery item from a fridge", description = "Removes multiple grocery item from a fridge by using the fridge id and a list grocery item id")
    public ResponseEntity<FridgeDto> removeGroceryItemsFromFridge(@PathVariable("fridgeId") Long fridgeId, @RequestBody Long[] groceryItemIds){
        log.debug("[X] Call to delete a given grocery from shoppinglist");
        return groceryItemService.removeGroceryItemsFromFridge(fridgeId, groceryItemIds);
    }
     */

    /*
        {
            "groceryItemId": 16,
                "name": "Kylling",
                "expectedShelfLife": 14,
                "actualShelfLife": 0,
                "amount": 18,
                "category": {
            "category": 5,
                    "name": "Fisk og sjømat",
                    "unit": "kg"
        }
        }

     */
    // UPDATE (update grocery item with given id in a fridge)
    @PutMapping("/fridge/updateItem/{fridgeId}")
    public ResponseEntity<GroceryItemDto> updateGroceryItemInFridge(@PathVariable("fridgeId") Long shoppinglistId, @RequestBody GroceryItemDto groceryItemDto){
        log.debug("[X] Call to update a given grocery in fridge");
        return groceryItemService.updateGroceryItemInFridge(shoppinglistId, groceryItemDto);
    }






    //BELOW ARE API CALLS FOR GROCERYITEM ALONE
    // GET (Get all grocery items)
    @GetMapping("/all")
    @Operation(summary = "Get all the grocery items", description = "Returns all the grocery items")
    public ResponseEntity<Set<GroceryItemDto>>  getAllGroceryItems() {
        log.debug("[X] Call to return all grocery items");
        return groceryItemService.getAllGroceryItems();
    }

    //DONE
    // GET (Get a grocery item with a given id)
    @GetMapping("/{groceryItemId}")
    @Operation(summary = "Get a specific grocery item", description = "Returns a specific grocery item")
    public ResponseEntity<GroceryItemDto> getGroceryItemById(@PathVariable Long groceryItemId) {
        log.debug("[X] Call to return a grocery item by id");
        return groceryItemService.getGroceryItemById(groceryItemId);
    }

    // PUT (Update a grocery item)
    @PutMapping("/update/{groceryItemId}")
    @Operation(summary = "Update a groceryItem", description = "Updates a groceryItem based on the json object given")
    public ResponseEntity<GroceryItemDto> updateGroceryItem(@PathVariable Long groceryItemId, @RequestBody GroceryItemDto updatedGroceryItemDto) {
        log.debug("[X] Call to update a grocery item with id = {}", groceryItemId);
        return groceryItemService.updateGroceryItem(groceryItemId, updatedGroceryItemDto);
    }

    // DELETE (Delete a grocery item by ID completely)
    @DeleteMapping("/delete/{groceryItemId}")
    @Operation(summary = "Delete a groceryItem", description = "Deletes a groceryItem based on the groceryyItemId and returns the item deleted")
    public ResponseEntity<GroceryItemDto> deleteGroceryItem(@PathVariable Long groceryItemId) {
        log.debug("[X] Call to delete a grocery item with id = {}", groceryItemId);
        return groceryItemService.deleteGroceryItem(groceryItemId);
    }


}
