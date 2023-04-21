package edu.ntnu.idatt2106_09.backend.controller;

import edu.ntnu.idatt2106_09.backend.model.Fridge;
import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
import edu.ntnu.idatt2106_09.backend.service.FridgeService;
import edu.ntnu.idatt2106_09.backend.service.GroceryItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/fridges")
public class FridgeController {

    @Autowired
    private FridgeService fridgeService;

    @Autowired
    private GroceryItemService groceryItemService;

    // POST (Create a new fridge)
    @PostMapping
    public ResponseEntity<Fridge> addFridge(@RequestBody Fridge fridge) {
        Fridge newFridge = fridgeService.addFridge(fridge);
        return new ResponseEntity<>(newFridge, HttpStatus.CREATED);
    }

    // GET (Read a fridge by ID)
    @GetMapping("/{fridgeId}")
    public ResponseEntity<Fridge> getFridgeById(@PathVariable Long fridgeId) {
        Optional<Fridge> fridgeOptional = fridgeService.getFridgeById(fridgeId);
        return fridgeOptional
                .map(fridge -> new ResponseEntity<>(fridge, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // GET (Read all fridges)
    @GetMapping
    public ResponseEntity<List<Fridge>> getAllFridges() {
        List<Fridge> fridges = fridgeService.getAllFridges();
        return new ResponseEntity<>(fridges, HttpStatus.OK);
    }

    // PUT (Update a fridge)
    @PutMapping("/{fridgeId}")
    public ResponseEntity<Fridge> updateFridge(@PathVariable Long fridgeId, @RequestBody Fridge updatedFridge) {
        Optional<Fridge> fridgeOptional = fridgeService.getFridgeById(fridgeId);
        if (fridgeOptional.isPresent()) {
            Fridge fridge = fridgeOptional.get();
            fridge.setName(updatedFridge.getName());
            fridge.setHousehold(updatedFridge.getHousehold());
            Fridge savedFridge = fridgeService.updateFridge(fridge);
            return new ResponseEntity<>(savedFridge, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // DELETE (Delete a fridge by ID)
    @DeleteMapping("/{fridgeId}")
    public ResponseEntity<Void> deleteFridge(@PathVariable Long fridgeId) {
        fridgeService.deleteFridge(fridgeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // POST (Add a grocery item to a fridge)
    @PostMapping("/{fridgeId}/grocery-items")
    public ResponseEntity<Fridge> addGroceryItemToFridge(@PathVariable Long fridgeId, @RequestBody GroceryItem groceryItem) {
        Fridge updatedFridge = fridgeService.addGroceryItemToFridge(fridgeId, groceryItem);
        if (updatedFridge != null) {
            return new ResponseEntity<>(updatedFridge, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // DELETE (Remove a grocery item from a fridge)
    @DeleteMapping("/{fridgeId}/grocery-items/{groceryItemId}")
    public ResponseEntity<Fridge> removeGroceryItemFromFridge(@PathVariable Long fridgeId, @PathVariable Long groceryItemId) {
        Optional<GroceryItem> groceryItemOptional = groceryItemService.getGroceryItemById(groceryItemId);
        if (groceryItemOptional.isPresent()) {
            GroceryItem groceryItem = groceryItemOptional.get();
            Fridge updatedFridge = fridgeService.removeGroceryItemFromFridge(fridgeId, groceryItem);
            if (updatedFridge != null) {
                return new ResponseEntity<>(updatedFridge, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
