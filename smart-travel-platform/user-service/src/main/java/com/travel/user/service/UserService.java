package com.travel.user.service;

import com.travel.user.dto.UserDTO;
import com.travel.user.dto.UserRequestDTO;
import com.travel.user.entity.User;
import com.travel.user.exception.DuplicateEmailException;
import com.travel.user.exception.UserNotFoundException;
import com.travel.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for User operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    /**
     * Create a new user
     * @param requestDTO User creation request
     * @return Created user DTO
     */
    @Transactional
    public UserDTO createUser(UserRequestDTO requestDTO) {
        log.info("Creating user with email: {}", requestDTO.getEmail());
        
        // Check if user already exists
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateEmailException(requestDTO.getEmail());
        }

        User user = new User();
        user.setName(requestDTO.getName());
        user.setEmail(requestDTO.getEmail());
        user.setPhone(requestDTO.getPhone());

        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());
        
        return convertToDTO(savedUser);
    }

    /**
     * Get user by ID
     * @param userId User ID
     * @return User DTO
     */
    public UserDTO getUserById(Long userId) {
        log.info("Fetching user with id: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        
        return convertToDTO(user);
    }

    /**
     * Validate if user exists
     * @param userId User ID
     * @return User DTO if exists
     */
    public UserDTO validateUser(Long userId) {
        log.info("Validating user with id: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        
        log.info("User validation successful for id: {}", userId);
        return convertToDTO(user);
    }

    /**
     * Get all users
     * @return List of user DTOs
     */
    public List<UserDTO> getAllUsers() {
        log.info("Fetching all users");
        
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert User entity to DTO
     * @param user User entity
     * @return User DTO
     */
    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getCreatedAt()
        );
    }
}
