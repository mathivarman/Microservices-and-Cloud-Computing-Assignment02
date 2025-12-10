package com.travel.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelResponseDTO {
    private boolean success;
    private String message;
    private HotelDTO data;
    private LocalDateTime timestamp;

    public HotelResponseDTO(boolean success, String message, HotelDTO data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }
}
