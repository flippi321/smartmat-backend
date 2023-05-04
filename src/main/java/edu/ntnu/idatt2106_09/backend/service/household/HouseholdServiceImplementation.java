package edu.ntnu.idatt2106_09.backend.service.household;

import edu.ntnu.idatt2106_09.backend.authentication.AuthenticationResponse;
import edu.ntnu.idatt2106_09.backend.authentication.AuthenticationService;
import edu.ntnu.idatt2106_09.backend.authentication.RegistrationRequest;
import edu.ntnu.idatt2106_09.backend.dto.FridgeDto;
import edu.ntnu.idatt2106_09.backend.dto.HouseholdDto;
import edu.ntnu.idatt2106_09.backend.dto.ShoppinglistDto;
import edu.ntnu.idatt2106_09.backend.dto.UserDto;
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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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

    private HouseholdDto castHouseholdToDto(Household household){
        modelMapper = new ModelMapper();
        return modelMapper.map(household, HouseholdDto.class);
    }

    private Household castDtoToHousehold(HouseholdDto householdDto){
        modelMapper = new ModelMapper();
        return modelMapper.map(householdDto, Household.class);
    }

    private UserDto castUserToDto(User user){
        modelMapper = new ModelMapper();
        return modelMapper.map(user, UserDto.class);
    }

    private User castDtoToUser(UserDto userdto){
        return modelMapper.map(userdto, User.class);
    }

    public HouseholdServiceImplementation(HouseholdRepository householdRepository, FridgeRepository fridgeRepository, ShoppinglistRepository shoppinglistRepository, UserRepository userRepository) {
        this.householdRepository = householdRepository;
        this.fridgeRepository = fridgeRepository;
        this.shoppinglistRepository = shoppinglistRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<Household> getHouseholdById(Long householdId) {
        return householdRepository.findById(householdId);
    }


    @Override
    public Optional<HouseholdDto> getHouseholdByIdAsDto(Long householdId) {
        log.debug("Fetching Household with id: {}", householdId);
        Optional<HouseholdDto> householdDtoOptional = Optional.empty();
        try {
            Optional<Household> householdOptional = householdRepository.findById(householdId);
            if (householdOptional.isPresent()) {
                log.info("[x] Household with id {} found", householdId);

                Shoppinglist shoppinglist = shoppinglistRepository.findByHouseholdId(householdId);
                Fridge fridge = fridgeRepository.findByHouseholdIdAsFridge(householdId);

                Household household = householdOptional.get();
                household.setShoppinglist(shoppinglist);
                household.setFridge(fridge);

                ModelMapper modelMapper = new ModelMapper();
                HouseholdDto householdDto = modelMapper.map(household, HouseholdDto.class);
                householdDtoOptional = Optional.of(householdDto);
            } else {
                log.warn("[x] Household with id {} not found", householdId);
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching Household with id {}: {}", householdId, e.getMessage());
        }
        return householdDtoOptional;
    }


    @Override
    public ResponseEntity<HouseholdDto> getHouseholdByUserId(Integer userId) {
        log.debug("Fetching User with id: {}", userId);
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
            log.info("[x] User with id {} found", userId);
            Household household = user.getHousehold();
            Shoppinglist shoppinglist = shoppinglistRepository.findByHouseholdId(household.getHouseholdId());
            Fridge fridge = fridgeRepository.findByHouseholdIdAsFridge(household.getHouseholdId());
            household.setFridge(fridge);
            household.setShoppinglist(shoppinglist);
            HouseholdDto householdDto = castHouseholdToDto(household);
            UserDto userDto = castUserToDto(user); // Convert the User object to a UserDto object
            householdDto.setUserDto(userDto); // Set the userDto field of the HouseholdDto object
            return ResponseEntity.ok(householdDto);
        } catch (NotFoundException ex) {
            log.warn("[x] Exception caught: {}", ex.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<UserDto> addUserToHousehold(Long householdId, UserDto userDto) {
        return null;
    }


  /*  {
        "firstname" : "test1",
        "lastname" : "test1surname",
        "email" : "test1@test.com",
        "password" : "passord"
      }*/
    /**
    @Override
    public ResponseEntity<UserDto> addUserToHousehold(Long householdId, UserDto userDto) {
        log.debug("Fetching Household with id: {}", householdId);
        try {
            Household household = householdRepository.findById(householdId)
                    .orElseThrow(() -> new NotFoundException("Household with id " + householdId + " not found"));
            log.info("[x] Household with id {} found", householdId);

            RegistrationRequest request = new RegistrationRequest(userDto.getFirstname(), userDto.getLastname(), userDto.getEmail(), userDto.getPassword());
            AuthenticationResponse response = authenticationService.register(request);
            User user = userRepository.findById(response.getId())
                    .orElseThrow(() -> new NotFoundException("User with id " + response.getId() + " not found"));

            user.setHousehold(household);
            userRepository.save(user);

            UserDto newUserDto = castUserToDto(user);
            return ResponseEntity.ok(newUserDto);
        } catch (NotFoundException ex) {
            log.warn("[x] Exception caught: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
     */

    @Override
    public ResponseEntity<HouseholdDto> createHousehold(Integer userId, HouseholdDto householdDto) {
        HouseholdDto newHouseholdDto = null;
        try {
            log.debug("Creating new Household with name: {}", householdDto.getName());
            Household household = castDtoToHousehold(householdDto);
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

            log.info("[x] Household with id {} created", newHousehold.getHouseholdId());
            newHouseholdDto = castHouseholdToDto(household);

            if (currentUser != null) {
                UserDto userDto = castUserToDto(currentUser);
                newHouseholdDto.setUserDto(userDto);
            }
        } catch (Exception e) {
            log.error("An error occurred while creating a new household: {}", e.getMessage());
        }
        return ResponseEntity.ok(newHouseholdDto);
    }


        @Override
    public ResponseEntity<HouseholdDto> updateHousehold(Long householdId, HouseholdDto householdDto) {
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
            HouseholdDto updatedHouseholdDto = castHouseholdToDto(household);
            return ResponseEntity.ok(updatedHouseholdDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public Set<UserDto> getAllUsersInHousehold(Long householdId) {
        Set<UserDto> userDtos = new HashSet<>();
        try {
            log.debug("Getting all users in household with id: {}", householdId);
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
            log.info("[x] Found {} users in household with id {}", userDtos.size(), householdId);
        } catch (Exception e) {
            log.error("An error occurred while getting all users in household with id {}: {}", householdId, e.getMessage());
        }
        return userDtos;
    }
}
