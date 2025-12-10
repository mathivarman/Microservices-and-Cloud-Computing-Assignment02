package com.travel.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard API response wrapper
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private boolean success;
    private String message;
    private UserDTO data;
    private LocalDateTime timestamp;

    public UserResponseDTO(boolean success, String message, UserDTO data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }
}
