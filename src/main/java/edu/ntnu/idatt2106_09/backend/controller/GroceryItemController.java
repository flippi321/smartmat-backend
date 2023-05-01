package edu.ntnu.idatt2106_09.backend.controller;

import edu.ntnu.idatt2106_09.backend.dto.*;
import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
import edu.ntnu.idatt2106_09.backend.repository.FridgeRepository;
import edu.ntnu.idatt2106_09.backend.repository.GroceryItemRepository;
import edu.ntnu.idatt2106_09.backend.repository.ShoppinglistRepository;
import edu.ntnu.idatt2106_09.backend.service.groceryItem.GroceryItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // POST (Add a new grocery item to a shoppinglist)
    @PostMapping("/transfer/{shoppinglistId}/{fridgeId}")
    @Operation(summary = "Transfer grocery items to fridge", description = "Transfer grocery items from shopping list to fridge")
    public void transferGroceryItemsToFridge(@PathVariable("shoppinglistId") Long shoppinglistId, @PathVariable("fridgeId") Long fridgeId, @RequestBody Long[] groceryItemIds) {
        log.debug("[X] Call to remove groceries from shoppinglist and add them to fridge");
        groceryItemService.transferGroceryItemsToFridge(shoppinglistId, fridgeId, groceryItemIds);
    }


    //BELOW ARE API CALLS FOR GROCERYITEM IN RELATION TO A SHOPPINGLIST

    // POST (Add a new grocery item to a shoppinglist)
    @PostMapping("/shoppinglist/{shoppinglistId}/{groceryItemId}/{amount}")
    @Operation(summary = "Add a grocery item to a shopping list", description = "Add a grocery item to a shopping list with the specified amount")
    public ResponseEntity<ShoppinglistDto> addGroceryItemToShoppinglist(@PathVariable("shoppinglistId") Long shoppinglistID, @PathVariable("groceryItemId") Long groceryItemId, @PathVariable("amount") int amount){
        log.debug("[X] Call to add crocery to shoppinglist");
        return groceryItemService.addGroceryItemToShoppinglist(shoppinglistID, groceryItemId, amount);
    }

    // GET (Get all grocery items in a given fridge)
    @GetMapping("/shoppinglist/{shoppinglistId}")
    @Operation(summary = "Get all grocery items in a shopping list", description = "Get all grocery items in a shopping list")
    public ResponseEntity<Set<GroceryItemShoppinglistDto>> getAllGroceryItemsInShoppinglist( @PathVariable("shoppinglistId") Long shoppinglistId){
        log.debug("[X] Call to return all grocery items in a given shoppinglist");
        return groceryItemService.getAllGroceryItemsInShoppinglist(shoppinglistId);
    }

    @GetMapping("/shoppinglist/{shoppinglistId}/{groceryItemId}")
    @Operation(summary = "Get a grocery item by ID in a shopping list", description = "Get a grocery item by ID in a shopping list")
    public ResponseEntity<GroceryItemShoppinglistDto> getGroceryItemsByIdInShoppinglist(@PathVariable("shoppinglistId") Long shoppinglistId, @PathVariable("groceryItemId") Long groceryItemId){
        log.debug("[X] Call to return aa grocery items by id in a given shoppinglist");
        return groceryItemService.getGroceryItemsByIdInShoppinglist(shoppinglistId, groceryItemId);
    }

    @DeleteMapping("/shoppinglist/deleteAll/{shoppinglistId}")
    @Operation(summary = "Delete all grocery items in a shopping list", description = "Delete all grocery items in a shopping list")
    public ResponseEntity<Void> deleteAllGroceryItemsInShoppinglist(@PathVariable("shoppinglistId") Long shoppinglistId){
        log.debug("[X] Call to delete all groceries in shoppinglist = {}", shoppinglistId);
        return groceryItemService.deleteAllGroceryItemsInShoppinglist(shoppinglistId);
    }

    @DeleteMapping("/shoppinglist/deleteItem/{shoppinglistId}/{groceryItemID}")
    @Operation(summary = "Remove a grocery item from a shopping list", description = "Remove a grocery item from a shopping list")
    public ResponseEntity<ShoppinglistDto> removeGroceryItemFromShoppinglist(@PathVariable("shoppinglistId") Long shoppinglistId, @PathVariable("groceryItemID") Long groceryItemID){
        log.debug("[X] Call to delete a given grocery from shoppinglist");
        return groceryItemService.removeGroceryItemFromShoppinglist(shoppinglistId, groceryItemID);
    }

    @DeleteMapping("/shoppinglist/deleteItems/{shoppinglistId}")
    public ResponseEntity<ShoppinglistDto> removeGroceryItemsFromShoppinglist(@PathVariable("shoppinglistId") Long shoppinglistId, @RequestBody Long[] groceryItemIds){
        log.debug("[X] Call to delete a given grocery from shoppinglist");
        return groceryItemService.removeGroceryItemsFromShoppinglist(shoppinglistId, groceryItemIds);
    }







    //BELOW ARE API CALLS FOR GROCERYITEM IN RELATION TO A FRIDGE

    // POST (Add a new grocery item to a fridge)
    @PostMapping("/fridge/{fridgeId}/{groceryItemId}/{amount}")
    public ResponseEntity<FridgeDto> addGroceryItemToFridge(@PathVariable("fridgeId") Long fridgeId, @PathVariable("groceryItemId") Long groceryItemId, @PathVariable("amount") int amount) {
        log.debug("[X] Call to add crocery to fridge");
        return groceryItemService.addGroceryItemToFridge(fridgeId, groceryItemId, amount);
    }

    // GET (Get all grocery items in a given fridge)
    @GetMapping("/fridge/{fridgeId}")
    public ResponseEntity<Set<GroceryItemFridgeDto>> getAllGroceryItemsInFridge(@PathVariable Long fridgeId) {
        log.debug("[X] Call to return all grocery items in a given fridge");
        return groceryItemService.getAllGroceryItemsInFridge(fridgeId);
    }

    @GetMapping("/fridge/{fridgeId}/{groceryItemId}")
    public ResponseEntity<GroceryItemFridgeDto> getGroceryItemsByIdInFridge(@PathVariable Long fridgeId, @PathVariable Long groceryItemId) {
        log.debug("[X] Call to return aa grocery items by id in a given fridge");
        return groceryItemService.getGroceryItemsByIdInFridge(fridgeId, groceryItemId);
    }

    @DeleteMapping("/fridge/deleteAll/{fridgeId}")
    public ResponseEntity<Void> deleteAllGroceryItemsInFridge(@PathVariable Long fridgeId) {
        log.debug("[X] Call to delete all groceries in fridge = {}", fridgeId);
        return groceryItemService.deleteAllGroceryItemsInFridge(fridgeId);
    }

    @DeleteMapping("/fridge/deleteItem/{fridgeId}/{groceryItemID}")
    public ResponseEntity<FridgeDto> removeGroceryItemFromFridge(@PathVariable Long fridgeId, @PathVariable("groceryItemID") Long groceryItemID) {
        log.debug("[X] Call to delete a given grocery from fridge");
        return groceryItemService.removeGroceryItemFromFridge(fridgeId, groceryItemID);
    }

    @DeleteMapping("/fridge/deleteItems/{fridgeId}")
    public ResponseEntity<FridgeDto> removeGroceryItemsFromFridge(@PathVariable("fridgeId") Long fridgeId, @RequestBody Long[] groceryItemIds){
        log.debug("[X] Call to delete a given grocery from shoppinglist");
        return groceryItemService.removeGroceryItemsFromFridge(fridgeId, groceryItemIds);
    }








    //BELOW ARE API CALLS FOR GROCERYITEM ALONE
    // GET (Get all grocery items)
    @GetMapping("/all")
    public ResponseEntity<Set<GroceryItemDto>>  getAllGroceryItems() {
        log.debug("[X] Call to return all grocery items");
        return groceryItemService.getAllGroceryItems();
    }

    //DONE
    // GET (Get a grocery item with a given id)
    @GetMapping("/{groceryItemId}")
    public ResponseEntity<GroceryItemDto> getGroceryItemById(@PathVariable Long groceryItemId) {
        log.debug("[X] Call to return a grocery item by id");
        return groceryItemService.getGroceryItemById(groceryItemId);
    }

    // PUT (Update a grocery item)
    /* json object updatedGroceryItemDto
    {
    "name": "Iskrem",
    "shelf_life": 0,
    "category": {
        "category": 2
    }
    }*/
    @PutMapping("/update/{groceryItemId}")
    public ResponseEntity<GroceryItemDto> updateGroceryItem(@PathVariable Long groceryItemId, @RequestBody GroceryItemDto updatedGroceryItemDto) {
        log.debug("[X] Call to update a grocery item with id = {}", groceryItemId);
        return groceryItemService.updateGroceryItem(groceryItemId, updatedGroceryItemDto);
    }

    // DELETE (Delete a grocery item by ID completely)
    @DeleteMapping("/delete/{groceryItemId}")
    public ResponseEntity<GroceryItemDto> deleteGroceryItem(@PathVariable Long groceryItemId) {
        log.debug("[X] Call to delete a grocery item with id = {}", groceryItemId);
        return groceryItemService.deleteGroceryItem(groceryItemId);
    }


}
