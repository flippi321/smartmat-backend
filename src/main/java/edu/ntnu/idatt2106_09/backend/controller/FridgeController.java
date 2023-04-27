package edu.ntnu.idatt2106_09.backend.controller;

import edu.ntnu.idatt2106_09.backend.dto.FridgeDto;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.NotFoundException;
import edu.ntnu.idatt2106_09.backend.model.Fridge;
import edu.ntnu.idatt2106_09.backend.service.fridge.FridgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
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

    @PutMapping("/update")
    public ResponseEntity<FridgeDto> updateFridge(@RequestBody FridgeDto fridgeDto) {
        log.debug("[X] Updating fridge with id: {}", fridgeDto.getFridgeId());
        FridgeDto updatedFridge = fridgeService.updateFridge(fridgeDto);
        log.info("[X] Fridge with id: {} updated", updatedFridge.getFridgeId());
        return new ResponseEntity<>(updatedFridge, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FridgeDto> getFridgeById(@PathVariable Long id) {
        log.debug("[X] Received request to get Fridge by id: {}", id);
        FridgeDto fridgeDto = fridgeService.getFridgeById(id);
        log.info("Fridge found with id: {} and details: {}", id, fridgeDto);
        return ResponseEntity.ok(fridgeDto);
    }

    @DeleteMapping("/{fridgeId}")
    public ResponseEntity<Void> deleteFridge(@PathVariable Long fridgeId) {
        log.debug("[X] Deleting fridge with id: {}", fridgeId);
        fridgeService.deleteFridge(fridgeId);
        log.info("[X] Fridge with id {} deleted", fridgeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}