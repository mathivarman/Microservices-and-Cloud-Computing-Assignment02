package com.travel.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Flight ID is required")
    private Long flightId;

    @NotNull(message = "Hotel ID is required")
    private Long hotelId;

    @NotNull(message = "Travel date is required")
    @Future(message = "Travel date must be in the future")
    private LocalDate travelDate;

    @NotNull(message = "Number of nights is required")
    @Min(value = 1, message = "Must book at least 1 night")
    private Integer numberOfNights;

    @NotNull(message = "Number of passengers is required")
    @Min(value = 1, message = "Must have at least 1 passenger")
    private Integer numberOfPassengers;
}
