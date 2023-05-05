package edu.ntnu.idatt2106_09.backend.service.user;

import edu.ntnu.idatt2106_09.backend.dto.UserDto;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.InternalServerErrorException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service interface for handling user related operations. Provides methods for updating and retrieving user data.
 * Also provides methods for converting between User and UserDto.
 */
public interface UserService {

    /**
     * Updates an existing user with the given email address.
     *
     * @param oldEmail The email address of the user to be updated.
     * @param userDto The UserDto object containing the updated user information.
     * @return A ResponseEntity containing the updated UserDto object.
     * @throws ResponseStatusException If the user with the given email address is not found.
     * @throws InternalServerErrorException If an unexpected error occurs during the update.
     */
    public ResponseEntity<UserDto> updateUser(String oldEmail, UserDto userDto);

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the identifier of the user to retrieve
     * @return a ResponseEntity containing the user DTO if found, or an error response if the user was not found
     * @throws ResponseStatusException if the user was not found
     * @throws InternalServerErrorException if an unexpected error occurs during retrieval
     */
    public ResponseEntity<UserDto> getUserById(Integer id);
}
