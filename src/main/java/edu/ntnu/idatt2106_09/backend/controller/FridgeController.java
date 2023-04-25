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

    @PostMapping("/add")
    public ResponseEntity<FridgeDto> addFridge(@RequestBody FridgeDto fridgeDto) {
        log.debug("[X] Adding new fridge");
        FridgeDto newFridge = fridgeService.addFridge(fridgeDto);
        log.info("[X] Fridge created with id: {}", newFridge.getFridgeId());
        return new ResponseEntity<>(newFridge, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<Set<Fridge>> getAllFridges() {
        log.debug("[X] Fetching all fridges");
        Set<Fridge> fridges = fridgeService.getAllFridges();
        log.debug("[X] Total number of fridges retrieved: {}", fridges.size());
        return new ResponseEntity<>(fridges, HttpStatus.OK);
    }

    // In FridgeController
    @DeleteMapping("/{fridgeId}")
    public ResponseEntity<Void> deleteFridge(@PathVariable Long fridgeId) {
        log.debug("[X] Deleting fridge with id: {}", fridgeId);
        Fridge fridge = fridgeService.getFridgeById(fridgeId)
                .orElseThrow(() -> new NotFoundException("Fridge with id " + fridgeId + " not found for deletion"));
        fridgeService.deleteFridge(fridgeId);
        log.info("[X] Fridge with id {} deleted", fridgeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    /**
    @DeleteMapping("/{fridgeId}")
    public ResponseEntity<Void> deleteFridge(@PathVariable Long fridgeId) {
        log.debug("[X] Deleting fridge with id: {}", fridgeId);
        Fridge fridge = fridgeService.getFridgeById(fridgeId)
                .orElseThrow(() -> new NotFoundException("Fridge with id " + fridgeId + " not found for deletion"));
        fridgeService.deleteFridge(fridgeId);
        log.info("[X] Fridge with id {} deleted", fridgeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    */
}