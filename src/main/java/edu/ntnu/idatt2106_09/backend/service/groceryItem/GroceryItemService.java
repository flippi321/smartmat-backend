package edu.ntnu.idatt2106_09.backend.service.groceryItem;

import edu.ntnu.idatt2106_09.backend.dto.FridgeDto;
import edu.ntnu.idatt2106_09.backend.dto.GroceryItemDto;
import edu.ntnu.idatt2106_09.backend.dto.GroceryItemFridgeDto;
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

    public ResponseEntity<Set<GroceryItemDto>>  getAllGroceryItems();

    public ResponseEntity<FridgeDto> addGroceryItemToFridge(Long fridgeId, GroceryItem groceryItem);

    public ResponseEntity<Set<GroceryItemDto>> getAllGroceryItemsInFridge(@PathVariable Long fridgeId);

    public ResponseEntity<Void> deleteAllGroceryItemsInFridge(@PathVariable Long fridgeId);

    public ResponseEntity<Void> deleteGroceryItemInFridge(@PathVariable Long fridgeId, @PathVariable Long groceryItemId);

    public ResponseEntity<GroceryItemDto> getGroceryItemById(Long groceryItemId);

    public ResponseEntity<GroceryItemDto> updateGroceryItem(Long groceryItemId, GroceryItemDto updatedGroceryItemDto);

    public ResponseEntity<GroceryItemDto> deleteGroceryItem(Long groceryItemId);
}
