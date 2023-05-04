package edu.ntnu.idatt2106_09.backend.service;

import edu.ntnu.idatt2106_09.backend.dto.HouseholdDto;
import edu.ntnu.idatt2106_09.backend.dto.ShoppinglistDto;
import edu.ntnu.idatt2106_09.backend.model.Household;
import edu.ntnu.idatt2106_09.backend.model.Shoppinglist;
import edu.ntnu.idatt2106_09.backend.repository.FridgeRepository;
import edu.ntnu.idatt2106_09.backend.repository.GroceryItemRepository;
import edu.ntnu.idatt2106_09.backend.repository.ShoppinglistRepository;
import edu.ntnu.idatt2106_09.backend.service.groceryItem.GroceryItemServiceImplementation;
import edu.ntnu.idatt2106_09.backend.service.shoppinglist.ShoppinglistServiceImplementation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.BadRequestException;

import edu.ntnu.idatt2106_09.backend.repository.HouseholdRepository;
import edu.ntnu.idatt2106_09.backend.service.household.HouseholdServiceImplementation;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShoppinglistServiceTest {

    @Mock
    private ShoppinglistRepository shoppinglistRepository;

    @Mock
    private HouseholdRepository householdRepository;


    @InjectMocks
    private ShoppinglistServiceImplementation shoppinglistService;

    @Mock
    private HouseholdServiceImplementation householdService;


    @Test
    public void addShoppinglistWithInvalidNameThrowsBadRequestExceptionTest() {
        householdService = new HouseholdServiceImplementation(householdRepository);
        shoppinglistService = new ShoppinglistServiceImplementation(shoppinglistRepository, householdService, householdRepository);
        ShoppinglistDto shoppinglistDto = new ShoppinglistDto();
        shoppinglistDto.setName("");
        shoppinglistDto.setHousehold(new HouseholdDto());
        lenient().when(householdService.getHouseholdById(anyLong())).thenReturn(Optional.of(new Household()));

        assertThatThrownBy(() -> shoppinglistService.addShoppinglist(shoppinglistDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Shoppinglist name cannot be empty");
    }

    @Test
    void updateShoppinglistTest() {
        Household household = new Household();
        household.setHouseholdId(1L);
        Shoppinglist shoppinglist = new Shoppinglist();
        shoppinglist.setShoppinglistId(1L);
        shoppinglist.setName("Old Shoppinglist Name");
        shoppinglist.setHousehold(household);

        ShoppinglistDto shoppinglistDto = new ShoppinglistDto();
        shoppinglistDto.setShoppinglistID(1L);
        shoppinglistDto.setName("New Shoppinglist Name");
        shoppinglistDto.setHousehold(new HouseholdDto());
        shoppinglistDto.getHousehold().setHouseholdId(1L);

        when(shoppinglistRepository.findById(1L)).thenReturn(Optional.of(shoppinglist));
        when(householdService.getHouseholdById(1L)).thenReturn(Optional.of(household));
        when(shoppinglistRepository.save(any(Shoppinglist.class))).thenReturn(shoppinglist);
        ShoppinglistDto updatedShoppinglistDto = shoppinglistService.updateShoppinglist(shoppinglistDto);

        verify(shoppinglistRepository).findById(1L);
        verify(shoppinglistRepository).save(any(Shoppinglist.class));

        assertEquals(shoppinglistDto.getShoppinglistID(), updatedShoppinglistDto.getShoppinglistID());
        assertEquals(shoppinglistDto.getName(), updatedShoppinglistDto.getName());
        assertEquals(shoppinglistDto.getHousehold().getHouseholdId(), updatedShoppinglistDto.getHousehold().getHouseholdId());
    }


    @Test
    void getShoppinglistByIdTest() {
        householdService = new HouseholdServiceImplementation(householdRepository);
        shoppinglistService = new ShoppinglistServiceImplementation(shoppinglistRepository, householdService, householdRepository);

        Shoppinglist shoppinglist = new Shoppinglist();
        shoppinglist.setShoppinglistId(1L);
        shoppinglist.setName("Test Shoppinglist");

        when(shoppinglistRepository.findById(1L)).thenReturn(Optional.of(shoppinglist));

        ShoppinglistDto fetchedShoppinglistDto = shoppinglistService.getShoppinglistById(1L);

        verify(shoppinglistRepository).findById(1L);
        assertEquals(shoppinglist.getShoppinglistId(), fetchedShoppinglistDto.getShoppinglistID());
        assertEquals(shoppinglist.getName(), fetchedShoppinglistDto.getName());
    }

    @Test
    void deleteShoppinglistTest() {
        Long shoppinglistId = 1L;
        Household household = new Household();
        household.setHouseholdId(1L);

        Shoppinglist shoppinglist = new Shoppinglist();
        shoppinglist.setShoppinglistId(shoppinglistId);
        shoppinglist.setHousehold(household);

        when(shoppinglistRepository.findById(shoppinglistId)).thenReturn(Optional.of(shoppinglist));
        doNothing().when(shoppinglistRepository).delete(any(Shoppinglist.class));
        when(householdRepository.save(any(Household.class))).thenReturn(household);

        shoppinglistService.deleteShoppinglist(shoppinglistId);

        verify(shoppinglistRepository).findById(shoppinglistId);
        verify(shoppinglistRepository).delete(any(Shoppinglist.class));
        verify(householdRepository).save(any(Household.class));

        assertNull(shoppinglist.getHousehold());
        assertNull(household.getHouseholdId());
    }
    }
