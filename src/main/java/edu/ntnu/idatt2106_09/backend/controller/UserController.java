package edu.ntnu.idatt2106_09.backend.controller;

import edu.ntnu.idatt2106_09.backend.dto.CategoryDto;
import edu.ntnu.idatt2106_09.backend.dto.UserDto;
import edu.ntnu.idatt2106_09.backend.service.category.CategoryService;
import edu.ntnu.idatt2106_09.backend.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "User management operations")
public class UserController {

    @Autowired
    private UserService userService;

    @PutMapping("/update/{oldEmail}")
    @Operation(summary = "Update user", description = "Update an existing user")
    public ResponseEntity<UserDto> updateUSer(@PathVariable("oldEmail") String oldEmail, @RequestBody UserDto userDto) {
        log.debug("[X] Call to update a user");
        return userService.updateUser(oldEmail, userDto);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Fetch a specific user by its ID")
    public ResponseEntity<UserDto> getUserById(@PathVariable("userId") Integer userId) {
        log.debug("[X] Call to return a user by id" );
        return userService.getUserById(userId);
    }
}
