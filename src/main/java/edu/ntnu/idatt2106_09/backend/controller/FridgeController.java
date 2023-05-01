package edu.ntnu.idatt2106_09.backend.controller;

import edu.ntnu.idatt2106_09.backend.dto.FridgeDto;
import edu.ntnu.idatt2106_09.backend.exceptionHandling.NotFoundException;
import edu.ntnu.idatt2106_09.backend.model.Fridge;
import edu.ntnu.idatt2106_09.backend.service.fridge.FridgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Fridge Management", description = "Controller class to manage endpoints for managing fridge objects")
public class FridgeController {

    @Autowired
    private FridgeService fridgeService;

    @PostMapping("/add")
    @Operation(summary = "Add a new fridge", description = "Create a new fridge and return the created fridge's details")
    @ApiResponse(responseCode = "201", description = "Fridge created successfully", content = @Content(schema = @Schema(implementation = FridgeDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input provided")
    public ResponseEntity<FridgeDto> addFridge(@RequestBody FridgeDto fridgeDto) {
        log.debug("[X] Adding new fridge");
        FridgeDto newFridge = fridgeService.addFridge(fridgeDto);
        log.info("[X] Fridge created with id: {}", newFridge.getFridgeId());
        return new ResponseEntity<>(newFridge, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    @Operation(summary = "Update an existing fridge", description = "Update the details of a fridge with the provided fridge ID and return the updated fridge's details")
    @ApiResponse(responseCode = "200", description = "Fridge updated successfully", content = @Content(schema = @Schema(implementation = FridgeDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input provided")
    @ApiResponse(responseCode = "404", description = "Fridge not found")
    public ResponseEntity<FridgeDto> updateFridge(@RequestBody FridgeDto fridgeDto) {
        log.debug("[X] Updating fridge with id: {}", fridgeDto.getFridgeId());
        FridgeDto updatedFridge = fridgeService.updateFridge(fridgeDto);
        log.info("[X] Fridge with id: {} updated", updatedFridge.getFridgeId());
        return new ResponseEntity<>(updatedFridge, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get fridge by ID", description = "Return the details of a fridge with the provided fridge ID")
    @ApiResponse(responseCode = "200", description = "Fridge found", content = @Content(schema = @Schema(implementation = FridgeDto.class)))
    @ApiResponse(responseCode = "404", description = "Fridge not found")

    public ResponseEntity<FridgeDto> getFridgeById(@PathVariable Long id) {
        log.debug("[X] Received request to get Fridge by id: {}", id);
        FridgeDto fridgeDto = fridgeService.getFridgeById(id);
        log.info("Fridge found with id: {} and details: {}", id, fridgeDto);
        return ResponseEntity.ok(fridgeDto);
    }

    @DeleteMapping("/{fridgeId}")
    @Operation(summary = "Delete a fridge", description = "Delete a fridge with the provided fridge ID")
    @ApiResponse(responseCode = "204", description = "Fridge deleted successfully")
    @ApiResponse(responseCode = "404", description = "Fridge not found")
    public ResponseEntity<Void> deleteFridge(@PathVariable Long fridgeId) {
        log.debug("[X] Deleting fridge with id: {}", fridgeId);
        fridgeService.deleteFridge(fridgeId);
        log.info("[X] Fridge with id {} deleted", fridgeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}