package com.travel.user.controller;

import com.travel.user.dto.UserDTO;
import com.travel.user.dto.UserRequestDTO;
import com.travel.user.dto.UserResponseDTO;
import com.travel.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for User Service
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Service", description = "User management and validation APIs")
public class UserController {

    private final UserService userService;

    /**
     * Create a new user
     * 
     * @param requestDTO User creation request
     * @return User response with created user data
     */
    @PostMapping
    @Operation(summary = "Create new user", description = "Creates a new user in the system")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO requestDTO) {
        log.info("POST /api/users - Creating new user");

        UserDTO userDTO = userService.createUser(requestDTO);
        UserResponseDTO response = new UserResponseDTO(
                true,
                "User created successfully",
                userDTO);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get user by ID
     * 
     * @param id User ID
     * @return User response with user data
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves user details by ID")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        log.info("GET /api/users/{} - Fetching user", id);

        UserDTO userDTO = userService.getUserById(id);
        UserResponseDTO response = new UserResponseDTO(
                true,
                "User retrieved successfully",
                userDTO);

        return ResponseEntity.ok(response);
    }

    /**
     * Validate if user exists
     * 
     * @param id User ID
     * @return User response if user exists
     */
    @GetMapping("/validate/{id}")
    @Operation(summary = "Validate user", description = "Validates if a user exists in the system")
    public ResponseEntity<UserResponseDTO> validateUser(@PathVariable Long id) {
        log.info("GET /api/users/validate/{} - Validating user", id);

        UserDTO userDTO = userService.validateUser(id);
        UserResponseDTO response = new UserResponseDTO(
                true,
                "User validation successful",
                userDTO);

        return ResponseEntity.ok(response);
    }

    /**
     * Get all users
     * 
     * @return List of all users
     */
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves all users in the system")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        log.info("GET /api/users - Fetching all users");

        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
