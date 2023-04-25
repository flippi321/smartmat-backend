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
    public Optional<Fridge> getFridgeById(Long fridgeId) {
        return fridgeRepository.findById(fridgeId);
    }

    @Override
    public Set<Fridge> getAllFridges() {
        return fridgeRepository.getAllFridges();
    }

    @Override
    public Fridge updateFridge(Fridge fridge) {
        return fridgeRepository.save(fridge);
    }

    /**
    public void removeFridgeFromHousehold(Long fridgeId) {
        Fridge fridge = fridgeRepository.findById(fridgeId)
                .orElseThrow(() -> new NotFoundException("Fridge with id " + fridgeId + " not found for deletion"));
        Household household = fridge.getHousehold();
        if (household != null) {
            household.setFridge(null);
            fridge.setHousehold(null);
            householdRepository.save(household);
            fridgeRepository.save(fridge);
        }
    }
     */

    /**
    // In FridgeService
    public void removeFridgeFromHousehold(Long fridgeId) {
        Fridge fridge = fridgeRepository.findById(fridgeId)
                .orElseThrow(() -> new NotFoundException("Fridge with id " + fridgeId + " not found for deletion"));
        Household household = fridge.getHousehold();
        if (household != null) {
            household.setFridge(null);
            fridge.setHousehold(null);
            householdRepository.save(household);
            fridgeRepository.save(fridge);
        }
    }

    @Override
    public void deleteFridge(Long fridgeId) {
        removeFridgeFromHousehold(fridgeId);
        fridgeRepository.deleteById(fridgeId);
    }*/

    public void deleteFridge(Long fridgeId) {
        Fridge fridge = fridgeRepository.findById(fridgeId)
                .orElseThrow(() -> new NotFoundException("Fridge with id " + fridgeId + " not found for deletion"));
        Household household = fridge.getHousehold();
        if (household != null) {
            household.setFridge(null);
            fridge.setHousehold(null);
            fridgeRepository.delete(fridge); // Delete the fridge row first
            householdRepository.save(household); // Update the household without the fridge
        }
    }



    @Override
    public Fridge addGroceryItemToFridge(Long fridgeId, GroceryItem groceryItem) {
        Optional<Fridge> fridgeOptional = fridgeRepository.findById(fridgeId);
        if (fridgeOptional.isPresent()) {
            Fridge fridge = fridgeOptional.get();
            fridge.addGroceryItem(groceryItem);
            return fridgeRepository.save(fridge);
        }
        return null;
    }

    @Override
    public Fridge removeGroceryItemFromFridge(Long fridgeId, GroceryItem groceryItem) {
        Optional<Fridge> fridgeOptional = fridgeRepository.findById(fridgeId);
        if (fridgeOptional.isPresent()) {
            Fridge fridge = fridgeOptional.get();
            fridge.removeGroceryItem(groceryItem);
            return fridgeRepository.save(fridge);
        }
        return null;
    }
}