package com.travel.flight.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {

    @NotNull(message = "Number of seats is required")
    @Min(value = 1, message = "Must reserve at least 1 seat")
    private Integer numberOfSeats;
}
