package com.travel.flight.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightResponseDTO {

    private boolean success;
    private String message;
    private FlightDTO data;
    private LocalDateTime timestamp;

    public FlightResponseDTO(boolean success, String message, FlightDTO data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }
}
