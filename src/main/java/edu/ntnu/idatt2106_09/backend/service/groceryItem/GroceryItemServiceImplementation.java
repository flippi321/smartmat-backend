package edu.ntnu.idatt2106_09.backend.service.groceryItem;

import edu.ntnu.idatt2106_09.backend.dto.*;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.NotFoundException;
import edu.ntnu.idatt2106_09.backend.model.*;
import edu.ntnu.idatt2106_09.backend.repository.FridgeRepository;
import edu.ntnu.idatt2106_09.backend.repository.GroceryItemRepository;
import edu.ntnu.idatt2106_09.backend.repository.ShoppinglistRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private GroceryItemRepository groceryItemRepository;
    @Autowired
    private FridgeRepository fridgeRepository;
    @Autowired
    private ShoppinglistRepository shoppinglistRepository;

    /**
     * Made for mocking in 'GroceryItemTest'
     */
    public GroceryItemServiceImplementation(GroceryItemRepository groceryItemRepository, FridgeRepository fridgeRepository, ShoppinglistRepository shoppinglistRepository) {
        this.groceryItemRepository = groceryItemRepository;
        this.fridgeRepository = fridgeRepository;
        this.shoppinglistRepository = shoppinglistRepository;
    }

    /**
     * Converts a GroceryItem object to a GroceryItemDto object.
     *
     * @param groceryItem A GroceryItem object to be converted.
     * @return A GroceryItemDto object representing the converted GroceryItem.
     */
    private GroceryItemDto castGroceryItemToDto(GroceryItem groceryItem) {
        return modelMapper.map(groceryItem, GroceryItemDto.class);
    }

    //Needs fixing, missing houshold dto
    /**
     * Converts a Fridge object to a FridgeDto object.
     *
     * @param fridge A Fridge object to be converted.
     * @return A FridgeDto object representing the converted Fridge.
     */
    private FridgeDto castFridgeToDto(Fridge fridge){
        return modelMapper.map(fridge, FridgeDto.class);
    }

    /**
     * Converts a Shoppinglist object to a ShoppinglistDto object.
     *
     * @param shoppinglist A Shoppinglist object to be converted.
     * @return A ShoppinglistDto object representing the converted Shoppinglist.
     */
    private ShoppinglistDto castShoppinglistToDto(Shoppinglist shoppinglist){
        return modelMapper.map(shoppinglist, ShoppinglistDto.class);
    }



    //BELOW ARE API CALLS FOR GROCERYITEM IN RELATION TO A SHOPPINGLIST AND A FRIDGE
    /**
     * Transfers one or more GroceryItems from a Shoppinglist to a Fridge.
     *
     * @param shoppinglistId A Long representing the ID of the Shoppinglist to transfer the GroceryItems from.
     * @param fridgeId A Long representing the ID of the Fridge to transfer the GroceryItems to.
     * @param groceryItemIds An array of Longs representing the IDs of the GroceryItems to transfer.
     * @throws NotFoundException if the Shoppinglist, Fridge, or GroceryItem with the given ID does not exist in the database.
     * @throws DataAccessException if an error occurs while accessing the database.
     * @throws HibernateException if an error occurs while Hibernate is executing a query or interacting with the database.
     * @throws Exception if an unknown exception occurs.
     */
    @Override
    public void transferGroceryItemsToFridge(Long shoppinglistId, Long fridgeId, Long[] groceryItemIds) {
        for (Long groceryItemId : groceryItemIds) {
            log.debug("[X] Fetching Grocery Item with id: {}", groceryItemId);
            try {
                GroceryItem groceryItem = groceryItemRepository.findById(groceryItemId)
                        .orElseThrow(() -> new NotFoundException("groceryItem with id " + groceryItemId + " not found"));
                log.info("[X] Grocery Item with id {} found", groceryItemId);

                Optional<Shoppinglist> shoppinglistOptional = shoppinglistRepository.findById(shoppinglistId);
                if (shoppinglistOptional.isPresent()) {
                    Shoppinglist shoppinglist = shoppinglistOptional.get();
                    log.info("[X] Shoppinglist with id {} found", shoppinglistId);
                    Set<GroceryItemShoppinglist> groceries = shoppinglist.getGroceries();
                    boolean groceryItemFoundInShoppingList = false;
                    for (GroceryItemShoppinglist grocery : groceries) {
                        if (grocery.getGroceryItemId().equals(groceryItemId)) {
                            groceryItemFoundInShoppingList = true;
                            Optional<Fridge> fridgeOptional = fridgeRepository.findById(fridgeId);
                            if (fridgeOptional.isPresent()) {
                                Fridge fridge = fridgeOptional.get();
                                log.info("[X] Fridge with id {} found", fridgeId);
                                fridge.addGroceryItem(groceryItem, grocery.getAmount());
                                fridgeRepository.save(fridge);
                                shoppinglist.removeGroceryItem(groceryItem);
                                shoppinglistRepository.save(shoppinglist);
                                log.info("[X] Grocery Item with id {} transferred from Shoppinglist with id {} to Fridge with id {}", groceryItemId, shoppinglistId, fridgeId);
                            } else {
                                throw new NotFoundException("fridge with id " + fridgeId + " not found");
                            }
                        }
                    }
                    if (!groceryItemFoundInShoppingList) {
                        log.warn("[X] Grocery Item with id {} not found in ShoppingList with id {}", groceryItemId, shoppinglistId);
                    }
                } else {
                    throw new NotFoundException("shoppingList with id " + shoppinglistId + " not found");
                }
            } catch (NotFoundException ex) {
                log.warn("[X] Exception caught: {}", ex.getMessage());
            } catch (DataAccessException ex) {
                log.error("[X] DataAccessException caught: {}", ex.getMessage());
            } catch (HibernateException ex) {
                log.error("[X] HibernateException caught: {}", ex.getMessage());
            } catch (Exception ex) {
                log.error("[X] Exception caught: {}", ex.getMessage());
            }
        }
    }



    //BELOW ARE CRUD METHODS FOR GROCERY ITEM IN RELATION TO A SHOPPINGLIST
    /**
     * Adds one or more GroceryItems to a Shoppinglist and returns a ResponseEntity containing a Set of the added GroceryItemDtos.
     *
     * @param shoppinglistId A Long representing the ID of the Shoppinglist to add the GroceryItems to.
     * @param groceryItems A Set of GroceryItemDtos representing the GroceryItems to add to the Shoppinglist.
     * @return A ResponseEntity containing a Set of the added GroceryItemDtos and a status code indicating the success of the operation.
     * @throws NotFoundException if the Shoppinglist or GroceryItem with the given ID does not exist in the database.
     */
    @Override
    public ResponseEntity<Set<GroceryItemDto>> addGroceryItemsToShoppinglist(Long shoppinglistId, Set<GroceryItemDto> groceryItems) {
        Set<GroceryItemDto> groceryItemDtos = new HashSet<>();
        for (GroceryItemDto groceryItemDto : groceryItems) {
            Long groceryItemId = groceryItemDto.getGroceryItemId();
            int amount = groceryItemDto.getAmount();
            Integer actualShelfLife = groceryItemDto.getActualShelfLife();
            log.debug("[X] Fetching Grocery Item with id: {}", groceryItemId);
            try {
                GroceryItem groceryItem = groceryItemRepository.findById(groceryItemId)
                        .orElseThrow(() -> new NotFoundException("groceryItem with id " + groceryItemId + " not found"));
                log.info("[X] Grocery Item with id {} found", groceryItemId);
                Optional<Shoppinglist> shoppinglistOptional = shoppinglistRepository.findById(shoppinglistId);
                if (shoppinglistOptional.isPresent()) {
                    Shoppinglist shoppinglist = shoppinglistOptional.get();
                    log.info("[X] Shoppinglist with id {} found", shoppinglistId);
                    shoppinglist.addGroceryItem(groceryItem, amount);
                    if (actualShelfLife == null || actualShelfLife == 0) {
                        groceryItem.setActualShelfLife(groceryItem.getExpectedShelfLife());
                    } else {
                        groceryItem.setActualShelfLife(actualShelfLife);
                    }
                    shoppinglistRepository.save(shoppinglist);
                    GroceryItemDto addedGroceryItemDto = castGroceryItemToDto(groceryItem);
                    addedGroceryItemDto.setAmount(amount);
                    groceryItemDtos.add(addedGroceryItemDto);
                    log.info("[X] Grocery Item with id {} added to Shoppinglist with id {}", groceryItemId, shoppinglistId);
                } else {
                    throw new NotFoundException("shoppingList with id " + shoppinglistId + " not found");
                }
            } catch (NotFoundException ex) {
                log.warn("[X] Exception caught: {}", ex.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>(groceryItemDtos, HttpStatus.OK);
    }

    /**
     * Retrieves all grocery items in a shopping list and returns them as a set of GroceryItemShoppinglistDto objects.
     *
     * @param shoppinglistId the ID of the shopping list to retrieve grocery items from
     * @return ResponseEntity<Set<GroceryItemShoppinglistDto>> containing the set of GroceryItemShoppinglistDto objects and a HTTP status code indicating success or failure of the operation
     * @throws NotFoundException if the shopping list with the given ID is not found in the database
     */
    @Override
    public ResponseEntity<Set<GroceryItemShoppinglistDto>> getAllGroceryItemsInShoppinglist(Long shoppinglistId) {
        log.debug("Fetching Shoppinglist with id: {}", shoppinglistId);
        try {
            Optional<Shoppinglist> shoppinglistOptional = shoppinglistRepository.findById(shoppinglistId);
            if (shoppinglistOptional.isPresent()) {
                Shoppinglist shoppinglist = shoppinglistOptional.get();
                log.info("[X] Shoppinglist with id {} found", shoppinglistId);
                Set<GroceryItemShoppinglist> groceries = shoppinglist.getGroceries();
                Set<GroceryItemShoppinglistDto> groceryItemDtos = new HashSet<>();
                for (GroceryItemShoppinglist grocery : groceries) {
                    GroceryItemShoppinglistDto groceryItemDto = new GroceryItemShoppinglistDto();
                    groceryItemDto.setGroceryItemId(grocery.getGroceryItemId());
                    groceryItemDto.setName(grocery.getGroceryItem().getName());
                    groceryItemDto.setExpectedShelfLife(grocery.getGroceryItem().getExpectedShelfLife());
                    groceryItemDto.setActualShelfLife(grocery.getGroceryItem().getActualShelfLife());
                    groceryItemDto.setImageLink(grocery.getGroceryItem().getImageLink());
                    groceryItemDto.setCategory(grocery.getGroceryItem().getCategory());
                    groceryItemDto.setAmount(grocery.getAmount());
                    groceryItemDtos.add(groceryItemDto);
                }
                return new ResponseEntity<>(groceryItemDtos, HttpStatus.OK);
            } else {
                throw new NotFoundException("shoppingList with id " + shoppinglistId + " not found");
            }
        } catch (NotFoundException ex) {
            log.warn("[X] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * This method retrieves a GroceryItemShoppinglistDto object for a given grocery item id and shopping list id.
     *
     * @param shoppinglistId The id of the shopping list to search in.
     * @param groceryItemId The id of the grocery item to retrieve.
     * @return A ResponseEntity containing a GroceryItemShoppinglistDto object if the grocery item is found in the shopping list,
     *         or a NOT_FOUND HttpStatus if the item or list is not found.
     * @throws NotFoundException if the grocery item with the given id is not found in the shopping list.
     */
    @Override
    public ResponseEntity<GroceryItemShoppinglistDto> getGroceryItemsByIdInShoppinglist(Long shoppinglistId, Long groceryItemId) {
        log.debug("[X] Fetching Shoppinglist with id: {}", shoppinglistId);
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
                        groceryItemDto.setExpectedShelfLife(grocery.getGroceryItem().getExpectedShelfLife());
                        groceryItemDto.setActualShelfLife(grocery.getGroceryItem().getActualShelfLife());
                        groceryItemDto.setImageLink(grocery.getGroceryItem().getImageLink());
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
            log.warn("[X] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     Deletes all grocery items from the shopping list with the provided ID.
     @param shoppinglistId the ID of the shopping list to delete grocery items from
     @return a ResponseEntity with a status code of NO_CONTENT if the operation was successful, or a status code of NOT_FOUND if the shopping list with the provided ID was not found
     @throws NotFoundException if the shopping list with the provided ID was not found
     */
    @Override
    public ResponseEntity<Void> deleteAllGroceryItemsInShoppinglist(Long shoppinglistId) {
        log.debug("[X] Fetching Shoppinglist with id: {}", shoppinglistId);
        try {
            Optional<Shoppinglist> shoppinglistOptional = shoppinglistRepository.findById(shoppinglistId);
            if (shoppinglistOptional.isPresent()) {
                Shoppinglist shoppinglist = shoppinglistOptional.get();
                log.info("[X] Shoppinglist with id {} found", shoppinglistId);
                Set<GroceryItemShoppinglist> groceries = shoppinglist.getGroceries();
                groceries.clear();
                shoppinglistRepository.save(shoppinglist);
                log.info("[X] All Grocery Items in Shoppinglist with id {} deleted", shoppinglistId);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                throw new NotFoundException("shoppingList with id " + shoppinglistId + " not found");
            }
        } catch (NotFoundException ex) {
            log.warn("[X] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Removes a grocery item from a shopping list.
     *
     * @param shoppinglistId the ID of the shopping list to remove the grocery item from
     * @param groceryItemId  the ID of the grocery item to remove from the shopping list
     * @return a ResponseEntity with a ShoppinglistDto representing the updated shopping list or NOT_FOUND if either the shopping list or grocery item is not found
     * @throws NotFoundException if the grocery item with the given ID is not found
     */
    @Override
    public ResponseEntity<ShoppinglistDto> removeGroceryItemFromShoppinglist(Long shoppinglistId, Long groceryItemId) {
        log.debug("[X] Fetching Grocery Item with id: {}", groceryItemId);
        try {
            GroceryItem groceryItem = groceryItemRepository.findById(groceryItemId)
                    .orElseThrow(() -> new NotFoundException("groceryItem with id " + groceryItemId + " not found"));
            log.info("[X] Grocery Item with id {} found", groceryItemId);
            Optional<Shoppinglist> shoppinglistOptional = shoppinglistRepository.findById(shoppinglistId);
            if (shoppinglistOptional.isPresent()) {
                Shoppinglist shoppinglist = shoppinglistOptional.get();
                log.info("[X] Shoppinglist with id {} found", shoppinglistId);
                shoppinglist.removeGroceryItem(groceryItem);
                shoppinglistRepository.save(shoppinglist);
                ShoppinglistDto shoppinglistDto = castShoppinglistToDto(shoppinglist);
                log.info("[X] Grocery Item with id {} removed from Shoppinglist with id {}", groceryItemId, shoppinglistId);
                return new ResponseEntity<>(shoppinglistDto, HttpStatus.OK);
            } else {
                throw new NotFoundException("shoppingList with id " + shoppinglistId + " not found");
            }
        } catch (NotFoundException ex) {
            log.warn("[X] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Removes grocery items from a shopping list.
     *
     * @param shoppinglistId the ID of the shopping list to remove items from
     * @param groceryItemIds the IDs of the grocery items to remove from the shopping list
     * @return a ResponseEntity containing a ShoppinglistDto if successful, or NOT_FOUND if an exception is caught
     * @throws NotFoundException if the shopping list or grocery item(s) cannot be found
     */
    @Override
    public ResponseEntity<ShoppinglistDto> removeGroceryItemsFromShoppinglist(Long shoppinglistId, Long[] groceryItemIds) {
        log.debug("[X] Fetching Grocery Items with ids: {}", Arrays.toString(groceryItemIds));
        try {
            List<GroceryItem> groceryItems = new ArrayList<>();
            for (Long groceryItemId : groceryItemIds) {
                GroceryItem groceryItem = groceryItemRepository.findById(groceryItemId)
                        .orElseThrow(() -> new NotFoundException("groceryItem with id " + groceryItemId + " not found"));
                log.info("[X] Grocery Item with id {} found", groceryItemId);
                groceryItems.add(groceryItem);
            }
            Optional<Shoppinglist> shoppinglistOptional = shoppinglistRepository.findById(shoppinglistId);
            if (shoppinglistOptional.isPresent()) {
                Shoppinglist shoppinglist = shoppinglistOptional.get();
                log.info("[X] Shoppinglist with id {} found", shoppinglistId);
                for (GroceryItem groceryItem : groceryItems) {
                    shoppinglist.removeGroceryItem(groceryItem);
                }
                shoppinglistRepository.save(shoppinglist);
                ShoppinglistDto shoppinglistDto = castShoppinglistToDto(shoppinglist);
                log.info("[X] Grocery Items with ids {} removed from Shoppinglist with id {}", Arrays.toString(groceryItemIds), shoppinglistId);
                return new ResponseEntity<>(shoppinglistDto, HttpStatus.OK);
            } else {
                throw new NotFoundException("shoppingList with id " + shoppinglistId + " not found");
            }
        } catch (NotFoundException ex) {
            log.warn("[X] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }






    //BELOW ARE CRUD METHODS FOR GROCERY ITEM IN RELATION TO A FRIDGE'

    /**
     * Adds a set of grocery items to the fridge with the given fridgeId.
     *
     * @param fridgeId     the ID of the fridge to add the grocery items to
     * @param groceryItems the set of GroceryItemDto objects to be added to the fridge
     * @return a ResponseEntity containing a Set of GroceryItemDto objects that were successfully added to the fridge
     *         with the given fridgeId, along with a HttpStatus.OK status code, or a ResponseEntity with a HttpStatus.NOT_FOUND
     *         status code if either the fridge with the given fridgeId or a grocery item with one of the IDs in the set of groceryItems
     *         was not found
     */
    @Override
    public ResponseEntity<Set<GroceryItemDto>> addGroceryItemsToFridge(Long fridgeId, Set<GroceryItemDto> groceryItems) {
        Set<GroceryItemDto> groceryItemDtos = new HashSet<>();
        for (GroceryItemDto groceryItemDto : groceryItems) {
            Long groceryItemId = groceryItemDto.getGroceryItemId();
            int amount = groceryItemDto.getAmount();
            Integer actualShelfLife = groceryItemDto.getActualShelfLife();
            log.debug("[X] Fetching Grocery Item with id: {}", groceryItemId);
            try {
                GroceryItem groceryItem = groceryItemRepository.findById(groceryItemId)
                        .orElseThrow(() -> new NotFoundException("groceryItem with id " + groceryItemId + " not found"));
                log.info("[X] Grocery Item with id {} found", groceryItemId);
                Optional<Fridge> fridgeOptional = fridgeRepository.findById(fridgeId);
                if (fridgeOptional.isPresent()) {
                    Fridge fridge = fridgeOptional.get();
                    log.info("[X] Fridge with id {} found", fridgeId);
                    if (actualShelfLife == null || actualShelfLife == 0) {
                        groceryItem.setActualShelfLife(groceryItem.getExpectedShelfLife());
                    } else {
                        groceryItem.setActualShelfLife(actualShelfLife);
                    }
                    fridge.addGroceryItem(groceryItem, amount);
                    fridgeRepository.save(fridge);
                    GroceryItemDto addedGroceryItemDto = castGroceryItemToDto(groceryItem);
                    addedGroceryItemDto.setAmount(amount);
                    groceryItemDtos.add(addedGroceryItemDto);
                    log.info("[X] Grocery Item with id {} added to Fridge with id {}", groceryItemId, fridgeId);
                } else {
                    throw new NotFoundException("fridge with id " + fridgeId + " not found");
                }
            } catch (NotFoundException ex) {
                log.warn("[X] Exception caught: {}", ex.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>(groceryItemDtos, HttpStatus.OK);
    }


    /**
     * Retrieve all grocery items in the specified fridge.
     *
     * @param fridgeId ID of the fridge to retrieve grocery items from.
     * @return A ResponseEntity object containing a Set of GroceryItemFridgeDto objects, representing the grocery items in the fridge.
     * @throws NotFoundException If the fridge with the specified ID is not found.
     */
   @Override
    public ResponseEntity<Set<GroceryItemFridgeDto>> getAllGroceryItemsInFridge(Long fridgeId) {
        log.debug("[X] Fetching Fridge with id: {}", fridgeId);
        try {
            Optional<Fridge> fridgeOptional = fridgeRepository.findById(fridgeId);
            if (fridgeOptional.isPresent()) {
                Fridge fridge = fridgeOptional.get();
                log.info("[X] Fridge with id {} found", fridgeId);
                Set<GroceryItemFridge> groceries = fridge.getGroceries();
                Set<GroceryItemFridgeDto> groceryItemDtos = new HashSet<>();
                for (GroceryItemFridge grocery : groceries) {
                    GroceryItemFridgeDto groceryItemDto = new GroceryItemFridgeDto();
                    groceryItemDto.setGroceryItemId(grocery.getGroceryItemId());
                    groceryItemDto.setName(grocery.getGroceryItem().getName());
                    groceryItemDto.setExpectedShelfLife(grocery.getGroceryItem().getExpectedShelfLife());
                    groceryItemDto.setActualShelfLife(grocery.getGroceryItem().getActualShelfLife());
                    groceryItemDto.setImageLink(grocery.getGroceryItem().getImageLink());
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
            log.warn("[X] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     Retrieves a grocery item by its ID from a specific fridge.
     @param fridgeId The ID of the fridge to search in.
     @param groceryItemId The ID of the grocery item to retrieve.
     @return A ResponseEntity containing a GroceryItemFridgeDto object if the grocery item is found in the fridge.
     @throws NotFoundException If the fridge or grocery item is not found.
     */
    @Override
    public ResponseEntity<GroceryItemFridgeDto> getGroceryItemsByIdInFridge(Long fridgeId, Long groceryItemId) {
        log.debug("[X] Fetching Fridge with id: {}", fridgeId);
        try {
            Optional<Fridge> fridgeOptional = fridgeRepository.findById(fridgeId);
            if (fridgeOptional.isPresent()) {
                Fridge fridge = fridgeOptional.get();
                log.info("[X] Fridge with id {} found", fridgeId);
                Set<GroceryItemFridge> groceries = fridge.getGroceries();
                for (GroceryItemFridge grocery : groceries) {
                    if (grocery.getGroceryItemId().equals(groceryItemId)) {
                        log.info("[X] Grocery Item with id {} found in Fridge with id {}", groceryItemId, fridgeId);
                        GroceryItemFridgeDto groceryItemDto = new GroceryItemFridgeDto();
                        groceryItemDto.setGroceryItemId(grocery.getGroceryItemId());
                        groceryItemDto.setName(grocery.getGroceryItem().getName());
                        groceryItemDto.setExpectedShelfLife(grocery.getGroceryItem().getActualShelfLife());
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
            log.warn("[X] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     *
     * Deletes all grocery items in a fridge with a given id.
     * @param fridgeId the id of the fridge whose grocery items are to be deleted
     * @return a ResponseEntity with the HTTP status code NO_CONTENT if the operation was successful,
     * or NOT_FOUND if the fridge with the given id was not found
     * @throws NotFoundException if the fridge with the given id is not found
     */
    @Override
    public ResponseEntity<Void> deleteAllGroceryItemsInFridge(Long fridgeId) {
        log.debug("[X] Fetching Fridge with id: {}", fridgeId);
        try {
            Optional<Fridge> fridgeOptional = fridgeRepository.findById(fridgeId);
            if (fridgeOptional.isPresent()) {
                Fridge fridge = fridgeOptional.get();
                log.info("[X] Fridge with id {} found", fridgeId);
                Set<GroceryItemFridge> groceries = fridge.getGroceries();
                groceries.clear();
                fridgeRepository.save(fridge);
                log.info("[X] All Grocery Items in Fridge with id {} deleted", fridgeId);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                throw new NotFoundException("fridge with id " + fridgeId + " not found");
            }
        } catch (NotFoundException ex) {
            log.warn("[X] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    /**
     * Removes the given grocery item from the fridge with the specified ID.
     * @param fridgeId the ID of the fridge from which the grocery item should be removed
     * @param groceryItemId the ID of the grocery item to be removed
     * @return a ResponseEntity containing the updated FridgeDto object and an HTTP status code
     * @throws NotFoundException if either the fridge or the grocery item cannot be found
     */
    @Override
    public ResponseEntity<FridgeDto> removeGroceryItemFromFridge(Long fridgeId, Long groceryItemId) {
        log.debug("[X] Fetching Grocery Item with id: {}", groceryItemId);
        try {
            GroceryItem groceryItem = groceryItemRepository.findById(groceryItemId)
                    .orElseThrow(() -> new NotFoundException("groceryItem with id " + groceryItemId + " not found"));
            log.info("[X] Grocery Item with id {} found", groceryItemId);
            Optional<Fridge> fridgeOptional = fridgeRepository.findById(fridgeId);
            if (fridgeOptional.isPresent()) {
                Fridge fridge = fridgeOptional.get();
                log.info("[X] Fridge with id {} found", fridgeId);
                fridge.removeGroceryItem(groceryItem);
                fridgeRepository.save(fridge);
                FridgeDto fridgeDto = castFridgeToDto(fridge);
                log.info("[X] Grocery Item with id {} removed from Fridge with id {}", groceryItemId, fridgeId);
                return new ResponseEntity<>(fridgeDto, HttpStatus.OK);
            } else {
                throw new NotFoundException("fridge with id " + fridgeId + " not found");
            }
        } catch (NotFoundException ex) {
            log.warn("[X] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Remove grocery items from a fridge by fridge ID and grocery item IDs.
     *
     * @param fridgeId the ID of the fridge from which grocery items are to be removed.
     * @param groceryItemIds an array of grocery item IDs to be removed from the fridge.
     * @return a ResponseEntity with FridgeDto as the response body and HTTP status OK (200) if the operation was successful.
     * @throws NotFoundException if the fridge or any of the grocery items is not found in the database.
     */
    @Override
    public ResponseEntity<FridgeDto> removeGroceryItemsFromFridge(Long fridgeId, Long[] groceryItemIds) {
        log.debug("[X] Fetching Grocery Items with ids: {}", Arrays.toString(groceryItemIds));
        try {
            List<GroceryItem> groceryItems = new ArrayList<>();
            for (Long groceryItemId : groceryItemIds) {
                GroceryItem groceryItem = groceryItemRepository.findById(groceryItemId)
                        .orElseThrow(() -> new NotFoundException("groceryItem with id " + groceryItemId + " not found"));
                log.info("[X] Grocery Item with id {} found", groceryItemId);
                groceryItems.add(groceryItem);
            }
            Optional<Fridge> fridgeOptional = fridgeRepository.findById(fridgeId);
            if (fridgeOptional.isPresent()) {
                Fridge fridge = fridgeOptional.get();
                log.info("[X] Fridge with id {} found", fridgeId);
                for (GroceryItem groceryItem : groceryItems) {
                    fridge.removeGroceryItem(groceryItem);
                }
                fridgeRepository.save(fridge);
                FridgeDto fridgeDto = castFridgeToDto(fridge);
                log.info("[X] Grocery Items with ids {} removed from Fridge with id {}", Arrays.toString(groceryItemIds), fridgeId);
                return new ResponseEntity<>(fridgeDto, HttpStatus.OK);
            } else {
                throw new NotFoundException("fridge with id " + fridgeId + " not found");
            }
        } catch (NotFoundException ex) {
            log.warn("[X] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }







    //BELOW ARE CRUD METHODS FOR GROCERYITEM ALONE

    /**
     * Remove grocery items from a fridge by fridge ID and grocery item IDs.
     *
     * @param fridgeId the ID of the fridge from which grocery items are to be removed.
     * @param groceryItemIds an array of grocery item IDs to be removed from the fridge.
     *
     * @return a ResponseEntity with FridgeDto as the response body and HTTP status OK (200) if the operation was successful.
     *
     * @throws NotFoundException if the fridge or any of the grocery items is not found in the database.
     */
    @Override
    public ResponseEntity<Set<GroceryItemDto>> getAllGroceryItems() {
        log.debug("[X] Fetching all Grocery Items");
        Set<GroceryItem> allGroceryItem = groceryItemRepository.getAllGroceryItems();
        Set<GroceryItemDto> groceryItemToBeReturned = new HashSet<>();
        for (GroceryItem groceryItem : allGroceryItem) {
            GroceryItemDto groceryItemDto = castGroceryItemToDto(groceryItem);
            groceryItemToBeReturned.add(groceryItemDto);
        }
        if (groceryItemToBeReturned.size() == 0) {
            log.info("[X] No Grocery Items found");
            return new ResponseEntity<>(groceryItemToBeReturned, HttpStatus.NO_CONTENT);
        }
        log.info("[X] {} Grocery Items found", groceryItemToBeReturned.size());
        return new ResponseEntity<>(groceryItemToBeReturned, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<GroceryItemDto> getGroceryItemById(Long groceryItemId) {
        log.debug("[X] Fetching Grocery Item with id: {}", groceryItemId);
        try {
            GroceryItem groceryItem = groceryItemRepository.findById(groceryItemId)
                    .orElseThrow(() -> new NotFoundException("groceryItem with id " + groceryItemId + " not found"));
            log.info("[X] Grocery Item with id {} found", groceryItemId);
            GroceryItemDto groceryItemDto = castGroceryItemToDto(groceryItem);
            return new ResponseEntity<>(groceryItemDto, HttpStatus.OK);
        } catch (NotFoundException ex) {
            log.warn("[X] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Update an existing grocery item in the database with the given ID using the provided updated grocery item DTO.
     *
     * @param groceryItemId       the ID of the grocery item to be updated
     * @param updatedGroceryItemDto the DTO representing the updated grocery item data
     * @return a ResponseEntity object with HTTP status code 200 if the update was successful
     * @throws NotFoundException if the grocery item with the given ID does not exist in the database
     */
    @Override
    public ResponseEntity<GroceryItemDto> updateGroceryItem(Long groceryItemId, GroceryItemDto updatedGroceryItemDto) {
        log.debug("[X] Updating Grocery Item with id: {}", groceryItemId);
        try {
            Optional<GroceryItem> groceryItemToUpdate = groceryItemRepository.findById(groceryItemId);

            if (groceryItemToUpdate.isPresent()) {
                GroceryItem groceryItem = groceryItemToUpdate.get();
                log.info("[X] Grocery Item with id {} found", groceryItemId);
                if (updatedGroceryItemDto.getName() != null) {
                    groceryItem.setName(updatedGroceryItemDto.getName());
                }
                if (updatedGroceryItemDto.getExpectedShelfLife() != 0) {
                    groceryItem.setExpectedShelfLife(updatedGroceryItemDto.getExpectedShelfLife());
                }
                if (updatedGroceryItemDto.getActualShelfLife() != 0) {
                    groceryItem.setActualShelfLife(updatedGroceryItemDto.getActualShelfLife());
                }
                if (updatedGroceryItemDto.getImageLink() != null) {
                    groceryItem.setImageLink(updatedGroceryItemDto.getImageLink());
                }
                if (updatedGroceryItemDto.getCategory() != null) {
                    groceryItem.setCategory(updatedGroceryItemDto.getCategory());
                }
                groceryItemRepository.save(groceryItem);
                log.info("[X] Grocery Item with id {} updated", groceryItemId);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                throw new NotFoundException("groceryItem with id " + groceryItemId + " not found");
            }
        } catch (NotFoundException ex) {
            log.warn("[X] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Deletes a Grocery Item from the database by the provided ID.
     *
     * @param groceryItemId the ID of the Grocery Item to be deleted
     * @return a ResponseEntity with HTTP status code OK if the Grocery Item is deleted successfully, or with HTTP status code NOT_FOUND if the Grocery Item with the provided ID is not found
     * @throws NotFoundException if the Grocery Item with the provided ID is not found
     */
    @Override
    public ResponseEntity<GroceryItemDto> deleteGroceryItem(Long groceryItemId) {
        log.debug("[X] Deleting Grocery Item with id: {}", groceryItemId);
        try {
            Optional<GroceryItem> groceryItemToDelete = groceryItemRepository.findById(groceryItemId);

            if (groceryItemToDelete.isPresent()) {
                groceryItemRepository.delete(groceryItemToDelete.get());
                groceryItemRepository.deleteById(groceryItemId);
                log.info("[X] Grocery Item with id {} deleted", groceryItemId);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                throw new NotFoundException("groceryItem with id " + groceryItemId + " not found");
            }
        } catch (NotFoundException ex) {
            log.warn("[X] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
