package edu.ntnu.idatt2106_09.backend.service.user;

import edu.ntnu.idatt2106_09.backend.dto.UserDto;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.InternalServerErrorException;
import edu.ntnu.idatt2106_09.backend.model.user.User;
import edu.ntnu.idatt2106_09.backend.repository.FridgeRepository;
import edu.ntnu.idatt2106_09.backend.repository.GroceryItemRepository;
import edu.ntnu.idatt2106_09.backend.repository.ShoppinglistRepository;
import edu.ntnu.idatt2106_09.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class UserServiceImplementation implements UserService{

    @Autowired
    private UserRepository userRepository;

    public UserServiceImplementation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public ResponseEntity<UserDto> updateUser(String oldEmail, UserDto userDto) {
        log.debug("[X] Attempting to update user with email: {}", userDto.getEmail());
        try {
            User user = userRepository.findByEmail(oldEmail)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));


            user.setEmail(userDto.getEmail());
            user.setFirstname(userDto.getFirstname());
            user.setLastname(userDto.getLastname());
            userRepository.save(user);

            UserDto updatedUserDto = new UserDto();
            updatedUserDto.setFirstname(user.getFirstname());
            updatedUserDto.setLastname(user.getLastname());
            updatedUserDto.setEmail(user.getEmail());

            log.info("[X] User updated successfully: {}", updatedUserDto);
            return ResponseEntity.ok(updatedUserDto);
        } catch (ResponseStatusException e) {
            log.warn("[X] User update failed: {} - {}", userDto.getEmail(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("[X] Unexpected error during user update: {} - {}", userDto.getEmail(), e.getMessage(), e);
            throw new InternalServerErrorException("An unexpected error occurred during update.");
        }
    }


    @Override
    public ResponseEntity<UserDto> getUserById(Integer id) {
        log.debug("[X] Attempting to retrieve user with id: {}", id);
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            UserDto userDto = new UserDto();
            userDto.setFirstname(user.getFirstname());
            userDto.setLastname(user.getLastname());
            userDto.setEmail(user.getEmail());

            log.info("[X] User retrieved successfully: {}", userDto);
            return ResponseEntity.ok(userDto);
        } catch (ResponseStatusException e) {
            log.warn("[X] User retrieval failed: {} - {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("[X] Unexpected error during user retrieval: {} - {}", id, e.getMessage(), e);
            throw new InternalServerErrorException("An unexpected error occurred during retrieval.");
        }
    }
}
