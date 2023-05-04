package edu.ntnu.idatt2106_09.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.*;

import edu.ntnu.idatt2106_09.backend.dto.*;
import edu.ntnu.idatt2106_09.backend.model.*;
import edu.ntnu.idatt2106_09.backend.repository.FridgeRepository;
import edu.ntnu.idatt2106_09.backend.repository.GroceryItemRepository;
import edu.ntnu.idatt2106_09.backend.repository.ShoppinglistRepository;
import edu.ntnu.idatt2106_09.backend.service.groceryItem.GroceryItemServiceImplementation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class GroceryItemServiceTest {


    @Mock
    private GroceryItemRepository groceryItemRepository;

    @Mock
    private FridgeRepository fridgeRepository;

    @Mock
    private ShoppinglistRepository shoppinglistRepository;


    @InjectMocks
    private GroceryItemServiceImplementation groceryItemService;

    //TESTS FOR GROCERY ITEMS IN RELATION TO SHOPPINGLIST AND FRIDGE
    /*
    @Test
    public void GroceryItemService_TransferGroceryItemsToFridge() {
        Long shoppinglistId = 1L;
        Long fridgeId = 1L;
        Long[] groceryItemIds = {1L, 2L};

        Shoppinglist shoppinglist = new Shoppinglist();
        shoppinglist.setShoppinglistId(shoppinglistId);

        Fridge fridge = new Fridge();
        fridge.setFridgeId(fridgeId);

        for (Long groceryItemId : groceryItemIds) {
            GroceryItem groceryItem = new GroceryItem();
            groceryItem.setGroceryItemId(groceryItemId);
            Category category = new Category();
            category.setName("Matvare");
            groceryItem.setCategory(category);
            when(groceryItemRepository.findById(groceryItemId)).thenReturn(Optional.of(groceryItem));
            shoppinglist.addGroceryItem(groceryItem, 1);
        }
        when(shoppinglistRepository.findById(shoppinglistId)).thenReturn(Optional.of(shoppinglist));
        when(fridgeRepository.findById(fridgeId)).thenReturn(Optional.of(fridge));

        assertThat(shoppinglist.getGroceries().size()).isEqualTo(2);

        groceryItemService.transferGroceryItemsToFridge(shoppinglistId, fridgeId, groceryItemIds);

        assertThat(shoppinglist.getGroceries().size()).isEqualTo(0);
        assertThat(fridge.getGroceries().size()).isEqualTo(2);
    }
     */



    //TESTS FOR GROCERY ITEMS IN RELATION TO SHOPPINGLIST
    @Test
    public void GroceryItemService_AddGroceryItemsToShoppinglist_ReturnGroceryItemDtos() {
        Long shoppinglistId = 1L;
        Long groceryItemId = 1L;
        int amount = 1;
        int actualShelfLife = 3;

        Shoppinglist shoppinglist = new Shoppinglist();
        shoppinglist.setShoppinglistId(shoppinglistId);

        GroceryItem groceryItem = new GroceryItem();
        groceryItem.setGroceryItemId(groceryItemId);

        Set<GroceryItemDto> groceryItems = new HashSet<>();
        GroceryItemDto groceryItemDto = new GroceryItemDto();
        groceryItemDto.setGroceryItemId(groceryItemId);
        groceryItemDto.setAmount(amount);
        groceryItemDto.setActualShelfLife(actualShelfLife);
        groceryItems.add(groceryItemDto);
        when(groceryItemRepository.findById(groceryItemId)).thenReturn(Optional.of(groceryItem));
        when(shoppinglistRepository.findById(shoppinglistId)).thenReturn(Optional.of(shoppinglist));

        ResponseEntity<Set<GroceryItemDto>> response = groceryItemService.addGroceryItemsToShoppinglist(shoppinglistId, groceryItems);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isEqualTo(1);
    }

    @Test
    public void GroceryItemService_AddGroceryItemsToShoppinglist_ActualShelfLifeSetToExpectedShelfLife() {
        Long shoppinglistId = 1L;
        Long groceryItemId = 1L;
        int amount = 1;
        int expectedShelfLife = 3;

        Shoppinglist shoppinglist = new Shoppinglist();
        shoppinglist.setShoppinglistId(shoppinglistId);

        GroceryItem groceryItem = new GroceryItem();
        groceryItem.setGroceryItemId(groceryItemId);
        groceryItem.setExpectedShelfLife(expectedShelfLife);

        Set<GroceryItemDto> groceryItems = new HashSet<>();
        GroceryItemDto groceryItemDto = new GroceryItemDto();
        groceryItemDto.setGroceryItemId(groceryItemId);
        groceryItemDto.setAmount(amount);
        groceryItems.add(groceryItemDto);

        when(groceryItemRepository.findById(groceryItemId)).thenReturn(Optional.of(groceryItem));
        when(shoppinglistRepository.findById(shoppinglistId)).thenReturn(Optional.of(shoppinglist));

        ResponseEntity<Set<GroceryItemDto>> response = groceryItemService.addGroceryItemsToShoppinglist(shoppinglistId, groceryItems);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().iterator().next().getActualShelfLife()).isEqualTo(expectedShelfLife);
    }

    @Test
    public void GroceryItemService_AddGroceryItemsToShoppinglist_ShoppinglistNotFound() {
        Long shoppinglistId = 1L;
        Long groceryItemId = 1L;
        int amount = 1;
        int actualShelfLife = 3;

        GroceryItem groceryItem = new GroceryItem();
        groceryItem.setGroceryItemId(groceryItemId);

        Set<GroceryItemDto> groceryItems = new HashSet<>();
        GroceryItemDto groceryItemDto = new GroceryItemDto();
        groceryItemDto.setGroceryItemId(groceryItemId);
        groceryItemDto.setAmount(amount);
        groceryItemDto.setActualShelfLife(actualShelfLife);
        groceryItems.add(groceryItemDto);

        when(groceryItemRepository.findById(groceryItemId)).thenReturn(Optional.of(groceryItem));
        when(shoppinglistRepository.findById(shoppinglistId)).thenReturn(Optional.empty());

        ResponseEntity<Set<GroceryItemDto>> response = groceryItemService.addGroceryItemsToShoppinglist(shoppinglistId, groceryItems);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

        @Test
    public void GroceryItemService_GetAllGroceryItemsInShoppinglist_ReturnGroceryItemShoppinglistDtoSet() {
        Long shoppinglistId = 1L;
        Shoppinglist shoppinglist = new Shoppinglist();
        shoppinglist.setShoppinglistId(shoppinglistId);
        GroceryItem groceryItem1 = new GroceryItem();
        groceryItem1.setGroceryItemId(1L);
        GroceryItemShoppinglist groceryItemShoppinglist1 = new GroceryItemShoppinglist(shoppinglist, groceryItem1, 1);
        GroceryItem groceryItem2 = new GroceryItem();
        groceryItem2.setGroceryItemId(2L);
        GroceryItemShoppinglist groceryItemShoppinglist2 = new GroceryItemShoppinglist(shoppinglist, groceryItem2, 2);
        Set<GroceryItemShoppinglist> groceries = new HashSet<>();
        groceries.add(groceryItemShoppinglist1);
        groceries.add(groceryItemShoppinglist2);
        shoppinglist.setGroceries(groceries);

        when(shoppinglistRepository.findById(shoppinglistId)).thenReturn(Optional.of(shoppinglist));

        ResponseEntity<Set<GroceryItemShoppinglistDto>> response = groceryItemService.getAllGroceryItemsInShoppinglist(shoppinglistId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isEqualTo(2);
    }

    @Test
    public void GroceryItemService_GetGroceryItemsByIdInShoppinglist_ReturnGroceryItemShoppinglistDto() {
        Long shoppinglistId = 1L;
        Long groceryItemId = 1L;

        Shoppinglist shoppinglist = new Shoppinglist();
        shoppinglist.setShoppinglistId(shoppinglistId);

        GroceryItem groceryItem = new GroceryItem();
        groceryItem.setGroceryItemId(groceryItemId);
        groceryItem.setName("Test");
        groceryItem.setExpectedShelfLife(7);
        groceryItem.setActualShelfLife(7);
        groceryItem.setImageLink("test");

        shoppinglist.addGroceryItem(groceryItem, 1);

        when(shoppinglistRepository.findById(shoppinglistId)).thenReturn(Optional.of(shoppinglist));

        ResponseEntity<GroceryItemShoppinglistDto> response = groceryItemService.getGroceryItemsByIdInShoppinglist(shoppinglistId, groceryItemId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getGroceryItemId()).isEqualTo(groceryItemId);
        assertThat(response.getBody().getName()).isEqualTo("Test");
        assertThat(response.getBody().getExpectedShelfLife()).isEqualTo(7);
        assertThat(response.getBody().getActualShelfLife()).isEqualTo(7);
        assertThat(response.getBody().getImageLink()).isEqualTo("test");
        assertThat(response.getBody().getAmount()).isEqualTo(1);
    }

    @Test
    public void GroceryItemService_DeleteAllGroceryItemsInShoppinglist() {
        Long shoppinglistId = 1L;

        Shoppinglist shoppinglist = new Shoppinglist();
        shoppinglist.setShoppinglistId(shoppinglistId);

        for (int i = 1; i <= 2; i++) {
            GroceryItem groceryItem = new GroceryItem();
            groceryItem.setGroceryItemId((long) i);
            shoppinglist.addGroceryItem(groceryItem, 1);
        }

        when(shoppinglistRepository.findById(shoppinglistId)).thenReturn(Optional.of(shoppinglist));

        ResponseEntity<Void> response = groceryItemService.deleteAllGroceryItemsInShoppinglist(shoppinglistId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(shoppinglist.getGroceries()).isEmpty();
    }


    @Test
    public void GroceryItemService_RemoveGroceryItemFromShoppinglist_ReturnGroceryItemDto() {
        Long shoppinglistId = 1L;
        Long groceryItemId1 = 1L;
        Long groceryItemId2 = 2L;
        Shoppinglist shoppinglist = new Shoppinglist();
        shoppinglist.setShoppinglistId(shoppinglistId);
        GroceryItem groceryItem1 = new GroceryItem();
        groceryItem1.setGroceryItemId(groceryItemId1);
        LocalDateTime timestamp = LocalDateTime.now();
        GroceryItemShoppinglist groceryItemShoppinglist1 = new GroceryItemShoppinglist(shoppinglist, groceryItem1, 1);
        GroceryItem groceryItem2 = new GroceryItem();
        groceryItem2.setGroceryItemId(groceryItemId2);
        GroceryItemShoppinglist groceryItemShoppinglist2 = new GroceryItemShoppinglist(shoppinglist, groceryItem2, 1);
        Set<GroceryItemShoppinglist> groceries = new HashSet<>();
        groceries.add(groceryItemShoppinglist1);
        groceries.add(groceryItemShoppinglist2);
        shoppinglist.setGroceries(groceries);

        when(shoppinglistRepository.findById(shoppinglistId)).thenReturn(Optional.of(shoppinglist));
        when(groceryItemRepository.findById(groceryItemId1)).thenReturn(Optional.of(groceryItem1));

        GroceryItemDto groceryItemDto = new GroceryItemDto();
        groceryItemDto.setGroceryItemId(groceryItemId1);
        groceryItemDto.setTimestamp(timestamp);

        ResponseEntity<ShoppinglistDto> response = groceryItemService.removeGroceryItemFromShoppinglist(shoppinglistId, groceryItemDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


    @Test
    public void GroceryItemService_UpdateGroceryItemInShoppinglist_ReturnUpdatedGroceryItemDto() {
        Long shoppinglistId = 1L;
        Long oldGroceryItemId = 1L;
        Long newGroceryItemId = 2L;
        int amount = 1;
        int actualShelfLife = 3;

        Shoppinglist shoppinglist = new Shoppinglist();
        shoppinglist.setShoppinglistId(shoppinglistId);

        GroceryItem oldGroceryItem = new GroceryItem();
        oldGroceryItem.setGroceryItemId(oldGroceryItemId);

        GroceryItem newGroceryItem = new GroceryItem();
        newGroceryItem.setGroceryItemId(newGroceryItemId);

        when(groceryItemRepository.findById(oldGroceryItemId)).thenReturn(Optional.of(oldGroceryItem));
        when(groceryItemRepository.findById(newGroceryItemId)).thenReturn(Optional.of(newGroceryItem));
        when(shoppinglistRepository.findById(shoppinglistId)).thenReturn(Optional.of(shoppinglist));

        GroceryItemDto oldGroceryItemDto = new GroceryItemDto();
        oldGroceryItemDto.setGroceryItemId(oldGroceryItem.getGroceryItemId());
        oldGroceryItemDto.setName(oldGroceryItem.getName());
        oldGroceryItemDto.setActualShelfLife(oldGroceryItem.getActualShelfLife());
        oldGroceryItemDto.setExpectedShelfLife(oldGroceryItem.getExpectedShelfLife());
        oldGroceryItemDto.setCategory(oldGroceryItem.getCategory());

        GroceryItemDto newGroceryItemDto = new GroceryItemDto();
        newGroceryItemDto.setGroceryItemId(newGroceryItem.getGroceryItemId());
        newGroceryItemDto.setName(newGroceryItem.getName());
        newGroceryItemDto.setActualShelfLife(actualShelfLife);
        newGroceryItemDto.setExpectedShelfLife(newGroceryItem.getExpectedShelfLife());
        newGroceryItemDto.setCategory(newGroceryItem.getCategory());
        newGroceryItemDto.setAmount(amount);

        groceryItemService.addGroceryItemsToShoppinglist(shoppinglistId, Collections.singleton(oldGroceryItemDto));

        ResponseEntity<GroceryItemDto> response = groceryItemService.updateGroceryItemInShoppinglist(shoppinglistId, newGroceryItemDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getGroceryItemId()).isEqualTo(newGroceryItemId);
    }






    //TESTS FOR GROCERY ITEMS IN RELATION TO FRIDGE

    @Test
    public void GroceryItemService_AddGroceryItemsToFridge_ReturnGroceryItemDtos() {
        Long fridgeId = 1L;
        Long groceryItemId = 1L;
        int amount = 1;
        int actualShelfLife = 3;

        Fridge fridge = new Fridge();
        fridge.setFridgeId(fridgeId);

        GroceryItem groceryItem = new GroceryItem();
        groceryItem.setGroceryItemId(groceryItemId);

        Set<GroceryItemDto> groceryItems = new HashSet<>();
        GroceryItemDto groceryItemDto = new GroceryItemDto();
        groceryItemDto.setGroceryItemId(groceryItemId);
        groceryItemDto.setAmount(amount);
        groceryItemDto.setActualShelfLife(actualShelfLife);
        groceryItems.add(groceryItemDto);

        when(groceryItemRepository.findById(groceryItemId)).thenReturn(Optional.of(groceryItem));
        when(fridgeRepository.findById(fridgeId)).thenReturn(Optional.of(fridge));

        ResponseEntity<Set<GroceryItemDto>> response = groceryItemService.addGroceryItemsToFridge(fridgeId, groceryItems);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isEqualTo(1);
    }

    @Test
    public void GroceryItemService_AddGroceryItemsToFridge_FridgeNotFound() {
        Long fridgeId = 1L;
        Long groceryItemId = 1L;
        int amount = 1;
        int actualShelfLife = 3;

        GroceryItem groceryItem = new GroceryItem();
        groceryItem.setGroceryItemId(groceryItemId);

        Set<GroceryItemDto> groceryItems = new HashSet<>();
        GroceryItemDto groceryItemDto = new GroceryItemDto();
        groceryItemDto.setGroceryItemId(groceryItemId);
        groceryItemDto.setAmount(amount);
        groceryItemDto.setActualShelfLife(actualShelfLife);
        groceryItems.add(groceryItemDto);


        when(groceryItemRepository.findById(groceryItemId)).thenReturn(Optional.of(groceryItem));
        when(fridgeRepository.findById(fridgeId)).thenReturn(Optional.empty());

        ResponseEntity<Set<GroceryItemDto>> response = groceryItemService.addGroceryItemsToFridge(fridgeId, groceryItems);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void GroceryItemService_AddGroceryItemsToFridge_ActualShelfLifeSetToExpectedShelfLife() {
        Long fridgeId = 1L;
        Long groceryItemId = 1L;
        int amount = 1;
        int expectedShelfLife = 3;

        Fridge fridge = new Fridge();
        fridge.setFridgeId(fridgeId);

        GroceryItem groceryItem = new GroceryItem();
        groceryItem.setGroceryItemId(groceryItemId);
        groceryItem.setExpectedShelfLife(expectedShelfLife);

        Set<GroceryItemDto> groceryItems = new HashSet<>();
        GroceryItemDto groceryItemDto = new GroceryItemDto();
        groceryItemDto.setGroceryItemId(groceryItemId);
        groceryItemDto.setAmount(amount);
        groceryItems.add(groceryItemDto);

        when(groceryItemRepository.findById(groceryItemId)).thenReturn(Optional.of(groceryItem));
        when(fridgeRepository.findById(fridgeId)).thenReturn(Optional.of(fridge));

        ResponseEntity<Set<GroceryItemDto>> response = groceryItemService.addGroceryItemsToFridge(fridgeId, groceryItems);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().iterator().next().getActualShelfLife()).isEqualTo(expectedShelfLife);
    }


        @Test
    public void GroceryItemService_GetAllGroceryItemsInFridge_ReturnGroceryItemFridgeDtoSet() {
        Long fridgeId = 1L;
        Fridge fridge = new Fridge();
        fridge.setFridgeId(fridgeId);
        GroceryItem groceryItem1 = new GroceryItem();
        groceryItem1.setGroceryItemId(1L);
        GroceryItemFridge groceryItemFridge1 = new GroceryItemFridge(fridge, groceryItem1, 1);
        GroceryItem groceryItem2 = new GroceryItem();
        groceryItem2.setGroceryItemId(2L);
        GroceryItemFridge groceryItemFridge2 = new GroceryItemFridge(fridge, groceryItem2, 2);
        Set<GroceryItemFridge> groceries = new HashSet<>();
        groceries.add(groceryItemFridge1);
        groceries.add(groceryItemFridge2);
        fridge.setGroceries(groceries);

        when(fridgeRepository.findById(fridgeId)).thenReturn(Optional.of(fridge));

        ResponseEntity<Set<GroceryItemFridgeDto>> response = groceryItemService.getAllGroceryItemsInFridge(fridgeId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isEqualTo(2);
    }



    @Test
    public void GroceryItemService_GetGroceryItemsByIdInFridge_ReturnGroceryItemFridgeDto() {
        Long fridgeId = 1L;
        Long groceryItemId = 1L;
        Fridge fridge = new Fridge();
        fridge.setFridgeId(fridgeId);
        GroceryItem groceryItem = new GroceryItem();
        groceryItem.setGroceryItemId(groceryItemId);
        GroceryItemFridge groceryItemFridge = new GroceryItemFridge(fridge, groceryItem, 1);
        Set<GroceryItemFridge> groceries = new HashSet<>();
        groceries.add(groceryItemFridge);
        fridge.setGroceries(groceries);

        when(fridgeRepository.findById(fridgeId)).thenReturn(Optional.of(fridge));

        ResponseEntity<GroceryItemFridgeDto> response = groceryItemService.getGroceryItemsByIdInFridge(fridgeId, groceryItemId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getGroceryItemId()).isEqualTo(groceryItemId);

    }


    @Test
    public void GroceryItemService_DeleteAllGroceryItemsInFridge_ReturnGroceryItemDtoSet() {
        Long fridgeId = 1L;
        Fridge fridge = new Fridge();
        fridge.setFridgeId(fridgeId);
        GroceryItem groceryItem1 = new GroceryItem();
        groceryItem1.setGroceryItemId(1L);
        GroceryItemFridge groceryItemFridge1 = new GroceryItemFridge(fridge, groceryItem1, 1);
        GroceryItem groceryItem2 = new GroceryItem();
        groceryItem2.setGroceryItemId(2L);
        GroceryItemFridge groceryItemFridge2 = new GroceryItemFridge(fridge, groceryItem2, 2);
        Set<GroceryItemFridge> groceries = new HashSet<>();
        groceries.add(groceryItemFridge1);
        groceries.add(groceryItemFridge2);
        fridge.setGroceries(groceries);

        when(fridgeRepository.findById(fridgeId)).thenReturn(Optional.of(fridge));

        ResponseEntity<Void> response = groceryItemService.deleteAllGroceryItemsInFridge(fridgeId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(fridge.getGroceries()).isEmpty();
    }

    @Test
    public void GroceryItemService_RemoveGroceryItemFromFridge_ReturnGroceryItemDto() {
        Long fridgeId = 1L;
        Long groceryItemId1 = 1L;
        Long groceryItemId2 = 2L;
        Fridge fridge = new Fridge();
        fridge.setFridgeId(fridgeId);
        GroceryItem groceryItem1 = new GroceryItem();
        groceryItem1.setGroceryItemId(groceryItemId1);
        LocalDateTime timestamp = LocalDateTime.now();
        GroceryItemFridge groceryItemFridge1 = new GroceryItemFridge(fridge, groceryItem1, 1);
        GroceryItem groceryItem2 = new GroceryItem();
        groceryItem2.setGroceryItemId(groceryItemId2);
        GroceryItemFridge groceryItemFridge2 = new GroceryItemFridge(fridge, groceryItem2, 1);
        Set<GroceryItemFridge> groceries = new HashSet<>();
        groceries.add(groceryItemFridge1);
        groceries.add(groceryItemFridge2);
        fridge.setGroceries(groceries);

        when(fridgeRepository.findById(fridgeId)).thenReturn(Optional.of(fridge));
        when(groceryItemRepository.findById(groceryItemId1)).thenReturn(Optional.of(groceryItem1));

        GroceryItemDto groceryItemDto = new GroceryItemDto();
        groceryItemDto.setGroceryItemId(groceryItemId1);
        groceryItemDto.setTimestamp(timestamp);

        ResponseEntity<FridgeDto> response = groceryItemService.removeGroceryItemFromFridge(fridgeId, groceryItemDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


    @Test
    public void GroceryItemService_UpdateGroceryItemInFridge_ReturnGroceryItemDto() {
        Long fridgeId = 1L;
        Long groceryItemId = 1L;
        int amount = 2;
        Integer actualShelfLife = 3;

        GroceryItemDto groceryItemDto = new GroceryItemDto();
        groceryItemDto.setGroceryItemId(groceryItemId);
        groceryItemDto.setAmount(amount);
        groceryItemDto.setActualShelfLife(actualShelfLife);

        Fridge fridge = new Fridge();
        fridge.setFridgeId(fridgeId);

        GroceryItem groceryItem = new GroceryItem();
        groceryItem.setGroceryItemId(groceryItemId);
        fridge.addGroceryItem(groceryItem, 1);

        when(groceryItemRepository.findById(groceryItemId)).thenReturn(Optional.of(groceryItem));
        when(fridgeRepository.findById(fridgeId)).thenReturn(Optional.of(fridge));

        ResponseEntity<GroceryItemDto> response = groceryItemService.updateGroceryItemInFridge(fridgeId, groceryItemDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getGroceryItemId()).isEqualTo(groceryItemId);
        assertThat(response.getBody().getAmount()).isEqualTo(amount);
    }



    //TESTS FOR GROCERY ITEMS
    @Test
    public void GroceryItemService_GetAllGroceryItems_ReturnGroceryItemDtoSet() {
        Set<GroceryItem> groceryItems = new HashSet<>();
        groceryItems.add(GroceryItem.builder().groceryItemId(1L).name("banana").expectedShelfLife(7).actualShelfLife(8).imageLink("test").build());
        groceryItems.add(GroceryItem.builder().groceryItemId(2L).name("apple").expectedShelfLife(14).actualShelfLife(15).imageLink("test").build());
        when(groceryItemRepository.getAllGroceryItems()).thenReturn(groceryItems);

        ResponseEntity<Set<GroceryItemDto>> groceryItemsReturn = groceryItemService.getAllGroceryItems();

        assertThat(groceryItemsReturn).isNotNull();
        assertThat(groceryItemsReturn.getBody().size()).isEqualTo(2);
    }


    @Test
    public void GroceryItemService_FindById_ReturnGroceryItemDto() {
        long droceryItemId = 1L;
        GroceryItem groceryItem = GroceryItem.builder().groceryItemId(1L).name("banana").expectedShelfLife(7).actualShelfLife(8).imageLink("test").build();
        when(groceryItemRepository.findById(droceryItemId)).thenReturn(Optional.ofNullable(groceryItem));

        ResponseEntity<GroceryItemDto> groceryItemReturn = groceryItemService.getGroceryItemById(droceryItemId);

        assertThat(groceryItemReturn).isNotNull();
    }

    @Test
    public void GroceryItemService_UpdateGroceryItem_ReturnGroceryItemDto() {
        Long groceryItemId = 1L;
        GroceryItemDto updatedGroceryItemDto = new GroceryItemDto();
        updatedGroceryItemDto.setName("apple");
        updatedGroceryItemDto.setExpectedShelfLife(10);
        updatedGroceryItemDto.setActualShelfLife(12);
        updatedGroceryItemDto.setImageLink("test");

        GroceryItem groceryItem = new GroceryItem();
        groceryItem.setGroceryItemId(groceryItemId);
        groceryItem.setName("banana");
        groceryItem.setExpectedShelfLife(7);
        groceryItem.setActualShelfLife(7);
        groceryItem.setImageLink("test2");

        when(groceryItemRepository.findById(groceryItemId)).thenReturn(Optional.of(groceryItem));
        ResponseEntity<GroceryItemDto> response = groceryItemService.updateGroceryItem(groceryItemId, updatedGroceryItemDto);

        verify(groceryItemRepository).save(groceryItem);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("apple", groceryItem.getName());
        assertEquals(10, groceryItem.getExpectedShelfLife());
        assertEquals(12, groceryItem.getActualShelfLife());
        assertEquals("test", groceryItem.getImageLink());
    }


    //USIKKER OM DENNE FAKTISK VIRKER
    @Test
    public void GroceryItemService_DeleteGroceryItem_ReturnGroceryItemDto() {
        Long groceryItemId = 1L;
        GroceryItem groceryItem = new GroceryItem();
        groceryItem.setGroceryItemId(groceryItemId);

        when(groceryItemRepository.findById(groceryItemId)).thenReturn(Optional.of(groceryItem));

        ResponseEntity<GroceryItemDto> response = groceryItemService.deleteGroceryItem(groceryItemId);

        verify(groceryItemRepository).deleteById(groceryItemId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }



}
