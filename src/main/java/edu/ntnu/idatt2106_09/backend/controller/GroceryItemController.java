package edu.ntnu.idatt2106_09.backend.controller;

import edu.ntnu.idatt2106_09.backend.dto.*;
import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
import edu.ntnu.idatt2106_09.backend.service.groceryItem.GroceryItemService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


@Slf4j
@RestController
@RequestMapping("/api/groceryItems")
public class GroceryItemController {

    @Autowired
    private GroceryItemService groceryItemService;

    @Autowired
    private ModelMapper modelMapper;

    private GroceryItem castDtoToGroceryItem(GroceryItemDto groceryItemDto) {
        return modelMapper.map(groceryItemDto, GroceryItem.class);
    }


    //BELOW ARE API CALLS FOR GROCERYITEM IN RELATION TO A SHOPPINGLIST AND A FRIDGE

    // POST (Add a new grocery item to a shoppinglist)
    @PostMapping("/transfer/{shoppinglistId}/{fridgeId}/{groceryItemId}")
    public ResponseEntity<FridgeDto> transferGroceryItemToFridge(@PathVariable("shoppinglistId") Long shoppinglistId, @PathVariable("fridgeId") Long fridgeId, @PathVariable("groceryItemId") Long groceryItemId) {
        log.debug("[X] Call to remove a grocery from shoppinglist and ad it to fridge");
        return groceryItemService.transferGroceryItemToFridge(shoppinglistId, fridgeId, groceryItemId);
    }


    //BELOW ARE API CALLS FOR GROCERYITEM IN RELATION TO A SHOPPINGLIST

    // POST (Add a new grocery item to a shoppinglist)
    @PostMapping("/shoppinglist/{shoppinglistId}/{groceryItemId}/{amount}")
    public ResponseEntity<ShoppinglistDto> addGroceryItemToShoppinglist(@PathVariable("shoppinglistId") Long shoppinglistID, @PathVariable("groceryItemId") Long groceryItemId, @PathVariable("amount") int amount){
        log.debug("[X] Call to add crocery to shoppinglist");
        return groceryItemService.addGroceryItemToShoppinglist(shoppinglistID, groceryItemId, amount);
    }

    // GET (Get all grocery items in a given fridge)
    @GetMapping("/shoppinglist/{shoppinglistId}")
    public ResponseEntity<Set<GroceryItemShoppinglistDto>> getAllGroceryItemsInShoppinglist( @PathVariable("shoppinglistId") Long shoppinglistId){
        log.debug("[X] Call to return all grocery items in a given shoppinglist");
        return groceryItemService.getAllGroceryItemsInShoppinglist(shoppinglistId);
    }

    @GetMapping("/shoppinglist/{fridgeId}/{groceryItemId}")
    public ResponseEntity<GroceryItemShoppinglistDto> getGroceryItemsByIdInShoppinglist(@PathVariable("shoppinglistId") Long shoppinglistId, @PathVariable("groceryItemId") Long groceryItemId){
        log.debug("[X] Call to return aa grocery items by id in a given shoppinglist");
        return groceryItemService.getGroceryItemsByIdInShoppinglist(shoppinglistId, groceryItemId);
    }

    @DeleteMapping("/shoppinglist/deleteAll/{shoppinglistId}")
    public ResponseEntity<Void> deleteAllGroceryItemsInShoppinglist(@PathVariable("shoppinglistId") Long shoppinglistId){
        log.debug("[X] Call to delete all groceries in shoppinglist = {}", shoppinglistId);
        return groceryItemService.deleteAllGroceryItemsInFridge(shoppinglistId);
    }

    @DeleteMapping("/shoppinglist/deleteItem/{shoppinglistId}")
    public ResponseEntity<ShoppinglistDto> removeGroceryItemFromShoppinglist(@PathVariable("shoppinglistId") Long shoppinglistId, @RequestBody GroceryItem groceryItem){
        log.debug("[X] Call to delete a given grocery from shoppinglist");
        return groceryItemService.removeGroceryItemFromShoppinglist(shoppinglistId, groceryItem);
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

    @DeleteMapping("/fridge/deleteItem/{fridgeId}")
    public ResponseEntity<FridgeDto> removeGroceryItemFromFridge(@PathVariable Long fridgeId, @RequestBody GroceryItem groceryItem) {
        log.debug("[X] Call to delete a given grocery from fridge");
        return groceryItemService.removeGroceryItemFromFridge(fridgeId, groceryItem);
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
