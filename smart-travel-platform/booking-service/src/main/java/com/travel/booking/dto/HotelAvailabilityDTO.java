package com.travel.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelAvailabilityDTO {
    private Long hotelId;
    private String hotelName;
    private boolean available;
    private Integer availableRooms;
    private Double pricePerNight;
    private String message;
}
