package edu.ntnu.idatt2106_09.backend.service.household;

import edu.ntnu.idatt2106_09.backend.dto.HouseholdDtoForHouseholdService;
import edu.ntnu.idatt2106_09.backend.dto.UserDto;
import edu.ntnu.idatt2106_09.backend.model.Household;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;

/**
 * Service interface for handling household related operations. Provides methods for fetching, creating, and updating
 * households, adding users to households, and retrieving information about users in households. Also provides methods
 * for converting between Household and HouseholdDtoForHouseholdService and between User and UserDto.
 */
@Service
public interface HouseholdService {

    /**
     * Retrieves the household with the given ID from the database, if it exists.
     *
     * @param householdId the ID of the household to retrieve
     * @return an Optional containing the household with the given ID, or an empty Optional if no household is found
     */
    public Optional<Household> getHouseholdById(Long householdId);

    /**
     * Retrieves a Household with the specified id as a DTO object.
     *
     * @param householdId the id of the Household to retrieve.
     * @return an Optional object containing a DTO representation of the Household if it exists, or an empty Optional if
     *         it does not. If an error occurs while retrieving the Household, an empty Optional is returned.
     *         The DTO contains the Household's id, name, invitation number, Fridge object
     *         (as a FridgeDtoWithoutHousehold), and Shoppinglist object (as a ShoppinglistDto). If the Household has
     *         no associated Fridge or Shoppinglist, the corresponding DTO object is null.
     @throws NullPointerException if the specified id is null.
     */
    public Optional<HouseholdDtoForHouseholdService> getHouseholdByIdAsDto(Long householdId);

    /**
     * Retrieves a HouseholdDtoForHouseholdService object for the household associated with the given user id.
     *
     * @param userId the id of the user whose household should be retrieved
     * @return a ResponseEntity object with the retrieved HouseholdDtoForHouseholdService object in the body if the user and their household are found, or a not found response if the user is not found
     */
    public ResponseEntity<HouseholdDtoForHouseholdService> getHouseholdByUserId(Integer userId);

    /**
     * Adds a user to a household with the given invitation number.
     *
     * @param invitationNr the invitation number of the household to add the user to
     * @param userId the id of the user to be added to the household
     * @return a ResponseEntity with the updated UserDto object
     * @throws ResponseStatusException if no household with the given invitation number or user with the given id exists
     */
    public ResponseEntity<UserDto> addUserToHousehold(Long invitationNr, Integer userId);

    /**
     * Creates a new household and saves it to the database. Assigns the current user as a member of the new household.
     * Generates a unique invitation number for the household and creates a new fridge and shopping list for it.
     *
     * @param userId the ID of the current user
     * @param householdDto the DTO containing the data for the new household to be created
     * @return a ResponseEntity containing the DTO for the newly created household and a HTTP status code indicating
     *         successor failure of the request
     */
    public ResponseEntity<HouseholdDtoForHouseholdService> createHousehold(Integer userId, HouseholdDtoForHouseholdService householdDto);

    /**
     * Updates an existing household with the provided id using the provided household DTO object.
     * If the household with the provided id does not exist, a 404 Not Found response is returned.
     *
     * @param householdId The id of the household to be updated
     * @param householdDto The DTO object containing the updated household data
     * @return ResponseEntity A response entity with the updated household DTO object if the update was successful, or
     *         a 404 Not Found response if the household with the provided id does not exist
     */
    public ResponseEntity<HouseholdDtoForHouseholdService> updateHousehold(Long householdId, HouseholdDtoForHouseholdService householdDto);

    /**
     * Returns a set of UserDto objects containing information about all users in the household with the specified
     * household ID. If the household is found, it retrieves all users associated with the household and maps their
     * information to a UserDto object, which is then added to a set of UserDto objects. If an error occurs while
     * retrieving the users, an error message is logged and an empty set is returned.
     *
     * @param householdId the ID of the household to retrieve users from
     * @return a set of UserDto objects containing information about all users in the household, or an empty set if an error occurs
     */
    public Set<UserDto> getAllUsersInHousehold(Long householdId);
}
