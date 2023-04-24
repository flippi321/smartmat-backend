package edu.ntnu.idatt2106_09.backend.controller;

import edu.ntnu.idatt2106_09.backend.dto.FridgeDto;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.BadRequestException;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.NotFoundException;
import edu.ntnu.idatt2106_09.backend.model.Fridge;
import edu.ntnu.idatt2106_09.backend.service.fridge.FridgeService;
import edu.ntnu.idatt2106_09.backend.service.household.HouseholdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/fridges")
public class FridgeController {

    @Autowired
    private FridgeService fridgeService;

    @Autowired
    private HouseholdService householdService;

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleNotFoundException(BadRequestException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/add")
    public ResponseEntity<FridgeDto> addFridge(@RequestBody FridgeDto fridgeDto) {
        log.debug("[X] Adding new fridgeDto");

        // Check if the fridgeDto name is valid
        if (fridgeDto.getName() == null || fridgeDto.getName().trim().isEmpty()) {
            log.warn("[X] Fridge name cannot be empty");
            throw new BadRequestException("Fridge name cannot be empty");
        }

        // Check if the household ID exists in the database
        Long householdId = fridgeDto.getHousehold().getHouseholdId();
        householdService.getHouseholdById(householdId)
                .orElseThrow(() -> new BadRequestException("Household with ID " + householdId + " not found"));

        FridgeDto newFridgeDto = fridgeService.addFridge(fridgeDto);
        log.info("[X] Fridge created with id: {}", newFridgeDto.getFridgeId());
        return new ResponseEntity<>(newFridgeDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Set<Fridge>> getAllFridges() {
        log.debug("[X] Fetching all fridges");
        Set<Fridge> fridges = fridgeService.getAllFridges();
        log.debug("[X] Total number of fridges retrieved: {}", fridges.size());
        return new ResponseEntity<>(fridges, HttpStatus.OK);
    }

    // TODO Vurder dette alternativet
    @DeleteMapping("/{fridgeIdTwo}")
    public ResponseEntity<Void> deleteFridgeTwo(@PathVariable Long fridgeId) {
        log.debug("[X] Deleting fridge with id: {}", fridgeId);
        Fridge fridge = fridgeService.getFridgeById(fridgeId)
                .orElseThrow(() -> new NotFoundException("Fridge with id " + fridgeId + " not found for deletion"));
        fridgeService.deleteFridge(fridgeId);
        log.debug("[X] Fridge with id {} deleted", fridgeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{fridgeId}")
    public ResponseEntity<Void> deleteFridge(@PathVariable Long fridgeId) {
        log.debug("[X] Deleting fridge with id: {}", fridgeId);
        fridgeService.deleteFridge(fridgeId);
        log.debug("[X] Fridge with id {} deleted", fridgeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); //TODO Exception handling?
    }
}