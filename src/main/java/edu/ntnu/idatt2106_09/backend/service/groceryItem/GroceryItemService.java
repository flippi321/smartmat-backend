package edu.ntnu.idatt2106_09.backend.service.groceryItem;

import edu.ntnu.idatt2106_09.backend.dto.*;
import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public interface GroceryItemService {


    //BELOW ARE API CALLS FOR GROCERYITEM IN RELATION TO A SHOPPINGLIST AND A FRIDGE
    public ResponseEntity<FridgeDto> transferGroceryItemToFridge(Long shoppinglistId, Long fridgeId, Long groceryItemId);

    //BELOW ARE CRUD METHODS FOR GROCERY ITEM IN RELATION TO A SHOPPINGLIST
    public ResponseEntity<ShoppinglistDto> addGroceryItemToShoppinglist(Long shoppinglistId, Long groceryItemId, int amount);

    public ResponseEntity<Set<GroceryItemShoppinglistDto>> getAllGroceryItemsInShoppinglist(Long shoppinglistId);

    public ResponseEntity<GroceryItemShoppinglistDto> getGroceryItemsByIdInShoppinglist(Long shoppinglistId, Long groceryItemId);

    public ResponseEntity<Void> deleteAllGroceryItemsInShoppinglist(Long shoppinglistId);

    public ResponseEntity<ShoppinglistDto> removeGroceryItemFromShoppinglist( Long shoppinglistId, GroceryItem groceryItem);



    //BELOW ARE CRUD METHODS FOR GROCERY ITEM IN RELATION TO A FRIDGE
    public ResponseEntity<FridgeDto> addGroceryItemToFridge(Long fridgeId, Long groceryItemId, int amount);

    public ResponseEntity<Set<GroceryItemFridgeDto>> getAllGroceryItemsInFridge(Long fridgeId);

    public ResponseEntity<GroceryItemFridgeDto> getGroceryItemsByIdInFridge(Long fridgeId, Long groceryItemId);

    public ResponseEntity<Void> deleteAllGroceryItemsInFridge(Long fridgeId);

    public ResponseEntity<FridgeDto> removeGroceryItemFromFridge( Long fridgeId, GroceryItem groceryItem);


    //BELOW ARE CRUD METHODS FOR GROCERY ITEM ALONE
    public ResponseEntity<Set<GroceryItemDto>>  getAllGroceryItems();

    public ResponseEntity<GroceryItemDto> getGroceryItemById(Long groceryItemId);

    public ResponseEntity<GroceryItemDto> updateGroceryItem(Long groceryItemId, GroceryItemDto updatedGroceryItemDto);

    public ResponseEntity<GroceryItemDto> deleteGroceryItem(Long groceryItemId);
}
