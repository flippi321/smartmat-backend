package edu.ntnu.idatt2106_09.backend.controller;
        import edu.ntnu.idatt2106_09.backend.dto.ShoppinglistDto;
        import edu.ntnu.idatt2106_09.backend.service.shoppinglist.ShoppinglistService;
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
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/shoppinglists")
@Tag(name = "Shoppinglist Management", description = "Controller class to manage endpoints for managing shoppinglist objects")
public class ShoppinglistController {

    @Autowired
    private ShoppinglistService shoppinglistService;

    @PostMapping("/add")
    @Operation(summary = "Add a new shoppinglist", description = "Create a new shoppinglist and return the created shoppinglist's details")
    @ApiResponse(responseCode = "201", description = "Shoppinglist created successfully", content = @Content(schema = @Schema(implementation = ShoppinglistDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input provided")
    public ResponseEntity<ShoppinglistDto> addShoppinglist(@RequestBody ShoppinglistDto shoppinglistDto) {
        log.debug("[X] Adding new shoppinglist");
        ShoppinglistDto newShoppinglist = shoppinglistService.addShoppinglist(shoppinglistDto);
        log.info("[X] Shoppinglist created with id: {}", newShoppinglist.getShoppinglistID());
        return new ResponseEntity<>(newShoppinglist, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    @Operation(summary = "Update an existing shoppinglist", description = "Update the details of a shoppinglist with the provided shoppinglist ID and return the updated shoppinglist's details")
    @ApiResponse(responseCode = "200", description = "Shoppinglist updated successfully", content = @Content(schema = @Schema(implementation = ShoppinglistDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input provided")
    @ApiResponse(responseCode = "404", description = "Shoppinglist not found")
    public ResponseEntity<ShoppinglistDto> updateShoppinglist(@RequestBody ShoppinglistDto shoppinglistDto) {
        log.debug("[X] Updating shoppinglist with id: {}", shoppinglistDto.getShoppinglistID());
        ShoppinglistDto updatedShoppinglist = shoppinglistService.updateShoppinglist(shoppinglistDto);
        log.info("[X] Shoppinglist with id: {} updated", updatedShoppinglist.getShoppinglistID());
        return new ResponseEntity<>(updatedShoppinglist, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get shoppinglist by ID", description = "Return the details of a shoppinglist with the provided shoppinglist ID")
    @ApiResponse(responseCode = "200", description = "Shoppinglist found", content = @Content(schema = @Schema(implementation = ShoppinglistDto.class)))
    @ApiResponse(responseCode = "404", description = "Shoppinglist not found")

    public ResponseEntity<ShoppinglistDto> getShoppinglistById(@PathVariable Long id) {
        log.debug("[X] Received request to get Shoppinglist by id: {}", id);
        ShoppinglistDto shoppinglistDto = shoppinglistService.getShoppinglistById(id);
        log.info("Shoppinglist found with id: {} and details: {}", id, shoppinglistDto);
        return ResponseEntity.ok(shoppinglistDto);
    }

    @DeleteMapping("/{shoppinglistId}")
    @Operation(summary = "Delete a shoppinglist", description = "Delete a shoppinglist with the provided shoppinglist ID")
    @ApiResponse(responseCode = "204", description = "Shoppinglist deleted successfully")
    @ApiResponse(responseCode = "404", description = "Shoppinglist not found")
    public ResponseEntity<Void> deleteShoppinglist(@PathVariable Long shoppinglistId) {
        log.debug("[X] Deleting shoppinglist with id: {}", shoppinglistId);
        shoppinglistService.deleteShoppinglist(shoppinglistId);
        log.info("[X] Shoppinglist with id {} deleted", shoppinglistId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
