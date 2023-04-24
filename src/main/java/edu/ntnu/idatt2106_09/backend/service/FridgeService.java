package edu.ntnu.idatt2106_09.backend.service;

import edu.ntnu.idatt2106_09.backend.model.Fridge;
import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
import edu.ntnu.idatt2106_09.backend.repository.FridgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class FridgeService {

    @Autowired
    private FridgeRepository fridgeRepository;

    // Create (Add a new fridge)
    public Fridge addFridge(Fridge fridge) {
        return fridgeRepository.save(fridge);
    }

    // Read (Get a fridge by ID)
    public Optional<Fridge> getFridgeById(Long fridgeId) {
        return fridgeRepository.findById(fridgeId);
    }

    // Read (Get all fridges)
    public Set<Fridge> getAllFridges() {
        return fridgeRepository.getAllFridges();
    }

    // Update (Update a fridge)
    public Fridge updateFridge(Fridge fridge) {
        return fridgeRepository.save(fridge);
    }

    // Delete (Delete a fridge by ID)
    public void deleteFridge(Long fridgeId) {
        fridgeRepository.deleteById(fridgeId);
    }

    // Add a grocery item to a fridge
    public Fridge addGroceryItemToFridge(Long fridgeId, GroceryItem groceryItem) {
        Optional<Fridge> fridgeOptional = fridgeRepository.findById(fridgeId);
        if (fridgeOptional.isPresent()) {
            Fridge fridge = fridgeOptional.get();
            fridge.addGroceryItem(groceryItem);
            return fridgeRepository.save(fridge);
        }
        return null;
    }

    // Remove a grocery item from a fridge
    public Fridge removeGroceryItemFromFridge(Long fridgeId, GroceryItem groceryItem) {
        Optional<Fridge> fridgeOptional = fridgeRepository.findById(fridgeId);
        if (fridgeOptional.isPresent()) {
            Fridge fridge = fridgeOptional.get();
            fridge.removeGroceryItem(groceryItem);
            return fridgeRepository.save(fridge);
        }
        return null;
    }
}
