package edu.ntnu.idatt2106_09.backend.controller;

import edu.ntnu.idatt2106_09.backend.dto.HouseholdDto;
import edu.ntnu.idatt2106_09.backend.dto.HouseholdDtoForHouseholdService;
import edu.ntnu.idatt2106_09.backend.dto.UserDto;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.NotFoundException;
import edu.ntnu.idatt2106_09.backend.model.Household;
import edu.ntnu.idatt2106_09.backend.service.household.HouseholdService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/household")
@Tag(name = "Household Management", description = "Controller class to manage endpoints for managing household objects")
public class HouseholdController {

    private final HouseholdService householdService;

    public HouseholdController(HouseholdService householdService) {
        this.householdService = householdService;
    }

    @GetMapping("/{householdId}")
    @Operation(summary = "Get a household by id", description = "Returns a household by it id")
    public Optional<HouseholdDtoForHouseholdService> getHouseholdById(@PathVariable("householdId") Long householdId) {
        log.debug("[X] Call to return a household by id");
        return householdService.getHouseholdByIdAsDto(householdId);
    }

    @GetMapping("/byUser/{userId}")
    @Operation(summary = "Get a household by a users id", description = "Returns a household by a users id")
    public ResponseEntity<HouseholdDtoForHouseholdService> getHouseholdByUserId(@PathVariable("userId") Integer userId) {
        log.debug("[X] Call to return a household by a users id");
        return householdService.getHouseholdByUserId(userId);
    }

    @PostMapping("/addNewUser/{userId}/{invitationNr}")
    @Operation(summary = "Add a new user to a household", description = "Adds a new user to a household with its invitation number")
    public ResponseEntity<UserDto> addUserToHousehold(@PathVariable("userId") Integer userId, @PathVariable("invitationNr") Long invitationNr) {
        log.debug("[X] Call add a new user to a household");
        return householdService.addUserToHousehold(invitationNr, userId);
    }


    @PostMapping("/create/{userId}")
    @Operation(summary = "Creates a new household", description = "Creates a new household and automatically allocates the logged inn user to the new household")
    public ResponseEntity<HouseholdDtoForHouseholdService> createHousehold(@PathVariable("userId") Integer userId, @RequestBody HouseholdDtoForHouseholdService householdDto) {
        log.debug("[X] Call to create a new household");
        return householdService.createHousehold(userId, householdDto);
    }


    @GetMapping("/users/{householdId}")
    @Operation(summary = "Get all users in a household", description = "Returns all the users in a household")
    public Set<UserDto> getAllUsersInHousehold(@PathVariable("householdId") Long householdId) {
        log.debug("[X] Call to return all users in a household");
        return householdService.getAllUsersInHousehold(householdId);
    }

    @PutMapping("/update/{householdId}")
    @Operation(summary = "Updates a households information", description = "Updates the name of the household, as well a the names of the fridge and shoppinglist if in the household")
    public ResponseEntity<HouseholdDtoForHouseholdService> updateHousehold(@PathVariable("householdId") Long householdId, @RequestBody HouseholdDtoForHouseholdService householdDto) {
        log.debug("[X] Call to update the household info");
        return householdService.updateHousehold(householdId, householdDto);
    }

}
