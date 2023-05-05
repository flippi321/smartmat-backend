package edu.ntnu.idatt2106_09.backend.service.groceryItem;

import edu.ntnu.idatt2106_09.backend.dto.*;
import edu.ntnu.idatt2106_09.backend.dto.recipe.IngredientDTO;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.NotFoundException;
import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
import org.hibernate.HibernateException;
import org.springframework.dao.DataAccessException;
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

    /**
     * Transfers one or more GroceryItems from a Shoppinglist to a Fridge.
     *
     * @param shoppinglistId A Long representing the ID of the Shoppinglist to transfer the GroceryItems from.
     * @param fridgeId A Long representing the ID of the Fridge to transfer the GroceryItems to.
     * @param groceryItems A set of grocery items representing to be transferred.
     * @throws NotFoundException if the Shoppinglist, Fridge, or GroceryItem with the given ID does not exist in the database.
     * @throws DataAccessException if an error occurs while accessing the database.
     * @throws HibernateException if an error occurs while Hibernate is executing a query or interacting with the database.
     * @throws Exception if an unknown exception occurs.
     */
    public void transferGroceryItemsToFridge(Long shoppinglistId, Long fridgeId, Set<GroceryItemDto> groceryItems);


    //BELOW ARE CRUD METHODS FOR GROCERY ITEM IN RELATION TO A SHOPPINGLIST

    /**
     * Adds one or more GroceryItems to a Shoppinglist and returns a ResponseEntity containing a Set of the added GroceryItemDtos.
     *
     * @param shoppinglistId A Long representing the ID of the Shoppinglist to add the GroceryItems to.
     * @param groceryItems A Set of GroceryItemDtos representing the GroceryItems to add to the Shoppinglist.
     * @return A ResponseEntity containing a Set of the added GroceryItemDtos and a status code indicating the success of the operation.
     * @throws NotFoundException if the Shoppinglist or GroceryItem with the given ID does not exist in the database.
     */
    public ResponseEntity<Set<GroceryItemDto>> addGroceryItemsToShoppinglist(Long shoppinglistId, Set<GroceryItemDto> groceryItems);

    /**
     * Retrieves all grocery items in a shopping list and returns them as a set of GroceryItemShoppinglistDto objects.
     *
     * @param shoppinglistId the ID of the shopping list to retrieve grocery items from
     * @return ResponseEntity<Set<GroceryItemShoppinglistDto>> containing the set of GroceryItemShoppinglistDto objects and a HTTP status code indicating success or failure of the operation
     * @throws NotFoundException if the shopping list with the given ID is not found in the database
     */
    public ResponseEntity<Set<GroceryItemShoppinglistDto>> getAllGroceryItemsInShoppinglist(Long shoppinglistId);

    /**
     * This method retrieves a GroceryItemShoppinglistDto object for a given grocery item id and shopping list id.
     *
     * @param shoppinglistId The id of the shopping list to search in.
     * @param groceryItemId The id of the grocery item to retrieve.
     * @return A ResponseEntity containing a GroceryItemShoppinglistDto object if the grocery item is found in the shopping list,
     *         or a NOT_FOUND HttpStatus if the item or list is not found.
     * @throws NotFoundException if the grocery item with the given id is not found in the shopping list.
     */
    public ResponseEntity<GroceryItemShoppinglistDto> getGroceryItemsByIdInShoppinglist(Long shoppinglistId, Long groceryItemId);

    /**
     * Deletes all grocery items from the shopping list with the provided ID.
     * @param shoppinglistId the ID of the shopping list to delete grocery items from
     * @return a ResponseEntity with a status code of NO_CONTENT if the operation was successful, or a status code of NOT_FOUND if the shopping list with the provided ID was not found
     * @throws NotFoundException if the shopping list with the provided ID was not found
     */
    public ResponseEntity<Void> deleteAllGroceryItemsInShoppinglist(Long shoppinglistId);

    /**
     * Removes a grocery item from a shopping list.
     *
     * @param shoppinglistId the ID of the shopping list to remove the grocery item from
     * @param groceryItems  the grocery items to be removed
     * @return a ResponseEntity with a ShoppinglistDto representing the updated shopping list or NOT_FOUND if either the shopping list or grocery item is not found
     * @throws NotFoundException if the grocery item with the given ID is not found
     */
    public void removeGroceryItemsFromShoppinglist(Long shoppinglistId,  Set<GroceryItemDto> groceryItems);

    /**
     * Updates a grocery item in a shopping list with the given id. Replaces the amount of the grocery item with the
     * given amount, removes the grocery item from the list with the given timestamp, and adds the grocery item back to
     * the list with the updated amount. If an actual shelf life is not provided or is 0, sets the actual shelf life to
     * the expected shelf life of the grocery item. Saves the updated shopping list to the database and returns the
     * updated grocery item as a GroceryItemDto object in a ResponseEntity with OK status.
     *
     * @param shoppinglistId the id of the shopping list containing the grocery item to update
     * @param groceryItemDto the GroceryItemDto object containing the updated information for the grocery item
     * @return a ResponseEntity with the updated GroceryItemDto object and OK status if the update is successful
     * @throws NotFoundException if the grocery item or shopping list with the given ids are not found
     */
    public ResponseEntity<GroceryItemDto> updateGroceryItemInShoppinglist(Long shoppinglistId, GroceryItemDto groceryItemDto);


    //BELOW ARE CRUD METHODS FOR GROCERY ITEM IN RELATION TO A FRIDGE

    /**
     * Adds a set of grocery items to the fridge with the given fridgeId.
     *
     * @param fridgeId     the ID of the fridge to add the grocery items to
     * @param groceryItems the set of GroceryItemDto objects to be added to the fridge
     * @return a ResponseEntity containing a Set of GroceryItemDto objects that were successfully added to the fridge
     *         with the given fridgeId, along with a HttpStatus.OK status code, or a ResponseEntity with a HttpStatus.NOT_FOUND
     *         status code if either the fridge with the given fridgeId or a grocery item with one of the IDs in the set of groceryItems
     *         was not found
     */
    public ResponseEntity<Set<GroceryItemDto>> addGroceryItemsToFridge(Long fridgeId, Set<GroceryItemDto> groceryItems);

    /**
     * Retrieve all grocery items in the specified fridge.
     *
     * @param fridgeId ID of the fridge to retrieve grocery items from.
     * @return A ResponseEntity object containing a Set of GroceryItemFridgeDto objects, representing the grocery items in the fridge.
     * @throws NotFoundException If the fridge with the specified ID is not found.
     */
    public ResponseEntity<Set<GroceryItemFridgeDto>> getAllGroceryItemsInFridge(Long fridgeId);

    /**
     * Retrieves a grocery item by its ID from a specific fridge.
     *
     * @param fridgeId The ID of the fridge to search in.
     * @param groceryItemId The ID of the grocery item to retrieve.
     * @return A ResponseEntity containing a GroceryItemFridgeDto object if the grocery item is found in the fridge.
     * @throws NotFoundException If the fridge or grocery item is not found.
     */
    public ResponseEntity<GroceryItemFridgeDto> getGroceryItemsByIdInFridge(Long fridgeId, Long groceryItemId);

    /**
     * Deletes all grocery items in a fridge with a given id.
     *
     * @param fridgeId the id of the fridge whose grocery items are to be deleted
     * @return a ResponseEntity with the HTTP status code NO_CONTENT if the operation was successful,
     * or NOT_FOUND if the fridge with the given id was not found
     * @throws NotFoundException if the fridge with the given id is not found
     */
    public ResponseEntity<Void> deleteAllGroceryItemsInFridge(Long fridgeId);

    /**
     * Removes the given grocery item from the fridge with the specified ID.
     *
     * @param fridgeId the ID of the fridge from which the grocery item should be removed
     * @param groceryItemDto the grocery item to be removed
     * @return a ResponseEntity containing the updated FridgeDto object and an HTTP status code
     * @throws NotFoundException if either the fridge or the grocery item cannot be found
     */
    public ResponseEntity<FridgeDto> removeGroceryItemFromFridge( Long fridgeId, GroceryItemDto groceryItemDto);

    /**
     * Updates a grocery item in the fridge with the given  fridgeId and GroceryItemDto .
     *
     * @param fridgeId the ID of the fridge to update the grocery item in
     * @param groceryItemDto the DTO containing the updated grocery item information
     * @return a ResponseEntity with the updated  GroceryItemDto and HTTP status OK, or HTTP status NOT_FOUND if
     *         either the grocery item or fridge could not be found
     */
    public ResponseEntity<GroceryItemDto> updateGroceryItemInFridge(Long fridgeId, GroceryItemDto groceryItemDto);


    //BELOW ARE CRUD METHODS FOR GROCERY ITEM ALONE

    /**
     * Returns a ResponseEntity with a Set of all GroceryItemDto objects in the database.
     *
     * @return ResponseEntity with a Set of all GroceryItemDto objects.
     *         If there are no grocery items in the database, returns a ResponseEntity with HttpStatus.NO_CONTENT.
     */
    public ResponseEntity<Set<GroceryItemDto>> getAllGroceryItems();

    /**
     * Retrieves a GroceryItem by its ID.
     *
     * @param groceryItemId the ID of the GroceryItem to retrieve.
     * @return a ResponseEntity containing the retrieved GroceryItemDto and HTTP status code 200 OK if successful,
     *         or HTTP status code 404 NOT FOUND if the GroceryItem with the specified ID cannot be found.
     */
    public ResponseEntity<GroceryItemDto> getGroceryItemById(Long groceryItemId);

    /**
     * Update an existing grocery item in the database with the given ID using the provided updated grocery item DTO.
     *
     * @param groceryItemId       the ID of the grocery item to be updated
     * @param updatedGroceryItemDto the DTO representing the updated grocery item data
     * @return a ResponseEntity object with HTTP status code 200 if the update was successful
     * @throws NotFoundException if the grocery item with the given ID does not exist in the database
     */
    public ResponseEntity<GroceryItemDto> updateGroceryItem(Long groceryItemId, GroceryItemDto updatedGroceryItemDto);

    /**
     * Deletes a Grocery Item from the database by the provided ID.
     *
     * @param groceryItemId the ID of the Grocery Item to be deleted
     * @return a ResponseEntity with HTTP status code OK if the Grocery Item is deleted successfully, or with HTTP status code NOT_FOUND if the Grocery Item with the provided ID is not found
     * @throws NotFoundException if the Grocery Item with the provided ID is not found
     */
    public ResponseEntity<GroceryItemDto> deleteGroceryItem(Long groceryItemId);

    /**
     * Removes a list of ingredients from a fridge, identified by the fridgeId. The amount to remove for each ingredient
     * is determined by the amount specified in the IngredientDTO object. Calls the removeAmountFromFridge method for
     * each ingredient to perform the actual removal.
     *
     * @param fridgeId the id of the fridge from which to remove the ingredients
     * @param ingredients a list of IngredientDTO objects containing the id and amount of each ingredient to remove
     * @return a ResponseEntity with NO_CONTENT status if the removal is successful
     * @throws NotFoundException if the fridge with the given id is not found
     */
    public ResponseEntity<Object> removeGroceryItemsFromFridge(Long fridgeId, List<IngredientDTO> ingredients);
}
