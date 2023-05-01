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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShoppinglistServiceTest {

    @Mock
    private GroceryItemRepository groceryItemRepository;

    @Mock
    private FridgeRepository fridgeRepository;

    @Mock
    private ShoppinglistRepository shoppinglistRepository;


    @InjectMocks
    private ShoppinglistServiceImplementation shoppinglistService;

    @Autowired
    private ModelMapper modelMapper;


    /*@Test
    public void ShoppinglistService_AddShoppinglist() {
        Long householdId = 1L;
        String shoppinglistName = "Test Shoppinglist";

        Household household = new Household();
        household.setHouseholdId(householdId);
        HouseholdDto householdDto =  modelMapper.map(household, HouseholdDto.class);

        ShoppinglistDto shoppinglistDto = new ShoppinglistDto();
        shoppinglistDto.setName(shoppinglistName);
        shoppinglistDto.setHousehold(householdDto);

        when(householdService.getHouseholdById(householdId)).thenReturn(Optional.of(household));
        when(shoppinglistRepository.findByNameAndHouseholdId(shoppinglistName, householdId)).thenReturn(null);
        when(shoppinglistRepository.findByHouseholdId(householdId)).thenReturn(new ArrayList<>());

        ShoppinglistDto savedShoppinglistDto = shoppinglistService.addShoppinglist(shoppinglistDto);

        assertThat(savedShoppinglistDto).isNotNull();
        assertThat(savedShoppinglistDto.getName()).isEqualTo(shoppinglistName);
    }*/

}
