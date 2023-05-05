package edu.ntnu.idatt2106_09.backend.service;

import edu.ntnu.idatt2106_09.backend.dto.UserDto;
import edu.ntnu.idatt2106_09.backend.model.user.User;
import edu.ntnu.idatt2106_09.backend.repository.FridgeRepository;
import edu.ntnu.idatt2106_09.backend.repository.HouseholdRepository;
import edu.ntnu.idatt2106_09.backend.repository.ShoppinglistRepository;
import edu.ntnu.idatt2106_09.backend.repository.UserRepository;
import edu.ntnu.idatt2106_09.backend.service.household.HouseholdServiceImplementation;
import edu.ntnu.idatt2106_09.backend.service.shoppinglist.ShoppinglistServiceImplementation;
import edu.ntnu.idatt2106_09.backend.service.user.UserServiceImplementation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImplementation userService;


    @Test
    public void testUpdateUser() {
        String oldEmail = "oldEmail@example.com";
        UserDto userDto = new UserDto();
        userDto.setEmail("newEmail@example.com");
        userDto.setFirstname("NewFirstName");
        userDto.setLastname("NewLastName");

        User user = new User();
        user.setEmail(oldEmail);
        user.setFirstname("OldFirstName");
        user.setLastname("OldLastName");

        when(userRepository.findByEmail(oldEmail)).thenReturn(Optional.of(user));

        ResponseEntity<UserDto> response = userService.updateUser(oldEmail, userDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserDto updatedUserDto = response.getBody();
        assertEquals(userDto.getEmail(), updatedUserDto.getEmail());
        assertEquals(userDto.getFirstname(), updatedUserDto.getFirstname());
        assertEquals(userDto.getLastname(), updatedUserDto.getLastname());

        verify(userRepository).save(user);
    }

    @Test
    public void testGetUserById() {
        // Given
        Integer id = 1;
        User user = new User();
        user.setId(id);
        user.setEmail("email@example.com");
        user.setFirstname("FirstName");
        user.setLastname("LastName");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // When
        ResponseEntity<UserDto> response = userService.getUserById(id);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserDto userDto = response.getBody();
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getFirstname(), userDto.getFirstname());
        assertEquals(user.getLastname(), userDto.getLastname());

        verify(userRepository).findById(id);
    }
}
