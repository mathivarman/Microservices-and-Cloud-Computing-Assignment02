package com.travel.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightAvailabilityDTO {
    private Long flightId;
    private String flightNumber;
    private boolean available;
    private Integer availableSeats;
    private Double pricePerSeat;
    private String message;
}
