package edu.ntnu.idatt2106_09.backend.service.groceryItem;

import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
import edu.ntnu.idatt2106_09.backend.repository.GroceryItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GroceryItemServiceImplementation implements GroceryItemService {

    @Autowired
    private GroceryItemRepository groceryItemRepository;

    @Override
    public GroceryItem addGroceryItemToFridge(GroceryItem groceryItem) {
        return groceryItemRepository.save(groceryItem);
    }

    @Override
    public Optional<GroceryItem> getGroceryItemById(Long groceryItemId) {
        return groceryItemRepository.findById(groceryItemId);
    }

    @Override
    public GroceryItem updateGroceryItem(GroceryItem groceryItem) {
        return groceryItemRepository.save(groceryItem);
    }
}
