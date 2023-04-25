package edu.ntnu.idatt2106_09.backend.service.groceryItem;

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
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;

@Slf4j
@Service
public class GroceryItemServiceImplementation implements GroceryItemService {

    @Autowired
    private ModelMapper modelMapper;

    private GroceryItemDto castObject(GroceryItem groceryItem){
        return modelMapper.map(groceryItem, GroceryItemDto.class);
    }

    private GroceryItemFridgeDto castObject2(GroceryItemFridge groceryItemFridge){
        return modelMapper.map(groceryItemFridge, GroceryItemFridgeDto.class);
    }

    private FridgeDto castObject3(Fridge fridge){
        return modelMapper.map(fridge, FridgeDto.class);
    }

    @Autowired
    private GroceryItemRepository groceryItemRepository;
    @Autowired
    private FridgeRepository fridgeRepository;
    @Autowired
    private GroceryItemFridgeRepository groceryItemFridgeRepository;



    @Override
    public ResponseEntity<Set<GroceryItemDto>> getAllGroceryItems(){
        Set<GroceryItem> allGroceryItem = groceryItemRepository.getAllGroceryItems();
        Set<GroceryItemDto> groceryItemToBeReturned = new HashSet<>();
        for (GroceryItem groceryItem : allGroceryItem) {
            GroceryItemDto groceryItemDto = castObject(groceryItem);
            groceryItemToBeReturned.add(groceryItemDto);
        }
        if (groceryItemToBeReturned.size() == 0) {
            return new ResponseEntity<>(groceryItemToBeReturned, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(groceryItemToBeReturned, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<FridgeDto> addGroceryItemToFridge(Long fridgeId, GroceryItem groceryItem) {
        // Find the Fridge entity with the given ID
        Fridge fridge = fridgeRepository.findById(fridgeId).orElseThrow(() -> new NotFoundException("Fridge not found"));

        // Add the GroceryItem to the Fridge
        fridge.addGroceryItem(groceryItem);

        // Save the changes
        fridgeRepository.save(fridge);

        // Map the updated Fridge entity to a FridgeDto
        ModelMapper modelMapper = new ModelMapper();
        FridgeDto fridgeDto = modelMapper.map(fridge, FridgeDto.class);

        // Return the FridgeDto in a ResponseEntity
        return new ResponseEntity<>(fridgeDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Set<GroceryItemDto>> getAllGroceryItemsInFridge(@PathVariable Long fridgeId) {
        Set<GroceryItem> allGroceryItems = fridgeRepository.getAllGroceryItemsInFridge(fridgeId);
        Set<GroceryItemDto> groceryItemsToBeReturned = new HashSet<>();

        if (allGroceryItems != null) {
            for (GroceryItem groceryItem : allGroceryItems) {
                GroceryItemDto groceryItemDto = castObject(groceryItem);
                groceryItemsToBeReturned.add(groceryItemDto);
            }
            return new ResponseEntity<>(groceryItemsToBeReturned, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(groceryItemsToBeReturned, HttpStatus.NO_CONTENT);
        }
    }

    @Override
    public ResponseEntity<Void> deleteAllGroceryItemsInFridge(@PathVariable Long fridgeId) {
        fridgeRepository.deleteAllGroceryItemsInFridge(fridgeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<Void> deleteGroceryItemInFridge(@PathVariable Long fridgeId, @PathVariable Long groceryItemId) {
        fridgeRepository.deleteGroceryItemInFridge(fridgeId, groceryItemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<GroceryItemDto> getGroceryItemById(Long groceryItemId) {
        log.debug("Fetching Grocery Item with id: {}", groceryItemId);
        GroceryItem groceryItem = groceryItemRepository.findById(groceryItemId)
                .orElseThrow(() -> new NotFoundException("groceryItem with id " + groceryItemId + " not found"));
        log.info("[x] Grocery Item with id {} found", groceryItemId);

        GroceryItemDto groceryItemDto = castObject(groceryItem);
        return new ResponseEntity<>(groceryItemDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<GroceryItemDto> updateGroceryItem(Long groceryItemId, GroceryItemDto updatedGroceryItemDto) {
        log.debug("Updating Grocery Item with id: {}", groceryItemId);
        Optional<GroceryItem> groceryItemToUpdate = groceryItemRepository.findById(groceryItemId);

        if (groceryItemToUpdate.isPresent()) {
            GroceryItem groceryItem = groceryItemToUpdate.get();
            if (updatedGroceryItemDto.getName() != null) {
                groceryItem.setName(updatedGroceryItemDto.getName());
            }
            if (updatedGroceryItemDto.getShelfLife() != 0) {
                groceryItem.setShelfLife(updatedGroceryItemDto.getShelfLife());
            }
            if (updatedGroceryItemDto.getCategory() != null) {
                groceryItem.setCategory(updatedGroceryItemDto.getCategory());
            }

            groceryItemRepository.save(groceryItem);
        } else {
            log.warn("[x] Grocery Item with id {} not found for update request", groceryItemId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        log.info("[x] Grocery Item with id {} updated", groceryItemId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<GroceryItemDto> deleteGroceryItem(Long groceryItemId){
        log.debug("Deleting Grocery Item with id: {}", groceryItemId);
        Optional<GroceryItem> groceryItemToDelete = groceryItemRepository.findById(groceryItemId);

        if (groceryItemToDelete.isPresent()) {
            groceryItemRepository.delete(groceryItemToDelete.get());
            groceryItemRepository.deleteById(groceryItemId);
            log.info("[x] Grocery Item with id {} deleted", groceryItemId);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            log.warn("[x] Grocery Item with id {} not found for delete request", groceryItemId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
