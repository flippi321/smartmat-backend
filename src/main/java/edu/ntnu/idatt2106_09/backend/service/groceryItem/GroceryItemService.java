package edu.ntnu.idatt2106_09.backend.service.groceryItem;

import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface GroceryItemService {

    public GroceryItem addGroceryItemToFridge(GroceryItem groceryItem);

    public Optional<GroceryItem> getGroceryItemById(Long groceryItemId);

    public GroceryItem updateGroceryItem(GroceryItem groceryItem);
}
