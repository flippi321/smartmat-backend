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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GroceryItemServiceImplementation implements GroceryItemService {


    @Autowired
    private ModelMapper modelMapper;

    /*@PostConstruct
    public void init() {
        modelMapper = new ModelMapper();
    }*/

    private GroceryItemDto castGroceryItemToDto(GroceryItem groceryItem) {
        modelMapper = new ModelMapper();
        return modelMapper.map(groceryItem, GroceryItemDto.class);
    }

    //Needs fixing, missing houshold dto
    private FridgeDto castFridgeToDto(Fridge fridge){
        modelMapper = new ModelMapper();
        return modelMapper.map(fridge, FridgeDto.class);
    }

    private ShoppinglistDto castShoppinglistToDto(Shoppinglist shoppinglist){
        modelMapper = new ModelMapper();
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
    public GroceryItemServiceImplementation(GroceryItemRepository groceryItemRepository, FridgeRepository fridgeRepository, ShoppinglistRepository shoppinglistRepository) {
        this.groceryItemRepository = groceryItemRepository;
        this.fridgeRepository = fridgeRepository;
        this.shoppinglistRepository = shoppinglistRepository;
    }

    //BELOW ARE API CALLS FOR GROCERYITEM IN RELATION TO A SHOPPINGLIST AND A FRIDGE
    @Override
    public void transferGroceryItemsToFridge(Long shoppinglistId, Long fridgeId, Set<GroceryItemDto> groceryItems) {
        for (GroceryItemDto groceryItemDto : groceryItems) {
            Long groceryItemId = groceryItemDto.getGroceryItemId();
            LocalDateTime timestamp = groceryItemDto.getTimestamp();
            log.debug("[X] Fetching Grocery Item with id: {}", groceryItemId);
            try {
                GroceryItem groceryItem = groceryItemRepository.findById(groceryItemId)
                        .orElseThrow(() -> new NotFoundException("groceryItem with id " + groceryItemId + " not found"));
                log.info("[X] Grocery Item with id {} found", groceryItemId);

                Optional<Shoppinglist> shoppinglistOptional = shoppinglistRepository.findById(shoppinglistId);
                if (shoppinglistOptional.isPresent()) {
                    Shoppinglist shoppinglist = shoppinglistOptional.get();
                    log.info("[X] Shoppinglist with id {} found", shoppinglistId);
                    if (groceryItem.getCategory().getName().equals("Ikke Matvare")) {
                        shoppinglist.removeGroceryItem(groceryItem, timestamp);
                        shoppinglistRepository.save(shoppinglist);
                        continue;
                    }else{
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
                                    shoppinglist.removeGroceryItem(groceryItem, timestamp);
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
                }
                } else {
                    throw new NotFoundException("shoppingList with id " + shoppinglistId + " not found");
                }
            } catch (NotFoundException ex) {
                log.warn("[X] Exception caught: {}", ex.getMessage());
            }
        }
    }



    //BELOW ARE CRUD METHODS FOR GROCERY ITEM IN RELATION TO A SHOPPINGLIST
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
                    groceryItemDto.setTimestamp(grocery.getTimestamp());
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
                        groceryItemDto.setTimestamp(grocery.getTimestamp());
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

    @Override
    public ResponseEntity<ShoppinglistDto> removeGroceryItemFromShoppinglist(Long shoppinglistId, GroceryItemDto groceryItemDto) {
        Long groceryItemId = groceryItemDto.getGroceryItemId();
        LocalDateTime timestamp = groceryItemDto.getTimestamp();
        log.debug("[X] Fetching Grocery Item with id: {}", groceryItemId);
        try {
            GroceryItem groceryItem = groceryItemRepository.findById(groceryItemId)
                    .orElseThrow(() -> new NotFoundException("groceryItem with id " + groceryItemId + " not found"));
            log.info("[X] Grocery Item with id {} found", groceryItemId);
            Optional<Shoppinglist> shoppinglistOptional = shoppinglistRepository.findById(shoppinglistId);
            if (shoppinglistOptional.isPresent()) {
                Shoppinglist shoppinglist = shoppinglistOptional.get();
                log.info("[X] Shoppinglist with id {} found", shoppinglistId);
                shoppinglist.removeGroceryItem(groceryItem, timestamp);
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

    /*@Override
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

     */

    @Override
    public ResponseEntity<GroceryItemDto> updateGroceryItemInShoppinglist(Long shoppinglistId, GroceryItemDto groceryItemDto) {
        Long groceryItemId = groceryItemDto.getGroceryItemId();
        LocalDateTime timestamp = groceryItemDto.getTimestamp();
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
                shoppinglist.removeGroceryItem(groceryItem, timestamp);
                shoppinglist.addGroceryItem(groceryItem, amount);
                if (actualShelfLife == null || actualShelfLife == 0) {
                    groceryItem.setActualShelfLife(groceryItem.getExpectedShelfLife());
                } else {
                    groceryItem.setActualShelfLife(actualShelfLife);
                }
                shoppinglistRepository.save(shoppinglist);
                GroceryItemDto updatedGroceryItemDto = castGroceryItemToDto(groceryItem);
                updatedGroceryItemDto.setAmount(amount);
                log.info("[X] Grocery Item with id {} updated in Shoppinglist with id {}", groceryItemId, shoppinglistId);
                return new ResponseEntity<>(updatedGroceryItemDto, HttpStatus.OK);
            } else {
                throw new NotFoundException("shoppingList with id " + shoppinglistId + " not found");
            }
        } catch (NotFoundException ex) {
            log.warn("[X] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }






    //BELOW ARE CRUD METHODS FOR GROCERY ITEM IN RELATION TO A FRIDGE
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
                    groceryItemDto.setTimestamp(grocery.getTimestamp());
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
                        groceryItemDto.setTimestamp(grocery.getTimestamp());
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


    @Override
    public ResponseEntity<FridgeDto> removeGroceryItemFromFridge(Long fridgeId, GroceryItemDto groceryItemDto) {
        Long groceryItemId = groceryItemDto.getGroceryItemId();
        LocalDateTime timestamp = groceryItemDto.getTimestamp();
        log.debug("[X] Fetching Grocery Item with id: {}", groceryItemId);
        try {
            GroceryItem groceryItem = groceryItemRepository.findById(groceryItemId)
                    .orElseThrow(() -> new NotFoundException("groceryItem with id " + groceryItemId + " not found"));
            log.info("[X] Grocery Item with id {} found", groceryItemId);
            Optional<Fridge> fridgeOptional = fridgeRepository.findById(fridgeId);
            if (fridgeOptional.isPresent()) {
                Fridge fridge = fridgeOptional.get();
                log.info("[X] Fridge with id {} found", fridgeId);
                fridge.removeGroceryItem(groceryItem, timestamp);
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

    /*
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
     */


    @Override
    public ResponseEntity<GroceryItemDto> updateGroceryItemInFridge(Long fridgeId, GroceryItemDto groceryItemDto) {
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
                boolean negative = false;
                if(actualShelfLife >= groceryItem.getActualShelfLife()){
                    actualShelfLife = actualShelfLife-groceryItem.getActualShelfLife();
                    negative = false;
                }else if(actualShelfLife < groceryItem.getActualShelfLife()) {
                    actualShelfLife = Math.abs(groceryItem.getActualShelfLife() - actualShelfLife);
                    negative = true;
                }
                fridge.updateGroceryItem(groceryItem, amount, actualShelfLife, negative);
                fridgeRepository.save(fridge);
                GroceryItemDto updatedGroceryItemDto = castGroceryItemToDto(groceryItem);
                updatedGroceryItemDto.setAmount(amount);
                log.info("[X] Grocery Item with id {} updated in Fridge with id {}",
                        groceryItemId, fridgeId);
                return new ResponseEntity<>(updatedGroceryItemDto, HttpStatus.OK);
            } else {
                throw new NotFoundException("fridge with id " + fridgeId + " not found");
            }
        } catch (NotFoundException ex) {
            log.warn("[X] Exception caught: {}", ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }







                        //BELOW ARE CRUD METHODS FOR GROCERYITEM ALONE
    @Override
    public ResponseEntity<Set<GroceryItemDto>> getAllGroceryItems() {
        log.debug("[X] Fetching all Grocery Items");
        Set<GroceryItem> allGroceryItem = groceryItemRepository.getAllGroceryItems();
        Set<GroceryItemDto> groceryItemToBeReturned = new HashSet<>();
        for (GroceryItem groceryItem : allGroceryItem) {
            GroceryItemDto groceryItemDto = castGroceryItemToDto(groceryItem);
            groceryItemDto.setAmount(1);
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
