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

@Slf4j
@Service
public class FridgeServiceImplementation implements FridgeService {

    @Autowired
    private FridgeRepository fridgeRepository;

    @Autowired
    private HouseholdServiceImplementation householdService;

    @Autowired
    private HouseholdRepository householdRepository;

    private final ModelMapper modelMapper = new ModelMapper();

    public FridgeServiceImplementation(FridgeRepository fridgeRepository,
                                       HouseholdServiceImplementation householdService, HouseholdRepository householdRepository) {
        this.fridgeRepository = fridgeRepository;
        this.householdService = householdService;
        this.householdRepository = householdRepository;
    }
    private FridgeDto castFridgetoToDto(Fridge fridge) {
        return modelMapper.map(fridge, FridgeDto.class);
    }

    private Fridge castDtoToFridge(FridgeDto fridgeDto) {
        return modelMapper.map(fridgeDto, Fridge.class);
    }
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
