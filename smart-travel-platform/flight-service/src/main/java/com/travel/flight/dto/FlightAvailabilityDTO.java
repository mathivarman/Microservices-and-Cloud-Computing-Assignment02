package com.travel.flight.dto;

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

    public FlightAvailabilityDTO(Long flightId, String flightNumber, boolean available, 
                                  Integer availableSeats, Double pricePerSeat) {
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.available = available;
        this.availableSeats = availableSeats;
        this.pricePerSeat = pricePerSeat;
        this.message = available ? "Flight available" : "No seats available";
    }
}
