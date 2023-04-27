package edu.ntnu.idatt2106_09.backend.service.fridge;

import edu.ntnu.idatt2106_09.backend.dto.FridgeDto;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.BadRequestException;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.NotFoundException;
import edu.ntnu.idatt2106_09.backend.model.Fridge;
import edu.ntnu.idatt2106_09.backend.model.GroceryItem;
import edu.ntnu.idatt2106_09.backend.model.Household;
import edu.ntnu.idatt2106_09.backend.repository.FridgeRepository;
import edu.ntnu.idatt2106_09.backend.repository.HouseholdRepository;
import edu.ntnu.idatt2106_09.backend.service.household.HouseholdService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class FridgeServiceImplementation implements FridgeService {

    @Autowired
    private FridgeRepository fridgeRepository;

    @Autowired
    private HouseholdService householdService;

    @Autowired
    private HouseholdRepository householdRepository;

    private final ModelMapper modelMapper = new ModelMapper();

    private FridgeDto castFridgetoToDto(Fridge fridge) {
        return modelMapper.map(fridge, FridgeDto.class);
    }

    private Fridge castDtoToFridge(FridgeDto fridgeDto) {
        return modelMapper.map(fridgeDto, Fridge.class);
    }

    @Override
    public FridgeDto addFridge(FridgeDto fridgeDto) {
        // Check if the fridgeDto name is valid
        if (fridgeDto.getName() == null || fridgeDto.getName().trim().isEmpty()) {
            log.warn("[X] Fridge name cannot be empty");
            throw new BadRequestException("Fridge name cannot be empty");
        }

        // Check if the household ID exists in the database
        Long householdId = fridgeDto.getHousehold().getHouseholdId();
        Household household = householdService.getHouseholdById(householdId)
                .orElseThrow(() -> new BadRequestException("Household with ID " + householdId + " not found"));

        // Check if a fridge with the same name already exists for the household
        Fridge existingFridge = fridgeRepository.findByNameAndHouseholdId(fridgeDto.getName(), householdId);
        if (existingFridge != null) {
            log.warn("[X] Fridge with name '{}' already exists for household ID {}", fridgeDto.getName(), householdId);
            throw new BadRequestException("Fridge with name '" + fridgeDto.getName() + "' already exists for household ID " + householdId);
        }

        // Check if the household already has a fridge
        if (!fridgeRepository.findByHouseholdId(householdId).isEmpty()) {
            log.warn("[X] Household ID {} already has a fridge", householdId);
            throw new BadRequestException("Household ID " + householdId + " already has a fridge");
        }

        // Convert FridgeDto to Fridge
        Fridge fridgeToSave = castDtoToFridge(fridgeDto);

        // Set the associated household
        fridgeToSave.setHousehold(household);

        // Save the new Fridge to the database
        Fridge savedFridge = fridgeRepository.save(fridgeToSave);

        // Convert the saved Fridge to FridgeDto
        FridgeDto savedFridgeDto = castFridgetoToDto(savedFridge);

        return savedFridgeDto;
    }

    @Override
    public FridgeDto updateFridge(FridgeDto fridgeDto) {
        // Check if the fridgeDto ID and name are valid
        if (fridgeDto.getFridgeId() == null || fridgeDto.getName() == null || fridgeDto.getName().trim().isEmpty()) {
            log.warn("[X] Fridge ID and name cannot be empty");
            throw new BadRequestException("Fridge ID and name cannot be empty");
        }

        // Check if the Fridge exists in the database
        Fridge fridgeToUpdate = fridgeRepository.findById(fridgeDto.getFridgeId())
                .orElseThrow(() -> new BadRequestException("Fridge with ID " + fridgeDto.getFridgeId() + " not found"));

        // Check if the household ID exists in the database
        Long householdId = fridgeDto.getHousehold().getHouseholdId();
        Household household = householdService.getHouseholdById(householdId)
                .orElseThrow(() -> new BadRequestException("Household with ID " + householdId + " not found"));

        // Check if a fridge with the same name already exists for the household, excluding the current fridge
        Fridge existingFridge = fridgeRepository.findByNameAndHouseholdIdExcludeFridgeId(fridgeDto.getName(), householdId, fridgeDto.getFridgeId());
        if (existingFridge != null) {
            log.warn("[X] Fridge with name '{}' already exists for household ID {}", fridgeDto.getName(), householdId);
            throw new BadRequestException("Fridge with name '" + fridgeDto.getName() + "' already exists for household ID " + householdId);
        }

        // Update the Fridge properties
        fridgeToUpdate.setName(fridgeDto.getName());
        fridgeToUpdate.setHousehold(household);

        // Save the updated Fridge to the database
        Fridge updatedFridge = fridgeRepository.save(fridgeToUpdate);

        // Convert the updated Fridge to FridgeDto
        FridgeDto updatedFridgeDto = castFridgetoToDto(updatedFridge);

        return updatedFridgeDto;
    }

    @Override
    public FridgeDto getFridgeById(Long fridgeId) {
        log.debug("Fetching Fridge by id: {}", fridgeId);
        Fridge fridge = fridgeRepository.findById(fridgeId)
                .orElseThrow(() -> {
                        log.warn("Fridge not found with id: {}", fridgeId);
                        return new NotFoundException("Fridge with id " + fridgeId + " not found");
                });
        return castFridgetoToDto(fridge);
    }

    @Override
    public void deleteFridge(Long fridgeId) {
        Fridge fridge = fridgeRepository.findById(fridgeId)
                .orElseThrow(() -> {
                    log.warn("Fridge not found with id: {}", fridgeId);
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
