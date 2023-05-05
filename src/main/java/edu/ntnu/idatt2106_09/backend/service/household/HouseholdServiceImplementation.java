package edu.ntnu.idatt2106_09.backend.service.household;

import edu.ntnu.idatt2106_09.backend.authentication.AuthenticationResponse;
import edu.ntnu.idatt2106_09.backend.authentication.AuthenticationService;
import edu.ntnu.idatt2106_09.backend.authentication.RegistrationRequest;
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


    private Household castDtoToHousehold(HouseholdDtoForHouseholdService householdDto){
        modelMapper = new ModelMapper();
        return modelMapper.map(householdDto, Household.class);
    }

    private UserDto castUserToDto(User user){
        modelMapper = new ModelMapper();
        return modelMapper.map(user, UserDto.class);
    }



    public HouseholdServiceImplementation(HouseholdRepository householdRepository, UserRepository userRepository, FridgeRepository fridgeRepository, ShoppinglistRepository shoppinglistRepository) {
        this.householdRepository = householdRepository;
        this.userRepository = userRepository;
        this.fridgeRepository = fridgeRepository;
        this.shoppinglistRepository = shoppinglistRepository;

    }


    @Override
    public Optional<Household> getHouseholdById(Long householdId) {
        return householdRepository.findById(householdId);
    }


    @Override
    public Optional<HouseholdDtoForHouseholdService> getHouseholdByIdAsDto(Long householdId) {
        modelMapper = new ModelMapper();
        log.debug("Fetching Household with id: {}", householdId);
        Optional<HouseholdDtoForHouseholdService> householdDtoOptional = Optional.empty();
        try {
            Optional<Household> householdOptional = householdRepository.findById(householdId);
            if (householdOptional.isPresent()) {
                log.info("[x] Household with id {} found", householdId);

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
                log.warn("[x] Household with id {} not found", householdId);
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching Household with id {}: {}", householdId, e.getMessage());
        }
        return householdDtoOptional;
    }


    @Override
    public ResponseEntity<HouseholdDtoForHouseholdService> getHouseholdByUserId(Integer userId) {
        modelMapper = new ModelMapper();
        log.debug("Fetching User with id: {}", userId);
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
            log.info("[x] User with id {} found", userId);
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
            log.warn("[x] Exception caught: {}", ex.getMessage());
            return ResponseEntity.notFound().build();
        }
    }


  /*  {
        "firstname" : "test1",
        "lastname" : "test1surname",
        "email" : "test1@test.com",
        "password" : "passord"
      }*/
    @Override
    public ResponseEntity<UserDto> addUserToHousehold(Long invitationNr, Integer userId) {
        log.debug("Adding user to household with invitationNr: {}", invitationNr);
        Optional<Household> optionalHousehold = householdRepository.findByInvitationNr(invitationNr);
        if (optionalHousehold.isPresent()) {
            Household household = optionalHousehold.get();
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setHousehold(household);
                User updatedUser = userRepository.save(user);
                UserDto updatedUserDto = castUserToDto(updatedUser);
                log.info("User with id {} added to household with id {}", updatedUser.getId(), household.getHouseholdId());
                return ResponseEntity.ok(updatedUserDto);
            } else {
                log.error("No user with id {} exists", userId);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user with the given id exists");
            }
        } else {
            log.error("No household with invitationNr {} exists", invitationNr);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No household with the given invitationNr exists");
        }
    }

    @Override
    public ResponseEntity<HouseholdDtoForHouseholdService> createHousehold(Integer userId, HouseholdDtoForHouseholdService householdDto) {
        HouseholdDtoForHouseholdService newHouseholdDto = new HouseholdDtoForHouseholdService();
        try {
            log.debug("Creating new Household with name: {}", householdDto.getName());
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

            log.info("[x] Household with id {} created", newHousehold.getHouseholdId());

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
            log.error("An error occurred while creating a new household: {}", e.getMessage());
        }
        return ResponseEntity.ok(newHouseholdDto);
    }


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
