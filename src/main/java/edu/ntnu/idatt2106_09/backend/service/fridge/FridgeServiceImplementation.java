package edu.ntnu.idatt2106_09.backend.service.fridge;

import edu.ntnu.idatt2106_09.backend.dto.FridgeDto;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.BadRequestException;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.NotFoundException;
import edu.ntnu.idatt2106_09.backend.model.Fridge;
import edu.ntnu.idatt2106_09.backend.model.Household;
import edu.ntnu.idatt2106_09.backend.repository.FridgeRepository;
import edu.ntnu.idatt2106_09.backend.repository.HouseholdRepository;
import edu.ntnu.idatt2106_09.backend.service.household.HouseholdService;
import edu.ntnu.idatt2106_09.backend.service.household.HouseholdServiceImplementation;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class is an implementation of the FridgeService interface, providing methods for managing fridges in the system.
 * It handles the business logic for operations related to fridges, including adding, updating, retrieving, and deleting
 * fridges. It uses the FridgeRepository and HouseholdRepository to interact with the database and apply the necessary
 * changes.
 */
@Slf4j
@Service
public class FridgeServiceImplementation implements FridgeService {

    @Autowired
    private FridgeRepository fridgeRepository;

    @Autowired
    private HouseholdServiceImplementation householdService;

    @Autowired
    private HouseholdRepository householdRepository;

    @Autowired
    private ModelMapper modelMapper = new ModelMapper();

    public FridgeServiceImplementation(FridgeRepository fridgeRepository,
                                       HouseholdServiceImplementation householdService, HouseholdRepository householdRepository) {
        this.fridgeRepository = fridgeRepository;
        this.householdService = householdService;
        this.householdRepository = householdRepository;
    }

    /**
     * Converts a Fridge object to a FridgeDto object using ModelMapper. This method
     * simplifies the process of converting between the domain model and the data
     * transfer object.
     *
     * @param fridge The Fridge object to be converted.
     * @return A FridgeDto object with the same properties as the given Fridge object.
     */
    private FridgeDto castFridgetoToDto(Fridge fridge) {
        return modelMapper.map(fridge, FridgeDto.class);
    }

    /**
     * Converts a FridgeDto object to a Fridge object using ModelMapper. This method
     * simplifies the process of converting between the data transfer object and the
     * domain model.
     *
     * @param fridgeDto The FridgeDto object to be converted.
     * @return A Fridge object with the same properties as the given FridgeDto object.
     */
    private Fridge castDtoToFridge(FridgeDto fridgeDto) {
        return modelMapper.map(fridgeDto, Fridge.class);
    }

    /**
     * Adds a new Fridge to the database. This method validates the input FridgeDto, converts it to a Fridge object,
     * associates it with the specified household, and persists it to the database.
     *
     * @param fridgeDto A FridgeDto object representing the new Fridge to be added.
     * @return A FridgeDto object representing the new Fridge that was added to the database.
     * @throws BadRequestException if the FridgeDto name is empty or null, if the household ID does not exist in the database,
     *         if a Fridge with the same name already exists for the household, or if the household already has a fridge.
     */
    @Override
    public FridgeDto addFridge(FridgeDto fridgeDto) {
        if (fridgeDto.getName() == null || fridgeDto.getName().trim().isEmpty()) {
            log.warn("[X] Fridge name cannot be empty");
            throw new BadRequestException("Fridge name cannot be empty");
        }
        Long householdId = fridgeDto.getHousehold().getHouseholdId();
        Household household = householdService.getHouseholdById(householdId)
                .orElseThrow(() -> new BadRequestException("Household with ID " + householdId + " not found"));
        Fridge existingFridge = fridgeRepository.findByNameAndHouseholdId(fridgeDto.getName(), householdId);
        if (existingFridge != null) {
            log.warn("[X] Fridge with name '{}' already exists for household ID {}", fridgeDto.getName(), householdId);
            throw new BadRequestException("Fridge with name '" + fridgeDto.getName() + "' already exists for household ID " + householdId);
        }
        if (!fridgeRepository.findByHouseholdId(householdId).isEmpty()) {
            log.warn("[X] Household ID {} already has a fridge", householdId);
            throw new BadRequestException("Household ID " + householdId + " already has a fridge");
        }
        Fridge fridgeToSave = castDtoToFridge(fridgeDto);
        fridgeToSave.setHousehold(household);
        Fridge savedFridge = fridgeRepository.save(fridgeToSave);
        FridgeDto savedFridgeDto = castFridgetoToDto(savedFridge);
        return savedFridgeDto;
    }

    /**
     * Updates an existing Fridge in the database. This method validates the input FridgeDto, retrieves the existing
     * Fridge from the database, updates its properties, and saves the changes back to the database.
     *
     * @param fridgeDto A FridgeDto object representing the Fridge to be updated.
     * @return A FridgeDto object representing the updated Fridge in the database.
     * @throws BadRequestException if the FridgeDto ID or name is empty or null, if the Fridge does not exist in the
     *         database, if the household ID does not exist in the database, or if a Fridge with the same name already
     *         exists for the household.
     */
    @Override
    public FridgeDto updateFridge(FridgeDto fridgeDto) {
        if (fridgeDto.getFridgeId() == null || fridgeDto.getName() == null || fridgeDto.getName().trim().isEmpty()) {
            log.warn("[X] Fridge ID and name cannot be empty");
            throw new BadRequestException("Fridge ID and name cannot be empty");
        }
        Fridge fridgeToUpdate = fridgeRepository.findById(fridgeDto.getFridgeId())
                .orElseThrow(() -> new BadRequestException("Fridge with ID " + fridgeDto.getFridgeId() + " not found"));
        Long householdId = fridgeDto.getHousehold().getHouseholdId();
        Household household = householdService.getHouseholdById(householdId)
                .orElseThrow(() -> new BadRequestException("Household with ID " + householdId + " not found"));
        Fridge existingFridge = fridgeRepository.findByNameAndHouseholdIdExcludeFridgeId(fridgeDto.getName(), householdId, fridgeDto.getFridgeId());
        if (existingFridge != null) {
            log.warn("[X] Fridge with name '{}' already exists for household ID {}", fridgeDto.getName(), householdId);
            throw new BadRequestException("Fridge with name '" + fridgeDto.getName() + "' already exists for household ID " + householdId);
        }
        fridgeToUpdate.setName(fridgeDto.getName());
        fridgeToUpdate.setHousehold(household);
        Fridge updatedFridge = fridgeRepository.save(fridgeToUpdate);
        FridgeDto updatedFridgeDto = castFridgetoToDto(updatedFridge);
        return updatedFridgeDto;
    }

    /**
     * Retrieves a Fridge from the database by its ID.
     *
     * @param fridgeId A Long representing the ID of the Fridge to retrieve.
     * @return A FridgeDto object representing the retrieved Fridge.
     * @throws NotFoundException if the Fridge with the given ID does not exist in the database.
     */
    @Override
    public FridgeDto getFridgeById(Long fridgeId) {
        log.debug("[X] Fetching Fridge by id: {}", fridgeId);
        Fridge fridge = fridgeRepository.findById(fridgeId)
                .orElseThrow(() -> {
                        log.warn("[X] Fridge not found with id: {}", fridgeId);
                        return new NotFoundException("Fridge with id " + fridgeId + " not found");
                });
        return castFridgetoToDto(fridge);
    }

    /**
     * Deletes a Fridge from the database by its ID.
     *
     * @param fridgeId A Long representing the ID of the Fridge to delete.
     * @throws NotFoundException if the Fridge with the given ID does not exist in the database.
     */
    @Override
    public void deleteFridge(Long fridgeId) {
        Fridge fridge = fridgeRepository.findById(fridgeId)
                .orElseThrow(() -> {
                    log.warn("[X] Fridge not found with id: {}", fridgeId);
                    return new NotFoundException("Fridge with id " + fridgeId + " not found for deletion");
                });
        Household household = fridge.getHousehold();
        if (household != null) {
            household.setFridge(null);
            fridge.setHousehold(null);
            fridgeRepository.delete(fridge); // Delete the fridge row first
            householdRepository.save(household); // Update the household without the fridge
        }
    }
}
