package edu.ntnu.idatt2106_09.backend.controller;

import edu.ntnu.idatt2106_09.backend.dto.GroceryItemDto;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.NotFoundException;
import edu.ntnu.idatt2106_09.backend.model.Fridge;
import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
import edu.ntnu.idatt2106_09.backend.model.GroceryItemFridge;
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

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // POST (Add a new grocery item to a fridge)
    @PostMapping("/fridge/{fridgeId}")
    public ResponseEntity<GroceryItem> addGroceryItemToFridge(@PathVariable Long fridgeId, @RequestBody GroceryItem groceryItem) {
        log.debug("Adding new Grocery Item to Fridge with ID: {}", fridgeId);
        if (groceryItem.getName() == null || groceryItem.getName().trim().isEmpty()) {
            log.warn("[x] Grocery Item needs to have a name");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<Fridge> optionalFridge = fridgeService.getFridgeById(fridgeId);
        if (!optionalFridge.isPresent()) {
            log.warn("[x] Fridge with ID {} not found", fridgeId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Fridge fridge = optionalFridge.get();
        GroceryItem newGroceryItem = groceryItemService.addGroceryItemToFridge(groceryItem);
        fridge.addGroceryItem(newGroceryItem);
        fridgeService.updateFridge(fridge);
        log.info("[x] Grocery Item created with id: {}", newGroceryItem.getGroceryItemId());
        return new ResponseEntity<>(newGroceryItem, HttpStatus.CREATED);
    }

    // GET (Get all grocery items in a given fridge)
    @GetMapping("/fridge/{fridgeId}")
    public ResponseEntity<List<GroceryItemDto>> getAllGroceryItemsInFridge(@PathVariable Long fridgeId) {
        log.debug("[x] Fetching all Grocery Items in Fridge with ID: {}", fridgeId);
        Optional<Fridge> optionalFridge = fridgeService.getFridgeById(fridgeId);
        if (!optionalFridge.isPresent()) {
            log.warn("[x] Fridge with ID {} not found", fridgeId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Fridge fridge = optionalFridge.get();
        List<GroceryItemDto> groceryItemsdto = fridge.getGroceries().stream()
                .map(GroceryItemFridge::getGroceryItem)
                .map(groceryItem -> modelMapper.map(groceryItem, GroceryItemDto.class))
                .collect(Collectors.toList());
        log.info("[x] Total number of Grocery Items retrieved: {}", groceryItemsdto.size());
        return new ResponseEntity<>(groceryItemsdto, HttpStatus.OK);
    }

    // GET (Get a grocery item with a given id)
    @GetMapping("/{groceryItemId}")
    public ResponseEntity<GroceryItemDto> getGroceryItemById(@PathVariable Long groceryItemId) {
        log.debug("Fetching Grocery Item with id: {}", groceryItemId);
        GroceryItem groceryItem = groceryItemService.getGroceryItemById(groceryItemId)
                .orElseThrow(() -> new NotFoundException("groceryItem with id " + groceryItemId + " not found"));
        log.info("[x] Grocery Item with id {} found", groceryItemId);

        GroceryItemDto groceryItemDto = modelMapper.map(groceryItem, GroceryItemDto.class);
        return new ResponseEntity<>(groceryItemDto, HttpStatus.OK);
    }

    // PUT (Update a grocery item)
    @PutMapping("/{groceryItemId}")
    public ResponseEntity<GroceryItem> updateGroceryItem(@PathVariable Long groceryItemId, @RequestBody GroceryItem updatedGroceryItem) {
        log.debug("Updating Grocery Item with id: {}", groceryItemId);
        Optional<GroceryItem> groceryItemToUpdate = groceryItemService.getGroceryItemById(groceryItemId);
        if (groceryItemToUpdate.isPresent()) {
            GroceryItem groceryItem = groceryItemToUpdate.get();
            groceryItem.setName(updatedGroceryItem.getName());
            groceryItem.setCategory(updatedGroceryItem.getCategory());
            GroceryItem savedGroceryItem = groceryItemService.updateGroceryItem(groceryItem);
            log.info("[x] Grocery Item with id {} updated", groceryItemId);
            return new ResponseEntity<>(savedGroceryItem, HttpStatus.OK);
        }
        log.warn("[x] Grocery Item with id {} not found for update request", groceryItemId);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // DELETE (Delete a grocery item by ID in a given fridge)
    @DeleteMapping("/{fridgeId}/{groceryItemId}")
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
    }

    // DELETE (Delete all grocery items in a given fridge)
    @DeleteMapping("/{fridgeId}")
    public ResponseEntity<Void> removeAllGroceryItemsFromFridge(@PathVariable Long fridgeId) {
        log.debug("[x] Removing all Grocery Items from Fridge with ID: {}", fridgeId);
        Optional<Fridge> optionalFridge = fridgeService.getFridgeById(fridgeId);
        if (!optionalFridge.isPresent()) {
            log.warn("[x] Fridge with ID {} not found", fridgeId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Fridge fridge = optionalFridge.get();
        Set<GroceryItem> groceryItems = fridge.getGroceries().stream()
                .map(GroceryItemFridge::getGroceryItem)
                .collect(Collectors.toSet());
        for (GroceryItem groceryItem : groceryItems) {
            fridge.removeGroceryItem(groceryItem);
        }
        fridgeService.updateFridge(fridge);
        log.debug("[x] All Grocery Items removed from Fridge with ID {}", fridgeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
