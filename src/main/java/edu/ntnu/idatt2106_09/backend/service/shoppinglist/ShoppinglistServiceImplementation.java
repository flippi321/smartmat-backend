package edu.ntnu.idatt2106_09.backend.service.shoppinglist;

import edu.ntnu.idatt2106_09.backend.dto.ShoppinglistDto;
import edu.ntnu.idatt2106_09.backend.dto.ShoppinglistDto;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.BadRequestException;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.NotFoundException;
import edu.ntnu.idatt2106_09.backend.model.Shoppinglist;
import edu.ntnu.idatt2106_09.backend.model.Household;
import edu.ntnu.idatt2106_09.backend.model.Shoppinglist;
import edu.ntnu.idatt2106_09.backend.repository.*;
import edu.ntnu.idatt2106_09.backend.repository.ShoppinglistRepository;
import edu.ntnu.idatt2106_09.backend.service.household.HouseholdServiceImplementation;
import edu.ntnu.idatt2106_09.backend.service.shoppinglist.ShoppinglistService;
import edu.ntnu.idatt2106_09.backend.service.household.HouseholdService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 The ShoppinglistServiceImplementation class is responsible for implementing the logic for managing Shoppinglists in the application.
 It allows for updating, adding, retrieving and deleting shoppinglists from the database.
 */
@Slf4j
@Service
public class ShoppinglistServiceImplementation implements ShoppinglistService {

    @Autowired
    private ShoppinglistRepository shoppinglistRepository;

    @Autowired
    private HouseholdService householdService;

    @Autowired
    private HouseholdRepository householdRepository;

    @Autowired
    private ModelMapper modelMapper;
    /**
     * Made for mocking in 'ShoppinglistServiceTest'
     */
    public ShoppinglistServiceImplementation(ShoppinglistRepository shoppinglistRepository,
                                       HouseholdServiceImplementation householdService, HouseholdRepository householdRepository) {
        this.shoppinglistRepository = shoppinglistRepository;
        this.householdService = householdService;
        this.householdRepository = householdRepository;
    }

    /**
     Converts a Shoppinglist entity to a ShoppinglistDto object using ModelMapper.
     @param shoppinglist the Shoppinglist entity to be converted to a ShoppinglistDto object
     @return the ShoppinglistDto object obtained from the conversion
     */
    private ShoppinglistDto castShoppinglistToDto(Shoppinglist shoppinglist) {
        modelMapper = new ModelMapper();
        return modelMapper.map(shoppinglist, ShoppinglistDto.class);
    }

    /**

     This method converts a ShoppinglistDto object to a Shoppinglist object using the ModelMapper library.
     @param shoppinglistDto the ShoppinglistDto object to be converted to a Shoppinglist object
     @return a Shoppinglist object with properties mapped from the input ShoppinglistDto object
     */

    private Shoppinglist castDtoToShoppinglist(ShoppinglistDto shoppinglistDto) {
        modelMapper = new ModelMapper();
        return modelMapper.map(shoppinglistDto, Shoppinglist.class);
    }

    /**
     Adds a new shopping list to a given household.
     @param shoppinglistDto a ShoppinglistDto object containing the shopping list information to be added.
     @return a ShoppinglistDto object containing the information of the newly created shopping list.
     @throws BadRequestException if the name of the shopping list is empty, the household does not exist, the shopping list already exists for the household, or if the household already has a shopping list.
     */

    @Override
    public ShoppinglistDto addShoppinglist(ShoppinglistDto shoppinglistDto) {
        if (shoppinglistDto.getName() == null || shoppinglistDto.getName().trim().isEmpty()) {
            log.warn("[X] Shoppinglist name cannot be empty");
            throw new BadRequestException("Shoppinglist name cannot be empty");
        }
        Long householdId = shoppinglistDto.getHousehold().getHouseholdId();
        Household household = householdService.getHouseholdById(householdId)
                .orElseThrow(() -> new BadRequestException("Household with ID " + householdId + " not found"));
        Shoppinglist existingShoppinglist = shoppinglistRepository.findByNameAndHouseholdId(shoppinglistDto.getName(), householdId);
        if (existingShoppinglist != null) {
            log.warn("[X] Shoppinglist with name '{}' already exists for household ID {}", shoppinglistDto.getName(), householdId);
            throw new BadRequestException("Shoppinglist with name '" + shoppinglistDto.getName() + "' already exists for household ID " + householdId);
        }
        if (!shoppinglistRepository.findByHouseholdId(householdId).isEmpty()) {
            log.warn("[X] Household ID {} already has a shoppinglist", householdId);
            throw new BadRequestException("Household ID " + householdId + " already has a shoppinglist");
        }
        Shoppinglist shoppinglistToSave = castDtoToShoppinglist(shoppinglistDto);
        shoppinglistToSave.setHousehold(household);
        Shoppinglist savedShoppinglist = shoppinglistRepository.save(shoppinglistToSave);
        ShoppinglistDto savedShoppinglistDto = castShoppinglistToDto(savedShoppinglist);
        return savedShoppinglistDto;
    }

    /**

     Update a shopping list with the given ShoppinglistDto object.
     @param shoppinglistDto the ShoppinglistDto object to update the shopping list with
     @return the updated ShoppinglistDto object
     @throws BadRequestException if the ID or name of the shopping list is null or empty, or if the shopping list is not found
     */
    @Override
    public ShoppinglistDto updateShoppinglist(ShoppinglistDto shoppinglistDto) {
        if (shoppinglistDto.getShoppinglistID() == null || shoppinglistDto.getName() == null || shoppinglistDto.getName().trim().isEmpty()) {
            log.warn("[X] Shoppinglist ID and name cannot be empty");
            throw new BadRequestException("Shoppinglist ID and name cannot be empty");
        }
        Shoppinglist shoppinglistToUpdate = shoppinglistRepository.findById(shoppinglistDto.getShoppinglistID())
                .orElseThrow(() -> new BadRequestException("Shoppinglist with ID " + shoppinglistDto.getShoppinglistID() + " not found"));
        Long householdId = shoppinglistDto.getHousehold().getHouseholdId();
        Household household = householdService.getHouseholdById(householdId)
                .orElseThrow(() -> new BadRequestException("Household with ID " + householdId + " not found"));
        Shoppinglist existingShoppinglist = shoppinglistRepository.findByNameAndHouseholdIdExcludeShoppinglistId(shoppinglistDto.getName(), householdId, shoppinglistDto.getShoppinglistID());
        if (existingShoppinglist != null) {
            log.warn("[X] Shoppinglist with name '{}' already exists for household ID {}", shoppinglistDto.getName(), householdId);
            throw new BadRequestException("Shoppinglist with name '" + shoppinglistDto.getName() + "' already exists for household ID " + householdId);
        }
        shoppinglistToUpdate.setName(shoppinglistDto.getName());
        shoppinglistToUpdate.setHousehold(household);
        Shoppinglist updatedShoppinglist = shoppinglistRepository.save(shoppinglistToUpdate);

        ShoppinglistDto updatedShoppinglistDto = castShoppinglistToDto(updatedShoppinglist);

        return updatedShoppinglistDto;
    }
    /**

     Retrieves a ShoppinglistDto by its unique ID.
     @param shoppinglistId the ID of the shoppinglist to retrieve
     @return a ShoppinglistDto representing the shoppinglist with the given ID
     @throws NotFoundException if no shoppinglist is found with the given ID
     */
    @Override
    public ShoppinglistDto getShoppinglistById(Long shoppinglistId) {
        log.debug("Fetching Shoppinglist by id: {}", shoppinglistId);
        Shoppinglist shoppinglist = shoppinglistRepository.findById(shoppinglistId)
                .orElseThrow(() -> {
                    log.warn("Shoppinglist not found with id: {}", shoppinglistId);
                    return new NotFoundException("Shoppinglist with id " + shoppinglistId + " not found");
                });
        return castShoppinglistToDto(shoppinglist);
    }

    /**

     Deletes the Shoppinglist with the given ID. If the Shoppinglist is associated with a Household, the Household's shoppinglist will be set to null and updated in the database after the Shoppinglist is deleted.
     @param shoppinglistId the ID of the Shoppinglist to be deleted
     @throws NotFoundException if no Shoppinglist with the given ID is found in the database
     */
    @Override
    public void deleteShoppinglist(Long shoppinglistId) {
        Shoppinglist shoppinglist = shoppinglistRepository.findById(shoppinglistId)
                .orElseThrow(() -> {
                    log.warn("Shoppinglist not found with id: {}", shoppinglistId);
                    return new NotFoundException("Shoppinglist with id " + shoppinglistId + " not found for deletion");
                });
        Household household = shoppinglist.getHousehold();
        if (household != null) {
            household.setHouseholdId(null);
            shoppinglist.setHousehold(null);
            shoppinglistRepository.delete(shoppinglist); // Delete the shoppinglist row first
            householdRepository.save(household); // Update the household without the shoppinglist
        }
    }
}
