package edu.ntnu.idatt2106_09.backend.service.groceryItem;

import edu.ntnu.idatt2106_09.backend.dto.*;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.NotFoundException;
import edu.ntnu.idatt2106_09.backend.model.*;
import edu.ntnu.idatt2106_09.backend.repository.FridgeRepository;
import edu.ntnu.idatt2106_09.backend.repository.GroceryItemRepository;
import edu.ntnu.idatt2106_09.backend.repository.ShoppinglistRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GroceryItemServiceImplementation implements GroceryItemService {

    @Autowired
    private ModelMapper modelMapper;

    private GroceryItemDto castGroceryItemToDto(GroceryItem groceryItem){
        return modelMapper.map(groceryItem, GroceryItemDto.class);
    }

    private FridgeDto castFridgeToDto(Fridge fridge){
        return modelMapper.map(fridge, FridgeDto.class);
    }
    private ShoppinglistDto castShoppinglistToDto(Shoppinglist shoppinglist){
        return modelMapper.map(shoppinglist, ShoppinglistDto.class);
    }

    @Autowired
    private GroceryItemRepository groceryItemRepository;
    @Autowired
    private FridgeRepository fridgeRepository;
    @Autowired
    private ShoppinglistRepository shoppinglistRepository;

    //BELOW ARE CRUD METHODS FOR GROCERY ITEM IN RELATION TO A SHOPPINGLIST
    @Override
    public ResponseEntity<ShoppinglistDto> addGroceryItemToShoppinglist(Long shoppinglistId, GroceryItem groceryItem, int amount) {
        Optional<Shoppinglist> shoppinglistOptional = shoppinglistRepository.findById(shoppinglistId);
        if (shoppinglistOptional.isPresent()) {
            Shoppinglist shoppinglist = shoppinglistOptional.get();
            shoppinglist.addGroceryItem(groceryItem, amount);
            shoppinglistRepository.save(shoppinglist);
            ShoppinglistDto shoppinglistDto = castShoppinglistToDto(shoppinglist);
            return new ResponseEntity<>(shoppinglistDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Set<GroceryItemShoppinglistDto>> getAllGroceryItemsInShoppinglist(Long shoppinglistId) {

        /*Optional<Shoppinglist> shoppinglistOptional = shoppinglistRepository.findById(shoppinglistId);
        if (shoppinglistOptional.isPresent()) {
            Shoppinglist shoppinglist = shoppinglistOptional.get();
            Set<GroceryItemShoppinglist> groceries = shoppinglist.getGroceries();
            ModelMapper modelMapper = new ModelMapper();
            Set<GroceryItemShoppinglistDto> groceryItemDtos = groceries.stream()
                    .map(grocery -> modelMapper.map(grocery, GroceryItemShoppinglistDto.class))
                    .collect(Collectors.toSet());
            return new ResponseEntity<>(groceryItemDtos, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }*/
        Optional<Shoppinglist> shoppinglistOptional = shoppinglistRepository.findById(shoppinglistId);
        if (shoppinglistOptional.isPresent()) {
            Shoppinglist shoppinglist = shoppinglistOptional.get();
            Set<GroceryItemShoppinglist> groceries = shoppinglist.getGroceries();
            Set<GroceryItemShoppinglistDto> groceryItemDtos = new HashSet<>();
            for (GroceryItemShoppinglist grocery : groceries) {
                GroceryItemShoppinglistDto groceryItemDto = new GroceryItemShoppinglistDto();
                groceryItemDto.setGroceryItemId(grocery.getGroceryItemId());
                groceryItemDto.setName(grocery.getGroceryItem().getName());
                groceryItemDto.setExpected_shelf_life(grocery.getGroceryItem().getShelfLife());
                groceryItemDto.setCategory(grocery.getGroceryItem().getCategory());
                groceryItemDto.setAmount(grocery.getAmount());
                groceryItemDtos.add(groceryItemDto);
            }
            return new ResponseEntity<>(groceryItemDtos, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<GroceryItemShoppinglistDto> getGroceryItemsByIdInShoppinglist(Long shoppinglistId, Long groceryItemId) {
        Optional<Shoppinglist> shoppinglistOptional = shoppinglistRepository.findById(shoppinglistId);
        if (shoppinglistOptional.isPresent()) {
            Shoppinglist shoppinglist = shoppinglistOptional.get();
            Set<GroceryItemShoppinglist> groceries = shoppinglist.getGroceries();
            for (GroceryItemShoppinglist grocery : groceries) {
                if (grocery.getGroceryItemId().equals(groceryItemId)) {
                    GroceryItemShoppinglistDto groceryItemDto = new GroceryItemShoppinglistDto();
                    groceryItemDto.setGroceryItemId(grocery.getGroceryItemId());
                    groceryItemDto.setName(grocery.getGroceryItem().getName());
                    groceryItemDto.setExpected_shelf_life(grocery.getGroceryItem().getShelfLife());
                    groceryItemDto.setCategory(grocery.getGroceryItem().getCategory());
                    groceryItemDto.setAmount(grocery.getAmount());
                    return new ResponseEntity<>(groceryItemDto, HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Void> deleteAllGroceryItemsInShoppinglist(Long shoppinglistId) {
        Optional<Shoppinglist> shoppinglistOptional = shoppinglistRepository.findById(shoppinglistId);
        if (shoppinglistOptional.isPresent()) {
            Shoppinglist shoppinglist = shoppinglistOptional.get();
            Set<GroceryItemShoppinglist> groceries = shoppinglist.getGroceries();
            groceries.clear();
            shoppinglistRepository.save(shoppinglist);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<ShoppinglistDto> removeGroceryItemFromShoppinglist( Long shoppinglistId, GroceryItem groceryItem) {
        Optional<Shoppinglist> shoppinglistOptional = shoppinglistRepository.findById(shoppinglistId);
        if (shoppinglistOptional.isPresent()) {
            Shoppinglist shoppinglist = shoppinglistOptional.get();
            shoppinglist.removeGroceryItem(groceryItem);
            shoppinglistRepository.save(shoppinglist);
            ShoppinglistDto shoppinglistDto = castShoppinglistToDto(shoppinglist);
            return new ResponseEntity<>(shoppinglistDto, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }





    //BELOW ARE CRUD METHODS FOR GROCERY ITEM IN RELATION TO A FRIDGE
    @Override
    public ResponseEntity<FridgeDto> addGroceryItemToFridge(Long fridgeId, GroceryItem groceryItem, int amount) {
        Optional<Fridge> fridgeOptional = fridgeRepository.findById(fridgeId);
        if (fridgeOptional.isPresent()) {
            Fridge fridge = fridgeOptional.get();
            fridge.addGroceryItem(groceryItem, amount);
            fridgeRepository.save(fridge);
            FridgeDto fridgeDto = castFridgeToDto(fridge);
            return new ResponseEntity<>(fridgeDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

   @Override
   public ResponseEntity<Set<GroceryItemFridgeDto>> getAllGroceryItemsInFridge(Long fridgeId) {
       Optional<Fridge> fridgeOptional = fridgeRepository.findById(fridgeId);
       if (fridgeOptional.isPresent()) {
           Fridge fridge = fridgeOptional.get();
           Set<GroceryItemFridge> groceries = fridge.getGroceries();
           Set<GroceryItemFridgeDto> groceryItemDtos = new HashSet<>();
           for (GroceryItemFridge grocery : groceries) {
               GroceryItemFridgeDto groceryItemDto = new GroceryItemFridgeDto();
               groceryItemDto.setGroceryItemId(grocery.getGroceryItemId());
               groceryItemDto.setName(grocery.getGroceryItem().getName());
               groceryItemDto.setExpected_shelf_life(grocery.getGroceryItem().getShelfLife());
               groceryItemDto.setCategory(grocery.getGroceryItem().getCategory());
               groceryItemDto.setAmount(grocery.getAmount());
               groceryItemDto.setDays_since_purchase(ChronoUnit.DAYS.between(LocalDate.now(), grocery.getPurchaseDate()));
               groceryItemDto.setDays_until_spoilt(ChronoUnit.DAYS.between(LocalDate.now(), grocery.getExpirationDate()));
               groceryItemDtos.add(groceryItemDto);
           }
           return new ResponseEntity<>(groceryItemDtos, HttpStatus.OK);
       } else {
           return new ResponseEntity<>(HttpStatus.NOT_FOUND);
       }
   }

    @Override
    public ResponseEntity<GroceryItemFridgeDto> getGroceryItemsByIdInFridge(Long fridgeId, Long groceryItemId) {
        Optional<Fridge> fridgeOptional = fridgeRepository.findById(fridgeId);
        if (fridgeOptional.isPresent()) {
            Fridge fridge = fridgeOptional.get();
            Set<GroceryItemFridge> groceries = fridge.getGroceries();
            for (GroceryItemFridge grocery : groceries) {
                if (grocery.getGroceryItemId().equals(groceryItemId)) {
                    GroceryItemFridgeDto groceryItemDto = new GroceryItemFridgeDto();
                    groceryItemDto.setGroceryItemId(grocery.getGroceryItemId());
                    groceryItemDto.setName(grocery.getGroceryItem().getName());
                    groceryItemDto.setExpected_shelf_life(grocery.getGroceryItem().getShelfLife());
                    groceryItemDto.setCategory(grocery.getGroceryItem().getCategory());
                    groceryItemDto.setAmount(grocery.getAmount());
                    groceryItemDto.setDays_since_purchase(ChronoUnit.DAYS.between(LocalDate.now(), grocery.getPurchaseDate()));
                    groceryItemDto.setDays_until_spoilt(ChronoUnit.DAYS.between(LocalDate.now(), grocery.getExpirationDate()));
                    return new ResponseEntity<>(groceryItemDto, HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Void> deleteAllGroceryItemsInFridge( Long fridgeId) {
        Optional<Fridge> fridgeOptional = fridgeRepository.findById(fridgeId);
        if (fridgeOptional.isPresent()) {
            Fridge fridge = fridgeOptional.get();
            Set<GroceryItemFridge> groceries = fridge.getGroceries();
            groceries.clear();
            fridgeRepository.save(fridge);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<FridgeDto> removeGroceryItemFromFridge( Long fridgeId, GroceryItem groceryItem) {
        Optional<Fridge> fridgeOptional = fridgeRepository.findById(fridgeId);
        if (fridgeOptional.isPresent()) {
            Fridge fridge = fridgeOptional.get();
            fridge.removeGroceryItem(groceryItem);
            fridgeRepository.save(fridge);
            FridgeDto fridgeDto = castFridgeToDto(fridge);
            return new ResponseEntity<>(fridgeDto, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }







    //BELOW ARE CRUD METHODS FOR GROCERYITEM ALONE
    @Override
    public ResponseEntity<Set<GroceryItemDto>> getAllGroceryItems(){
        Set<GroceryItem> allGroceryItem = groceryItemRepository.getAllGroceryItems();
        Set<GroceryItemDto> groceryItemToBeReturned = new HashSet<>();
        for (GroceryItem groceryItem : allGroceryItem) {
            GroceryItemDto groceryItemDto = castGroceryItemToDto(groceryItem);
            groceryItemToBeReturned.add(groceryItemDto);
        }
        if (groceryItemToBeReturned.size() == 0) {
            return new ResponseEntity<>(groceryItemToBeReturned, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(groceryItemToBeReturned, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<GroceryItemDto> getGroceryItemById(Long groceryItemId) {
        log.debug("Fetching Grocery Item with id: {}", groceryItemId);
        GroceryItem groceryItem = groceryItemRepository.findById(groceryItemId)
                .orElseThrow(() -> new NotFoundException("groceryItem with id " + groceryItemId + " not found"));
        log.info("[x] Grocery Item with id {} found", groceryItemId);

        GroceryItemDto groceryItemDto = castGroceryItemToDto(groceryItem);
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
            if (updatedGroceryItemDto.getExpected_shelf_life() != 0) {
                groceryItem.setShelfLife(updatedGroceryItemDto.getExpected_shelf_life());
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
