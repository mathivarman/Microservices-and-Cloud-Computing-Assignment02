package com.travel.hotel.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomReservationDTO {

    @NotNull(message = "Number of rooms is required")
    @Min(value = 1, message = "Must reserve at least 1 room")
    private Integer numberOfRooms;
}
