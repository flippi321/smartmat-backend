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

    public ShoppinglistServiceImplementation(ShoppinglistRepository shoppinglistRepository,
                                       HouseholdServiceImplementation householdService, HouseholdRepository householdRepository) {
        this.shoppinglistRepository = shoppinglistRepository;
        this.householdService = householdService;
        this.householdRepository = householdRepository;
    }

    private ShoppinglistDto castShoppinglistToDto(Shoppinglist shoppinglist) {
        modelMapper = new ModelMapper();
        return modelMapper.map(shoppinglist, ShoppinglistDto.class);
    }

    private Shoppinglist castDtoToShoppinglist(ShoppinglistDto shoppinglistDto) {
        modelMapper = new ModelMapper();
        return modelMapper.map(shoppinglistDto, Shoppinglist.class);
    }

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
