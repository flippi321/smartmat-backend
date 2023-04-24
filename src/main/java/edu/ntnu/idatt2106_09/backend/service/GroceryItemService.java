package edu.ntnu.idatt2106_09.backend.service;

import edu.ntnu.idatt2106_09.backend.model.Fridge;
import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
import edu.ntnu.idatt2106_09.backend.repository.FridgeRepository;
import edu.ntnu.idatt2106_09.backend.repository.GroceryItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class GroceryItemService {

    @Autowired
    private GroceryItemRepository groceryItemRepository;

    // Create (Add a new groceryItem)
    public GroceryItem addGroceryItemToFridge(GroceryItem groceryItem) {
        return groceryItemRepository.save(groceryItem);
    }

    // Get a grocery item by ID
    public Optional<GroceryItem> getGroceryItemById(Long groceryItemId) {
        return groceryItemRepository.findById(groceryItemId);
    }


    // Update (Update a groceryItem)
    public GroceryItem updateGroceryItem(GroceryItem groceryItem) {
        return groceryItemRepository.save(groceryItem);
    }


}
