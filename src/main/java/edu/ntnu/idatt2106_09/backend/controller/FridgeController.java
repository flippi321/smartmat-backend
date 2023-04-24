package edu.ntnu.idatt2106_09.backend.controller;

import edu.ntnu.idatt2106_09.backend.exceptionHandling.FridgeNotFoundException;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.GroceryItemNotFoundException;
import edu.ntnu.idatt2106_09.backend.model.Fridge;
import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
import edu.ntnu.idatt2106_09.backend.service.FridgeService;
import edu.ntnu.idatt2106_09.backend.service.GroceryItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/fridges")
public class FridgeController {

    @Autowired
    private FridgeService fridgeService;

    @Autowired
    private GroceryItemService groceryItemService;

    @ExceptionHandler(FridgeNotFoundException.class)
    public ResponseEntity<String> handleFridgeNotFoundException(FridgeNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(GroceryItemNotFoundException.class)
    public ResponseEntity<String> handleGroceryItemNotFoundException(GroceryItemNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    //TODO Burde de over bli brukt på try-catch metoden?

    // POST (Create a new fridge)
    @PostMapping
    public ResponseEntity<Fridge> addFridge(@RequestBody Fridge fridge) {
        log.debug("Adding new fridge");
        if (fridge.getName() == null || fridge.getName().trim().isEmpty()) {
            log.warn("Fridge name cannot be empty");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Fridge newFridge = fridgeService.addFridge(fridge);
        log.info("Fridge created with id: {}", newFridge.getFridgeId());
        return new ResponseEntity<>(newFridge, HttpStatus.CREATED);
    }

    // TODO Vurder dette alternativet
    // GET (Read a fridge by ID)
    @GetMapping("/{fridgeId}")
    public ResponseEntity<Fridge> getFridgeByIdTwo(@PathVariable Long fridgeId) {
        log.debug("Fetching fridge with id: {}", fridgeId);
        Fridge fridge = fridgeService.getFridgeById(fridgeId)
                .orElseThrow(() -> new FridgeNotFoundException("Fridge with id " + fridgeId + " not found"));
        log.debug("Fridge with id {} found", fridgeId);
        return new ResponseEntity<>(fridge, HttpStatus.OK);
    }

    // GET (Read a fridge by ID)
    @GetMapping("/{fridgeId}")
    public ResponseEntity<Fridge> getFridgeById(@PathVariable Long fridgeId) {
        log.debug("Fetching fridge with id: {}", fridgeId);
        Optional<Fridge> fridge = fridgeService.getFridgeById(fridgeId);
        if (fridge.isPresent()) {
            log.debug("Fridge with id {} found", fridgeId);
            return new ResponseEntity<>(fridge.get(), HttpStatus.OK); //TODO Skal .get() brukes her?
        } else {
            log.warn("Fridge with id {} not found", fridgeId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //TODO Metode foreslått av GPT - bruke funksjonell programmering slik?
    /**
    // GET (Read a fridge by ID)
    @GetMapping("/{fridgeId}")
    public ResponseEntity<Fridge> getFridgeById(@PathVariable Long fridgeId) {
        log.info("Fetching fridge with ID: {}", fridgeId);
        Optional<Fridge> fridgeOptional = fridgeService.getFridgeById(fridgeId);
        return fridgeOptional
                .map(fridge -> {
                    log.info("Fridge found: {}", fridge);
                    return new ResponseEntity<>(fridge, HttpStatus.OK);
                })
                .orElseGet(() -> {
                    log.warn("Fridge with ID {} not found", fridgeId);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }
    */

    // GET (Read all fridges)
    @GetMapping
    public ResponseEntity<List<Fridge>> getAllFridges() {
        log.debug("Fetching all fridges");
        List<Fridge> fridges = fridgeService.getAllFridges();
        log.debug("Total number of fridges retrieved: {}", fridges.size());
        return new ResponseEntity<>(fridges, HttpStatus.OK);
    }

    // TODO Vurder dette alternativet
    // PUT (Update a fridge)
    @PutMapping("/{fridgeId}")
    public ResponseEntity<Fridge> updateFridgeTwo(@PathVariable Long fridgeId, @RequestBody Fridge updatedFridge) {
        log.debug("Updating fridge with id: {}", fridgeId);
        Fridge fridge = fridgeService.getFridgeById(fridgeId)
                .orElseThrow(() -> new FridgeNotFoundException("Fridge with id " + fridgeId + " not found for update"));
        fridge.setName(updatedFridge.getName());
        fridge.setHousehold(updatedFridge.getHousehold());
        Fridge savedFridge = fridgeService.updateFridge(fridge);
        log.info("Fridge with id {} updated", fridgeId);
        return new ResponseEntity<>(savedFridge, HttpStatus.OK);
    }

    // PUT (Update a fridge)
    @PutMapping("/{fridgeId}")
    public ResponseEntity<Fridge> updateFridge(@PathVariable Long fridgeId, @RequestBody Fridge updatedFridge) {
        log.debug("Updating fridge with id: {}", fridgeId);
        Optional<Fridge> fridgeToUpdate = fridgeService.getFridgeById(fridgeId);
        if (fridgeToUpdate.isPresent()) {
            Fridge fridge = fridgeToUpdate.get(); //TODO Gir dette mening?
            fridge.setName(updatedFridge.getName());
            fridge.setHousehold(updatedFridge.getHousehold());
            Fridge savedFridge = fridgeService.updateFridge(fridge);
            log.info("Fridge with id {} updated", fridgeId); //TODO Hvorfor er denne .info når den over er .debug?
            return new ResponseEntity<>(savedFridge, HttpStatus.OK);
        }
        log.warn("Fridge with id {} not found for update request", fridgeId);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // TODO Vurder dette alternativet
    // DELETE (Delete a fridge by ID)
    @DeleteMapping("/{fridgeId}")
    public ResponseEntity<Void> deleteFridgeTwo(@PathVariable Long fridgeId) {
        log.debug("Deleting fridge with id: {}", fridgeId);
        Fridge fridge = fridgeService.getFridgeById(fridgeId)
                .orElseThrow(() -> new FridgeNotFoundException("Fridge with id " + fridgeId + " not found for deletion"));
        fridgeService.deleteFridge(fridgeId);
        log.info("Fridge with id {} deleted", fridgeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // DELETE (Delete a fridge by ID)
    @DeleteMapping("/{fridgeId}")
    public ResponseEntity<Void> deleteFridge(@PathVariable Long fridgeId) {
        log.debug("Deleting fridge with id: {}", fridgeId);
        fridgeService.deleteFridge(fridgeId);
        log.info("Fridge with id {} deleted", fridgeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); //TODO Exception handling?
    }

    // POST (Add a grocery item to a fridge)
    @PostMapping("/{fridgeId}/grocery-items")
    public ResponseEntity<Fridge> addGroceryItemToFridge(@PathVariable Long fridgeId, @RequestBody GroceryItem groceryItem) {
        log.debug("Adding grocery item to fridge with id: {}", fridgeId);
        Fridge updatedFridge = fridgeService.addGroceryItemToFridge(fridgeId, groceryItem);
        if (updatedFridge != null) {
            log.info("Grocery item added to fridge with id: {}", fridgeId);
            return new ResponseEntity<>(updatedFridge, HttpStatus.OK);
        }
        log.warn("Couldn't find fridge with id {}", fridgeId);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // TODO Vurder dette alternativet
    // DELETE (Remove a grocery item from a fridge)
    @DeleteMapping("/{fridgeId}/grocery-items/{groceryItemId}")
    public ResponseEntity<Fridge> removeGroceryItemFromFridgeTwo(@PathVariable Long fridgeId, @PathVariable Long groceryItemId) {
        log.debug("Removing grocery item with id {} from fridge with id: {}", groceryItemId, fridgeId);
        GroceryItem groceryItem = groceryItemService.getGroceryItemById(groceryItemId)
                .orElseThrow(() -> new GroceryItemNotFoundException("Grocery item with id " + groceryItemId + " not found for removal"));
        Fridge updatedFridge = fridgeService.removeGroceryItemFromFridge(fridgeId, groceryItem);
                //.orElseThrow(() -> new FridgeNotFoundException("Fridge with id " + fridgeId + " not found for removing grocery item"));
        log.info("Grocery item with id {} removed from fridge with id: {}", groceryItemId, fridgeId);
        return new ResponseEntity<>(updatedFridge, HttpStatus.OK);
    }

    // DELETE (Remove a grocery item from a fridge)
    @DeleteMapping("/{fridgeId}/grocery-items/{groceryItemId}")
    public ResponseEntity<Fridge> removeGroceryItemFromFridge(@PathVariable Long fridgeId, @PathVariable Long groceryItemId) {
        log.debug("Removing grocery item with id {} from fridge with id: {}", groceryItemId, fridgeId);
        Optional<GroceryItem> groceryItemToRemove = groceryItemService.getGroceryItemById(groceryItemId);
        if (groceryItemToRemove.isPresent()) {
            GroceryItem groceryItem = groceryItemToRemove.get();
            Fridge updatedFridge = fridgeService.removeGroceryItemFromFridge(fridgeId, groceryItem);
            if (updatedFridge != null) { //TODO Gir denne mening?
                log.info("Grocery item with id {} removed from fridge with id: {}", groceryItemId, fridgeId);
                return new ResponseEntity<>(updatedFridge, HttpStatus.OK);
            }
            log.warn("Fridge with id {} not found for removing grocery item", fridgeId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        log.warn("The grocery item with id {} was not found in the fridge", groceryItemId);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
