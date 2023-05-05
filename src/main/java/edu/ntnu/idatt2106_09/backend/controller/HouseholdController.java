package edu.ntnu.idatt2106_09.backend.controller;

import edu.ntnu.idatt2106_09.backend.dto.HouseholdDto;
import edu.ntnu.idatt2106_09.backend.dto.HouseholdDtoForHouseholdService;
import edu.ntnu.idatt2106_09.backend.dto.UserDto;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.NotFoundException;
import edu.ntnu.idatt2106_09.backend.model.Household;
import edu.ntnu.idatt2106_09.backend.service.household.HouseholdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/household")
public class HouseholdController {

    private final HouseholdService householdService;

    public HouseholdController(HouseholdService householdService) {
        this.householdService = householdService;
    }

    @GetMapping("/{householdId}")
    public Optional<HouseholdDtoForHouseholdService> getHouseholdById(@PathVariable("householdId") Long householdId) {
        log.debug("[X] Call to return a household by id");
        return householdService.getHouseholdByIdAsDto(householdId);
    }

    @GetMapping("/byUser/{userId}")
    public ResponseEntity<HouseholdDtoForHouseholdService> getHouseholdByUserId(@PathVariable("userId") Integer userId) {
        log.debug("[X] Call to return a household by a users id");
        return householdService.getHouseholdByUserId(userId);
    }

    @PostMapping("/addNewUser/{userId}/{invitationNr}")
    public ResponseEntity<UserDto> addUserToHousehold(@PathVariable("userId") Integer userId, @PathVariable("invitationNr") Long invitationNr) {
        log.debug("[X] Call add a new user to a household");
        return householdService.addUserToHousehold(invitationNr, userId);
    }


    @PostMapping("/create/{userId}")
    public ResponseEntity<HouseholdDtoForHouseholdService> createHousehold(@PathVariable("userId") Integer userId, @RequestBody HouseholdDtoForHouseholdService householdDto) {
        log.debug("[X] Call to create a new household");
        return householdService.createHousehold(userId, householdDto);
    }


    @GetMapping("/users/{householdId}")
    public Set<UserDto> getAllUsersInHousehold(@PathVariable("householdId") Long householdId) {
        log.debug("[X] Call to return all users in a household");
        return householdService.getAllUsersInHousehold(householdId);
    }

    @PutMapping("/update/{householdId}")
    public ResponseEntity<HouseholdDtoForHouseholdService> updateHousehold(@PathVariable("householdId") Long householdId, @RequestBody HouseholdDtoForHouseholdService householdDto) {
        log.debug("[X] Call to update the household info");
        return householdService.updateHousehold(householdId, householdDto);
    }

}
