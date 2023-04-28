package edu.ntnu.idatt2106_09.backend.service.groceryItem;

import edu.ntnu.idatt2106_09.backend.dto.*;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.NotFoundException;
import edu.ntnu.idatt2106_09.backend.model.*;
import edu.ntnu.idatt2106_09.backend.repository.FridgeRepository;
import edu.ntnu.idatt2106_09.backend.repository.GroceryItemRepository;
import edu.ntnu.idatt2106_09.backend.repository.ShoppinglistRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
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

    /**
     * Made for mocking in 'GroceryItemTest'
     */
    public GroceryItemServiceImplementation(GroceryItemRepository groceryItemRepository) {
        this.groceryItemRepository = groceryItemRepository;
    }

    //BELOW ARE API CALLS FOR GROCERYITEM IN RELATION TO A SHOPPINGLIST AND A FRIDGE
    @Override
    public ResponseEntity<FridgeDto> transferGroceryItemToFridge(Long shoppinglistId, Long fridgeId, Long groceryItemId) {
        log.debug("Fetching Grocery Item with id: {}", groceryItemId);
        try {
            GroceryItem groceryItem = groceryItemRepository.findById(groceryItemId)
                    .orElseThrow(() -> new NotFoundException("groceryItem with id " + groceryItemId + " not found"));
            log.info("[x] Grocery Item with id {} found", groceryItemId);

            Optional<Shoppinglist> shoppinglistOptional = shoppinglistRepository.findById(shoppinglistId);
            if (shoppinglistOptional.isPresent()) {
                Shoppinglist shoppinglist = shoppinglistOptional.get();
                log.info("[x] Shoppinglist with id {} found", shoppinglistId);
                Set<GroceryItemShoppinglist> groceries = shoppinglist.getGroceries();
                for (GroceryItemShoppinglist grocery : groceries) {
                    if (grocery.getGroceryItemId().equals(groceryItemId)) {
                        Optional<Fridge> fridgeOptional = fridgeRepository.findById(fridgeId);
                        if (fridgeOptional.isPresent()) {
                            Fridge fridge = fridgeOptional.get();
                            log.info("[x] Fridge with id {} found", fridgeId);
                            fridge.addGroceryItem(groceryItem, grocery.getAmount());
                            fridgeRepository.save(fridge);
                            FridgeDto fridgeDto = castFridgeToDto(fridge);
                            shoppinglist.removeGroceryItem(groceryItem);
                            shoppinglistRepository.save(shoppinglist);
                            log.info("[x] Grocery Item with id {} transferred from Shoppinglist with id {} to Fridge with id {}", groceryItemId, shoppinglistId, fridgeId);
                            return new ResponseEntity<>(fridgeDto, HttpStatus.OK);
                        } else {
                            throw new NotFoundException("fridge with id " + fridgeId + " not found");
                        }
                    } else {
                        throw new NotFoundException("groceryItem with id " + groceryItemId + " not found in shoppingList with id " + shoppinglistId);
                    }
                }
            } else {
                throw new NotFoundException("shoppingList with id " + shoppinglistId + " not found");
            }
        } catch (NotFoundException ex) {
            log.warn("[x] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }



    //BELOW ARE CRUD METHODS FOR GROCERY ITEM IN RELATION TO A SHOPPINGLIST
    @Override
    public ResponseEntity<ShoppinglistDto> addGroceryItemToShoppinglist(Long shoppinglistId, Long groceryItemId, int amount) {
        log.debug("Fetching Grocery Item with id: {}", groceryItemId);
        try {
            GroceryItem groceryItem = groceryItemRepository.findById(groceryItemId)
                    .orElseThrow(() -> new NotFoundException("groceryItem with id " + groceryItemId + " not found"));
            log.info("[x] Grocery Item with id {} found", groceryItemId);
            Optional<Shoppinglist> shoppinglistOptional = shoppinglistRepository.findById(shoppinglistId);
            if (shoppinglistOptional.isPresent()) {
                Shoppinglist shoppinglist = shoppinglistOptional.get();
                shoppinglist.addGroceryItem(groceryItem, amount);
                shoppinglistRepository.save(shoppinglist);
                ShoppinglistDto shoppinglistDto = castShoppinglistToDto(shoppinglist);
                return new ResponseEntity<>(shoppinglistDto, HttpStatus.OK);
            } else {
                throw new NotFoundException("shoppingList with id " + shoppinglistId + " not found");
            }
        } catch (NotFoundException ex) {
            log.warn("[x] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Set<GroceryItemShoppinglistDto>> getAllGroceryItemsInShoppinglist(Long shoppinglistId) {
        log.debug("Fetching Shoppinglist with id: {}", shoppinglistId);
        try {
            Optional<Shoppinglist> shoppinglistOptional = shoppinglistRepository.findById(shoppinglistId);
            if (shoppinglistOptional.isPresent()) {
                Shoppinglist shoppinglist = shoppinglistOptional.get();
                log.info("[x] Shoppinglist with id {} found", shoppinglistId);
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
                throw new NotFoundException("shoppingList with id " + shoppinglistId + " not found");
            }
        } catch (NotFoundException ex) {
            log.warn("[x] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<GroceryItemShoppinglistDto> getGroceryItemsByIdInShoppinglist(Long shoppinglistId, Long groceryItemId) {
        log.debug("Fetching Shoppinglist with id: {}", shoppinglistId);
        try {
            Optional<Shoppinglist> shoppinglistOptional = shoppinglistRepository.findById(shoppinglistId);
            if (shoppinglistOptional.isPresent()) {
                Shoppinglist shoppinglist = shoppinglistOptional.get();
                log.info("[x] Shoppinglist with id {} found", shoppinglistId);
                Set<GroceryItemShoppinglist> groceries = shoppinglist.getGroceries();
                for (GroceryItemShoppinglist grocery : groceries) {
                    if (grocery.getGroceryItemId().equals(groceryItemId)) {
                        log.info("[x] Grocery Item with id {} found in Shoppinglist with id {}", groceryItemId, shoppinglistId);
                        GroceryItemShoppinglistDto groceryItemDto = new GroceryItemShoppinglistDto();
                        groceryItemDto.setGroceryItemId(grocery.getGroceryItemId());
                        groceryItemDto.setName(grocery.getGroceryItem().getName());
                        groceryItemDto.setExpected_shelf_life(grocery.getGroceryItem().getShelfLife());
                        groceryItemDto.setCategory(grocery.getGroceryItem().getCategory());
                        groceryItemDto.setAmount(grocery.getAmount());
                        return new ResponseEntity<>(groceryItemDto, HttpStatus.OK);
                    }
                }
                throw new NotFoundException("groceryItem with id " + groceryItemId + " not found in shoppingList with id " + shoppinglistId);
            } else {
                throw new NotFoundException("shoppingList with id " + shoppinglistId + " not found");
            }
        } catch (NotFoundException ex) {
            log.warn("[x] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Void> deleteAllGroceryItemsInShoppinglist(Long shoppinglistId) {
        log.debug("Fetching Shoppinglist with id: {}", shoppinglistId);
        try {
            Optional<Shoppinglist> shoppinglistOptional = shoppinglistRepository.findById(shoppinglistId);
            if (shoppinglistOptional.isPresent()) {
                Shoppinglist shoppinglist = shoppinglistOptional.get();
                log.info("[x] Shoppinglist with id {} found", shoppinglistId);
                Set<GroceryItemShoppinglist> groceries = shoppinglist.getGroceries();
                groceries.clear();
                shoppinglistRepository.save(shoppinglist);
                log.info("[x] All Grocery Items in Shoppinglist with id {} deleted", shoppinglistId);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                throw new NotFoundException("shoppingList with id " + shoppinglistId + " not found");
            }
        } catch (NotFoundException ex) {
            log.warn("[x] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<ShoppinglistDto> removeGroceryItemFromShoppinglist(Long shoppinglistId, Long groceryItemId) {
        log.debug("Fetching Grocery Item with id: {}", groceryItemId);
        try {
            GroceryItem groceryItem = groceryItemRepository.findById(groceryItemId)
                    .orElseThrow(() -> new NotFoundException("groceryItem with id " + groceryItemId + " not found"));
            log.info("[x] Grocery Item with id {} found", groceryItemId);
            Optional<Shoppinglist> shoppinglistOptional = shoppinglistRepository.findById(shoppinglistId);
            if (shoppinglistOptional.isPresent()) {
                Shoppinglist shoppinglist = shoppinglistOptional.get();
                log.info("[x] Shoppinglist with id {} found", shoppinglistId);
                shoppinglist.removeGroceryItem(groceryItem);
                shoppinglistRepository.save(shoppinglist);
                ShoppinglistDto shoppinglistDto = castShoppinglistToDto(shoppinglist);
                log.info("[x] Grocery Item with id {} removed from Shoppinglist with id {}", groceryItemId, shoppinglistId);
                return new ResponseEntity<>(shoppinglistDto, HttpStatus.OK);
            } else {
                throw new NotFoundException("shoppingList with id " + shoppinglistId + " not found");
            }
        } catch (NotFoundException ex) {
            log.warn("[x] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }






    //BELOW ARE CRUD METHODS FOR GROCERY ITEM IN RELATION TO A FRIDGE
    @Override
    public ResponseEntity<FridgeDto> addGroceryItemToFridge(Long fridgeId, Long groceryItemId, int amount) {
        log.debug("Fetching Grocery Item with id: {}", groceryItemId);
        try {
            GroceryItem groceryItem = groceryItemRepository.findById(groceryItemId)
                    .orElseThrow(() -> new NotFoundException("groceryItem with id " + groceryItemId + " not found"));
            log.info("[x] Grocery Item with id {} found", groceryItemId);
            Optional<Fridge> fridgeOptional = fridgeRepository.findById(fridgeId);
            if (fridgeOptional.isPresent()) {
                Fridge fridge = fridgeOptional.get();
                log.info("[x] Fridge with id {} found", fridgeId);
                fridge.addGroceryItem(groceryItem, amount);
                fridgeRepository.save(fridge);
                FridgeDto fridgeDto = castFridgeToDto(fridge);
                log.info("[x] Grocery Item with id {} added to Fridge with id {}", groceryItemId, fridgeId);
                return new ResponseEntity<>(fridgeDto, HttpStatus.OK);
            } else {
                throw new NotFoundException("fridge with id " + fridgeId + " not found");
            }
        } catch (NotFoundException ex) {
            log.warn("[x] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

   @Override
    public ResponseEntity<Set<GroceryItemFridgeDto>> getAllGroceryItemsInFridge(Long fridgeId) {
        log.debug("Fetching Fridge with id: {}", fridgeId);
        try {
            Optional<Fridge> fridgeOptional = fridgeRepository.findById(fridgeId);
            if (fridgeOptional.isPresent()) {
                Fridge fridge = fridgeOptional.get();
                log.info("[x] Fridge with id {} found", fridgeId);
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
                throw new NotFoundException("fridge with id " + fridgeId + " not found");
            }
        } catch (NotFoundException ex) {
            log.warn("[x] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<GroceryItemFridgeDto> getGroceryItemsByIdInFridge(Long fridgeId, Long groceryItemId) {
        log.debug("Fetching Fridge with id: {}", fridgeId);
        try {
            Optional<Fridge> fridgeOptional = fridgeRepository.findById(fridgeId);
            if (fridgeOptional.isPresent()) {
                Fridge fridge = fridgeOptional.get();
                log.info("[x] Fridge with id {} found", fridgeId);
                Set<GroceryItemFridge> groceries = fridge.getGroceries();
                for (GroceryItemFridge grocery : groceries) {
                    if (grocery.getGroceryItemId().equals(groceryItemId)) {
                        log.info("[x] Grocery Item with id {} found in Fridge with id {}", groceryItemId, fridgeId);
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
                throw new NotFoundException("groceryItem with id " + groceryItemId + " not found in fridge with id " + fridgeId);
            } else {
                throw new NotFoundException("fridge with id " + fridgeId + " not found");
            }
        } catch (NotFoundException ex) {
            log.warn("[x] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Void> deleteAllGroceryItemsInFridge(Long fridgeId) {
        log.debug("Fetching Fridge with id: {}", fridgeId);
        try {
            Optional<Fridge> fridgeOptional = fridgeRepository.findById(fridgeId);
            if (fridgeOptional.isPresent()) {
                Fridge fridge = fridgeOptional.get();
                log.info("[x] Fridge with id {} found", fridgeId);
                Set<GroceryItemFridge> groceries = fridge.getGroceries();
                groceries.clear();
                fridgeRepository.save(fridge);
                log.info("[x] All Grocery Items in Fridge with id {} deleted", fridgeId);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                throw new NotFoundException("fridge with id " + fridgeId + " not found");
            }
        } catch (NotFoundException ex) {
            log.warn("[x] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @Override
    public ResponseEntity<FridgeDto> removeGroceryItemFromFridge(Long fridgeId, Long groceryItemId) {
        log.debug("Fetching Grocery Item with id: {}", groceryItemId);
        try {
            GroceryItem groceryItem = groceryItemRepository.findById(groceryItemId)
                    .orElseThrow(() -> new NotFoundException("groceryItem with id " + groceryItemId + " not found"));
            log.info("[x] Grocery Item with id {} found", groceryItemId);
            Optional<Fridge> fridgeOptional = fridgeRepository.findById(fridgeId);
            if (fridgeOptional.isPresent()) {
                Fridge fridge = fridgeOptional.get();
                log.info("[x] Fridge with id {} found", fridgeId);
                fridge.removeGroceryItem(groceryItem);
                fridgeRepository.save(fridge);
                FridgeDto fridgeDto = castFridgeToDto(fridge);
                log.info("[x] Grocery Item with id {} removed from Fridge with id {}", groceryItemId, fridgeId);
                return new ResponseEntity<>(fridgeDto, HttpStatus.OK);
            } else {
                throw new NotFoundException("fridge with id " + fridgeId + " not found");
            }
        } catch (NotFoundException ex) {
            log.warn("[x] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }







    //BELOW ARE CRUD METHODS FOR GROCERYITEM ALONE
    @Override
    public ResponseEntity<Set<GroceryItemDto>> getAllGroceryItems() {
        log.debug("Fetching all Grocery Items");
        Set<GroceryItem> allGroceryItem = groceryItemRepository.getAllGroceryItems();
        Set<GroceryItemDto> groceryItemToBeReturned = new HashSet<>();
        for (GroceryItem groceryItem : allGroceryItem) {
            GroceryItemDto groceryItemDto = castGroceryItemToDto(groceryItem);
            groceryItemToBeReturned.add(groceryItemDto);
        }
        if (groceryItemToBeReturned.size() == 0) {
            log.info("[x] No Grocery Items found");
            return new ResponseEntity<>(groceryItemToBeReturned, HttpStatus.NO_CONTENT);
        }
        log.info("[x] {} Grocery Items found", groceryItemToBeReturned.size());
        return new ResponseEntity<>(groceryItemToBeReturned, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<GroceryItemDto> getGroceryItemById(Long groceryItemId) {
        log.debug("Fetching Grocery Item with id: {}", groceryItemId);
        try {
            GroceryItem groceryItem = groceryItemRepository.findById(groceryItemId)
                    .orElseThrow(() -> new NotFoundException("groceryItem with id " + groceryItemId + " not found"));
            log.info("[x] Grocery Item with id {} found", groceryItemId);
            GroceryItemDto groceryItemDto = castGroceryItemToDto(groceryItem);
            return new ResponseEntity<>(groceryItemDto, HttpStatus.OK);
        } catch (NotFoundException ex) {
            log.warn("[x] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<GroceryItemDto> updateGroceryItem(Long groceryItemId, GroceryItemDto updatedGroceryItemDto) {
        log.debug("Updating Grocery Item with id: {}", groceryItemId);
        try {
            Optional<GroceryItem> groceryItemToUpdate = groceryItemRepository.findById(groceryItemId);

            if (groceryItemToUpdate.isPresent()) {
                GroceryItem groceryItem = groceryItemToUpdate.get();
                log.info("[x] Grocery Item with id {} found", groceryItemId);
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
                log.info("[x] Grocery Item with id {} updated", groceryItemId);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                throw new NotFoundException("groceryItem with id " + groceryItemId + " not found");
            }
        } catch (NotFoundException ex) {
            log.warn("[x] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<GroceryItemDto> deleteGroceryItem(Long groceryItemId) {
        log.debug("Deleting Grocery Item with id: {}", groceryItemId);
        try {
            Optional<GroceryItem> groceryItemToDelete = groceryItemRepository.findById(groceryItemId);

            if (groceryItemToDelete.isPresent()) {
                groceryItemRepository.delete(groceryItemToDelete.get());
                groceryItemRepository.deleteById(groceryItemId);
                log.info("[x] Grocery Item with id {} deleted", groceryItemId);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                throw new NotFoundException("groceryItem with id " + groceryItemId + " not found");
            }
        } catch (NotFoundException ex) {
            log.warn("[x] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}