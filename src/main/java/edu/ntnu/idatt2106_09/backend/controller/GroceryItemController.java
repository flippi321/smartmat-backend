package edu.ntnu.idatt2106_09.backend.controller;

import edu.ntnu.idatt2106_09.backend.dto.FridgeDto;
import edu.ntnu.idatt2106_09.backend.dto.GroceryItemDto;
import edu.ntnu.idatt2106_09.backend.dto.GroceryItemFridgeDto;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.NotFoundException;
import edu.ntnu.idatt2106_09.backend.model.Fridge;
import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
import edu.ntnu.idatt2106_09.backend.model.GroceryItemFridge;
import edu.ntnu.idatt2106_09.backend.repository.FridgeRepository;
import edu.ntnu.idatt2106_09.backend.repository.GroceryItemFridgeRepository;
import edu.ntnu.idatt2106_09.backend.repository.GroceryItemRepository;
import edu.ntnu.idatt2106_09.backend.service.fridge.FridgeService;
import edu.ntnu.idatt2106_09.backend.service.groceryItem.GroceryItemService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/groceryItems")
public class GroceryItemController {

    @Autowired
    private GroceryItemService groceryItemService ;

    @Autowired
    private FridgeService fridgeService ;

    @Autowired
    private ModelMapper modelMapper;

    private GroceryItemFridgeDto castObject2(GroceryItemFridge groceryItemFridge){
        return modelMapper.map(groceryItemFridge, GroceryItemFridgeDto.class);
    }

    @Autowired
    private GroceryItemRepository groceryItemRepository;
    @Autowired
    private FridgeRepository fridgeRepository;
    @Autowired
    private GroceryItemFridgeRepository groceryItemFridgeRepository;


    // POST (Add a new grocery item to a fridge)
    /*json
    {
    "groceryItem": {
    "grocery_item_id" = 1
    },
    "amount": 2,
    "purchaseDate": "2023-04-25"
    }   */
    @PostMapping("/fridge/{fridgeId}/grocery-item")
    public GroceryItemFridge addGroceryItemToFridge(@RequestBody GroceryItemFridge groceryItemFridge) {
        /*log.debug("[X] Call to add crocery to fridge");
        return groceryItemService.addGroceryItemToFridge(fridgeId, groceryItem);*/
        return groceryItemFridgeRepository.save(groceryItemFridge);
    }

    // GET (Get all grocery items in a given fridge)
    @GetMapping("/fridge/{fridgeId}")
    public ResponseEntity<Set<GroceryItemDto>> getAllGroceryItemsInFridge(@PathVariable("fridgeId") Long fridgeId) {
        log.debug("[X] Call to return all grocery items in a given fridge");
        return groceryItemService.getAllGroceryItemsInFridge(fridgeId);
    }

    @DeleteMapping("/fridge/delete/{fridgeId}")
    public ResponseEntity<Void> deleteAllGroceryItemsInFridge(@PathVariable Long fridgeId) {
        log.debug("[X] Call to delete all groceries in fridge = {}", fridgeId);
        return groceryItemService.deleteAllGroceryItemsInFridge(fridgeId);
    }

    @DeleteMapping("/fridge/deleteItem/{fridgeId}/{groceryItemId}")
    public ResponseEntity<Void> deleteGroceryItemInFridge(@PathVariable Long fridgeId, @PathVariable Long groceryItemId) {
        fridgeRepository.deleteGroceryItemInFridge(fridgeId, groceryItemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //DONE
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

    // DELETE (Delete a grocery item by ID in a given fridge)
    /*@DeleteMapping("/{fridgeId}/{groceryItemId}")
    public ResponseEntity<Void> removeGroceryItemFromFridge(@PathVariable Long fridgeId, @PathVariable Long groceryItemId) {
        log.debug("[x] Removing Grocery Item with ID {} from Fridge with ID: {}", groceryItemId, fridgeId);
        Optional<Fridge> optionalFridge = fridgeService.getFridgeById(fridgeId);
        if (!optionalFridge.isPresent()) {
            log.warn("[x] Fridge with ID {} not found", fridgeId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Fridge fridge = optionalFridge.get();
        Optional<GroceryItem> optionalGroceryItem = groceryItemService.getGroceryItemById(groceryItemId);
        if (!optionalGroceryItem.isPresent()) {
            log.warn("[x] Grocery Item with ID {} not found", groceryItemId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        GroceryItem groceryItem = optionalGroceryItem.get();
        fridge.removeGroceryItem(groceryItem);
        fridgeService.updateFridge(fridge);
        log.debug("[x] Grocery Item with ID {} removed from Fridge with ID {}", groceryItemId, fridgeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }*/

}
