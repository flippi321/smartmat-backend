package edu.ntnu.idatt2106_09.backend.service.household;

import edu.ntnu.idatt2106_09.backend.authentication.AuthenticationService;
import edu.ntnu.idatt2106_09.backend.dto.*;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.NotFoundException;
import edu.ntnu.idatt2106_09.backend.model.Fridge;
import edu.ntnu.idatt2106_09.backend.model.Household;
import edu.ntnu.idatt2106_09.backend.model.Shoppinglist;
import edu.ntnu.idatt2106_09.backend.model.user.User;
import edu.ntnu.idatt2106_09.backend.repository.FridgeRepository;
import edu.ntnu.idatt2106_09.backend.repository.HouseholdRepository;
import edu.ntnu.idatt2106_09.backend.repository.ShoppinglistRepository;
import edu.ntnu.idatt2106_09.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

/**
 * The HouseholdServiceImplementation class is responsible for implementing the logic for managing Household in the
 * application. It allows for updating, adding, retrieving and deleting households from the database.
 */
@Slf4j
@Service
public class HouseholdServiceImplementation implements HouseholdService {

    @Autowired
    private HouseholdRepository householdRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FridgeRepository fridgeRepository;

    @Autowired
    private ShoppinglistRepository shoppinglistRepository;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Casts a Household object to a HouseholdDtoForHouseholdService object using the ModelMapper library.
     *
     * @param household the Household object to be casted
     * @return a HouseholdDtoForHouseholdService object that represents the input Household object
     */
    private HouseholdDtoForHouseholdService castHouseholdToDto(Household household){
        modelMapper = new ModelMapper();
        return modelMapper.map(household, HouseholdDtoForHouseholdService.class);
    }

    /**
     * Converts a HouseholdDtoForHouseholdService object to a Household object using the ModelMapper library.
     *
     * @param householdDto The HouseholdDtoForHouseholdService object to be converted.
     * @return A Household object converted from the given HouseholdDtoForHouseholdService object.
     */
    private Household castDtoToHousehold(HouseholdDtoForHouseholdService householdDto){
        modelMapper = new ModelMapper();
        return modelMapper.map(householdDto, Household.class);
    }

    /**
     * Maps a User entity to a UserDto using ModelMapper.
     *
     * @param user the User entity to be mapped
     * @return the UserDto object that represents the User entity
     */
    private UserDto castUserToDto(User user){
        modelMapper = new ModelMapper();
        return modelMapper.map(user, UserDto.class);
    }

    /**
     * Maps a UserDto object to a User object using ModelMapper.
     *
     * @param userdto the UserDto object to map to a User object
     * @return the mapped User object
     */
    private User castDtoToUser(UserDto userdto){
        return modelMapper.map(userdto, User.class);
    }

    /**
     * Made for mocking in 'HouseholdServiceTest'
     */
    public HouseholdServiceImplementation(HouseholdRepository householdRepository, UserRepository userRepository,
                                          FridgeRepository fridgeRepository, ShoppinglistRepository
                                                  shoppinglistRepository) {
        this.householdRepository = householdRepository;
        this.userRepository = userRepository;
        this.fridgeRepository = fridgeRepository;
        this.shoppinglistRepository = shoppinglistRepository;

    }

    /**
     * Retrieves the household with the given ID from the database, if it exists.
     *
     * @param householdId the ID of the household to retrieve
     * @return an Optional containing the household with the given ID, or an empty Optional if no household is found
     */
    @Override
    public Optional<Household> getHouseholdById(Long householdId) {
        return householdRepository.findById(householdId);
    }

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
    @Override
    public Optional<HouseholdDtoForHouseholdService> getHouseholdByIdAsDto(Long householdId) {
        log.debug("[X] Fetching Household with id: {}", householdId);
        modelMapper = new ModelMapper();
        Optional<HouseholdDtoForHouseholdService> householdDtoOptional = Optional.empty();
        try {
            Optional<Household> householdOptional = householdRepository.findById(householdId);
            if (householdOptional.isPresent()) {
                log.info("[X] Household with id {} found", householdId);

                Shoppinglist shoppinglist = shoppinglistRepository.findByHouseholdIdAsShoppinglist(householdId);
                Fridge fridge = fridgeRepository.findByHouseholdIdAsFridge(householdId);

                Household household = householdOptional.get();
                household.setShoppinglist(shoppinglist);
                household.setFridge(fridge);
                HouseholdDtoForHouseholdService householdDto = new HouseholdDtoForHouseholdService();
                householdDto.setHouseholdId(household.getHouseholdId());
                householdDto.setName(household.getName());
                householdDto.setInvitationNr(household.getInvitationNr());
                householdDto.setFridge(modelMapper.map(household.getFridge(), FridgeDtoWithoutHousehold.class));
                householdDto.setShoppinglist(modelMapper.map(household.getShoppinglist(), ShoppinglistDto.class));
                householdDtoOptional = Optional.of(householdDto);
            } else {
                log.warn("[X] Household with id {} not found", householdId);
            }
        } catch (Exception e) {
            log.error("[X] An error occurred while fetching Household with id {}: {}", householdId, e.getMessage());
        }
        return householdDtoOptional;
    }

    /**
     * Retrieves a HouseholdDtoForHouseholdService object for the household associated with the given user id.
     *
     * @param userId the id of the user whose household should be retrieved
     * @return a ResponseEntity object with the retrieved HouseholdDtoForHouseholdService object in the body if the user and their household are found, or a not found response if the user is not found
     */
    @Override
    public ResponseEntity<HouseholdDtoForHouseholdService> getHouseholdByUserId(Integer userId) {
        log.debug("[X] Fetching User with id: {}", userId);
        modelMapper = new ModelMapper();
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
            log.info("[X] User with id {} found", userId);
            Household household = user.getHousehold();
            Shoppinglist shoppinglist = shoppinglistRepository.findByHouseholdIdAsShoppinglist(household.getHouseholdId());
            Fridge fridge = fridgeRepository.findByHouseholdIdAsFridge(household.getHouseholdId());
            household.setFridge(fridge);
            household.setShoppinglist(shoppinglist);
            HouseholdDtoForHouseholdService householdDto = new HouseholdDtoForHouseholdService();
            householdDto.setHouseholdId(household.getHouseholdId());
            householdDto.setName(household.getName());
            householdDto.setInvitationNr(household.getInvitationNr());
            householdDto.setFridge(modelMapper.map(household.getFridge(), FridgeDtoWithoutHousehold.class));
            householdDto.setShoppinglist(modelMapper.map(household.getShoppinglist(), ShoppinglistDto.class));

            UserDto userDto = castUserToDto(user); // Convert the User object to a UserDto object
            householdDto.setUserDto(userDto); // Set the userDto field of the HouseholdDto object
            return ResponseEntity.ok(householdDto);
        } catch (NotFoundException ex) {
            log.warn("[X] Exception caught: {}", ex.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Adds a user to a household with the given invitation number.
     *
     * @param invitationNr the invitation number of the household to add the user to
     * @param userId the id of the user to be added to the household
     * @return a ResponseEntity with the updated UserDto object
     * @throws ResponseStatusException if no household with the given invitation number or user with the given id exists
     */
    @Override
    public ResponseEntity<UserDto> addUserToHousehold(Long invitationNr, Integer userId) {
        log.debug("[X] Adding user to household with invitationNr: {}", invitationNr);
        Optional<Household> optionalHousehold = householdRepository.findByInvitationNr(invitationNr);
        if (optionalHousehold.isPresent()) {
            Household household = optionalHousehold.get();
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setHousehold(household);
                User updatedUser = userRepository.save(user);
                UserDto updatedUserDto = castUserToDto(updatedUser);
                log.info("[X] User with id {} added to household with id {}", updatedUser.getId(), household.getHouseholdId());
                return ResponseEntity.ok(updatedUserDto);
            } else {
                log.error("[X] No user with id {} exists", userId);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user with the given id exists");
            }
        } else {
            log.error("[X] No household with invitationNr {} exists", invitationNr);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No household with the given invitationNr exists");
        }
    }

    /**
     * Creates a new household and saves it to the database. Assigns the current user as a member of the new household.
     * Generates a unique invitation number for the household and creates a new fridge and shopping list for it.
     *
     * @param userId the ID of the current user
     * @param householdDto the DTO containing the data for the new household to be created
     * @return a ResponseEntity containing the DTO for the newly created household and a HTTP status code indicating
     *         successor failure of the request
     */
    @Override
    public ResponseEntity<HouseholdDtoForHouseholdService> createHousehold(Integer userId, HouseholdDtoForHouseholdService householdDto) {
        HouseholdDtoForHouseholdService newHouseholdDto = new HouseholdDtoForHouseholdService();
        try {
            log.debug("[X] Creating new Household with name: {}", householdDto.getName());
            Household household = castDtoToHousehold(householdDto);

            Random rand = new Random();
            long invitationNr;
            Optional<Household> existingHousehold;
            do {
                invitationNr = 100000L + (long)(rand.nextDouble() * (999999L - 100000L));
                existingHousehold = householdRepository.findByInvitationNr(invitationNr);
            } while (existingHousehold.isPresent());
            household.setInvitationNr(invitationNr);

            Fridge fridge = new Fridge();
            fridge.setName(householdDto.getFridge().getName());
            fridgeRepository.save(fridge);
            household.setFridge(fridge);
            Shoppinglist shoppinglist = new Shoppinglist();
            shoppinglist.setName(householdDto.getShoppinglist().getName());
            shoppinglistRepository.save(shoppinglist);
            household.setShoppinglist(shoppinglist);
            Household newHousehold = householdRepository.save(household);

            fridge.setHousehold(newHousehold);
            fridgeRepository.save(fridge);
            shoppinglist.setHousehold(newHousehold);
            shoppinglistRepository.save(shoppinglist);
            Optional<User> optionalUser = userRepository.findById(userId);
            User currentUser = null;
            if (optionalUser.isPresent()) {
                currentUser = optionalUser.get();
                currentUser.setHousehold(newHousehold);
                userRepository.save(currentUser);
            }

            log.info("[X] Household with id {} created", newHousehold.getHouseholdId());

            newHouseholdDto.setHouseholdId(household.getHouseholdId());
            newHouseholdDto.setName(household.getName());
            newHouseholdDto.setInvitationNr(household.getInvitationNr());
            newHouseholdDto.setFridge(modelMapper.map(household.getFridge(), FridgeDtoWithoutHousehold.class));
            newHouseholdDto.setShoppinglist(modelMapper.map(household.getShoppinglist(), ShoppinglistDto.class));

            if (currentUser != null) {
                UserDto userDto = castUserToDto(currentUser);
                newHouseholdDto.setUserDto(userDto);
            }
        } catch (Exception e) {
            log.error("[X] An error occurred while creating a new household: {}", e.getMessage());
        }
        return ResponseEntity.ok(newHouseholdDto);
    }

    /**
     * Updates an existing household with the provided id using the provided household DTO object.
     * If the household with the provided id does not exist, a 404 Not Found response is returned.
     *
     * @param householdId The id of the household to be updated
     * @param householdDto The DTO object containing the updated household data
     * @return ResponseEntity A response entity with the updated household DTO object if the update was successful, or
     *         a 404 Not Found response if the household with the provided id does not exist
     */
    @Override
    public ResponseEntity<HouseholdDtoForHouseholdService> updateHousehold(Long householdId, HouseholdDtoForHouseholdService householdDto) {
        modelMapper = new ModelMapper();
        Optional<Household> householdOptional = householdRepository.findById(householdId);
        if (householdOptional.isPresent()) {
            Household household = householdOptional.get();
            if (householdDto.getName() != null) {
                household.setName(householdDto.getName());
            }
            if (householdDto.getFridge() != null && householdDto.getFridge().getName() != null) {
                household.getFridge().setName(householdDto.getFridge().getName());
            }
            if (householdDto.getShoppinglist() != null && householdDto.getShoppinglist().getName() != null) {
                household.getShoppinglist().setName(householdDto.getShoppinglist().getName());
            }
            householdRepository.save(household);
            HouseholdDtoForHouseholdService updatedHouseholdDto = new HouseholdDtoForHouseholdService();
            updatedHouseholdDto.setHouseholdId(household.getHouseholdId());
            updatedHouseholdDto.setName(household.getName());
            updatedHouseholdDto.setInvitationNr(household.getInvitationNr());
            updatedHouseholdDto.setFridge(modelMapper.map(household.getFridge(), FridgeDtoWithoutHousehold.class));
            updatedHouseholdDto.setShoppinglist(modelMapper.map(household.getShoppinglist(), ShoppinglistDto.class));

            return ResponseEntity.ok(updatedHouseholdDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Returns a set of UserDto objects containing information about all users in the household with the specified
     * household ID. If the household is found, it retrieves all users associated with the household and maps their
     * information to a UserDto object, which is then added to a set of UserDto objects. If an error occurs while
     * retrieving the users, an error message is logged and an empty set is returned.
     *
     * @param householdId the ID of the household to retrieve users from
     * @return a set of UserDto objects containing information about all users in the household, or an empty set if an error occurs
     */
    @Override
    public Set<UserDto> getAllUsersInHousehold(Long householdId) {
        Set<UserDto> userDtos = new HashSet<>();
        try {
            log.debug("[X] Getting all users in household with id: {}", householdId);
            Optional<Household> optionalHousehold = householdRepository.findById(householdId);
            if (optionalHousehold.isPresent()) {
                Household household = optionalHousehold.get();
                Set<User> users = userRepository.findAllByHousehold(household);
                for (User user : users) {
                    UserDto userDto = new UserDto();
                    userDto.setId(user.getId());
                    userDto.setFirstname(user.getFirstname());
                    userDto.setLastname(user.getLastname());
                    userDto.setEmail(user.getEmail());
                    userDtos.add(userDto);
                }
            }
            log.info("[X] Found {} users in household with id {}", userDtos.size(), householdId);
        } catch (Exception e) {
            log.error("[X] An error occurred while getting all users in household with id {}: {}", householdId, e.getMessage());
        }
        return userDtos;
    }
}
