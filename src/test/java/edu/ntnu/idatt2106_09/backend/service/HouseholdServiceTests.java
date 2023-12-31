package edu.ntnu.idatt2106_09.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;

import edu.ntnu.idatt2106_09.backend.dto.*;
import edu.ntnu.idatt2106_09.backend.model.*;
import edu.ntnu.idatt2106_09.backend.model.user.User;
import edu.ntnu.idatt2106_09.backend.repository.*;
import edu.ntnu.idatt2106_09.backend.service.household.HouseholdServiceImplementation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class HouseholdServiceTests {
    @Mock
    private HouseholdRepository householdRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FridgeRepository fridgeRepository;

    @Mock
    private ShoppinglistRepository shoppinglistRepository;


    @InjectMocks
    private HouseholdServiceImplementation householdService;

    @Test
    public void getHouseholdByIdAsDtoTest() {
        Long householdId = 1L;
        Household household = new Household();
        household.setHouseholdId(householdId);
        household.setName("Test Household");
        Fridge fridge = new Fridge();
        fridge.setName("Test Fridge");
        Shoppinglist shoppinglist = new Shoppinglist();
        shoppinglist.setName("Test Shoppinglist");

        when(householdRepository.findById(householdId)).thenReturn(Optional.of(household));
        when(fridgeRepository.findByHouseholdIdAsFridge(householdId)).thenReturn(fridge);
        when(shoppinglistRepository.findByHouseholdIdAsShoppinglist(householdId)).thenReturn(shoppinglist);

        Optional<HouseholdDtoForHouseholdService> response = householdService.getHouseholdByIdAsDto(householdId);
        assertThat(response.isPresent()).isTrue();
        assertThat(response.get().getName()).isEqualTo(household.getName());
        assertThat(response.get().getFridge().getName()).isEqualTo(fridge.getName());
        assertThat(response.get().getShoppinglist().getName()).isEqualTo(shoppinglist.getName());

        assertThat(response.get().getHouseholdId()).isEqualTo(household.getHouseholdId());
        assertThat(response.get().getFridge()).isNotNull();
        assertThat(response.get().getShoppinglist()).isNotNull();
    }



        @Test
    public void getHouseholdByUserIdTest() {
        Integer userId = 1;
        User user = new User();
        user.setId(userId);
        Household household = new Household();
        household.setHouseholdId(1L);
        household.setName("Test Household");
        Fridge fridge = new Fridge();
        fridge.setName("Test Fridge");
        Shoppinglist shoppinglist = new Shoppinglist();
        shoppinglist.setName("Test Shoppinglist");
        user.setHousehold(household);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(fridgeRepository.findByHouseholdIdAsFridge(household.getHouseholdId())).thenReturn(fridge);
        when(shoppinglistRepository.findByHouseholdIdAsShoppinglist(household.getHouseholdId())).thenReturn(shoppinglist);

        ResponseEntity<HouseholdDtoForHouseholdService> response = householdService.getHouseholdByUserId(userId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo(household.getName());
        assertThat(response.getBody().getFridge().getName()).isEqualTo(fridge.getName());
        assertThat(response.getBody().getShoppinglist().getName()).isEqualTo(shoppinglist.getName());
    }




    @Test
    public void createHouseholdTest() {
        HouseholdDtoForHouseholdService householdDto = new HouseholdDtoForHouseholdService();
        householdDto.setName("Test Household");
        FridgeDtoWithoutHousehold fridgeDto = new FridgeDtoWithoutHousehold();
        fridgeDto.setName("Test Fridge");
        householdDto.setFridge(fridgeDto);
        ShoppinglistDto shoppinglistDto = new ShoppinglistDto();
        shoppinglistDto.setName("Test Shoppinglist");
        householdDto.setShoppinglist(shoppinglistDto);
        Integer userId = 1;

        User user = new User();
        user.setId(userId);

        Household newHousehold = new Household();
        newHousehold.setHouseholdId(1L);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(householdRepository.save(any(Household.class))).thenReturn(newHousehold);

        ResponseEntity<HouseholdDtoForHouseholdService> response = householdService.createHousehold(userId, householdDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo(householdDto.getName());
        assertThat(response.getBody().getFridge().getName()).isEqualTo(fridgeDto.getName());
        assertThat(response.getBody().getShoppinglist().getName()).isEqualTo(shoppinglistDto.getName());

    }

    @Test
    public void updateHouseholdTest() {
        Long householdId = 1L;
        Household household = new Household();
        household.setHouseholdId(householdId);
        household.setName("Test Household");
        Fridge fridge = new Fridge();
        fridge.setName("Test Fridge");
        Shoppinglist shoppinglist = new Shoppinglist();
        shoppinglist.setName("Test Shoppinglist");
        household.setFridge(fridge);
        household.setShoppinglist(shoppinglist);

        HouseholdDtoForHouseholdService householdDto = new HouseholdDtoForHouseholdService();
        householdDto.setName("Updated Household");
        FridgeDtoWithoutHousehold fridgeDto = new FridgeDtoWithoutHousehold();
        fridgeDto.setName("Updated Fridge");
        ShoppinglistDto shoppinglistDto = new ShoppinglistDto();
        shoppinglistDto.setName("Updated Shoppinglist");
        householdDto.setFridge(fridgeDto);
        householdDto.setShoppinglist(shoppinglistDto);

        when(householdRepository.findById(householdId)).thenReturn(Optional.of(household));

        ResponseEntity<HouseholdDtoForHouseholdService> response = householdService.updateHousehold(householdId, householdDto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo(householdDto.getName());
        assertThat(response.getBody().getFridge().getName()).isEqualTo(fridgeDto.getName());
        assertThat(response.getBody().getShoppinglist().getName()).isEqualTo(shoppinglistDto.getName());
    }

    @Test
    public void getAllUsersInHouseholdTest() {
        Long householdId = 1L;
        Household household = new Household();
        household.setHouseholdId(householdId);
        household.setName("Test Household");

        User user1 = new User();
        user1.setId(1);
        user1.setFirstname("Test");
        user1.setLastname("User1");
        user1.setEmail("test.user1@example.com");
        user1.setHousehold(household);

        User user2 = new User();
        user2.setId(2);
        user2.setFirstname("Test");
        user2.setLastname("User2");
        user2.setEmail("test.user2@example.com");
        user2.setHousehold(household);

        Set<User> users = new HashSet<>();
        users.add(user1);
        users.add(user2);

        when(householdRepository.findById(householdId)).thenReturn(Optional.of(household));
        when(userRepository.findAllByHousehold(household)).thenReturn(users);

        Set<UserDto> result = householdService.getAllUsersInHousehold(householdId);
        assertThat(result).hasSize(users.size());

        verify(householdRepository).findById(householdId);
        verify(userRepository).findAllByHousehold(household);
    }


    @Test
    public void addUserToHouseholdTest() {
        Long invitationNr = 1L;
        Integer userId = 1;
        Household household = new Household();
        household.setInvitationNr(invitationNr);
        User user = new User();
        user.setId(userId);

        when(householdRepository.findByInvitationNr(invitationNr)).thenReturn(Optional.of(household));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        ResponseEntity<UserDto> response = householdService.addUserToHousehold(invitationNr, userId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        verify(householdRepository).findByInvitationNr(invitationNr);
        verify(userRepository).findById(userId);
        verify(userRepository).save(user);
    }

}
