package com.travel.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {
    private boolean success;
    private String message;
    private BookingDTO data;
    private LocalDateTime timestamp;

    public BookingResponseDTO(boolean success, String message, BookingDTO data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }
}
