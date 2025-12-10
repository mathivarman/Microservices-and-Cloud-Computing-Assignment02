package com.travel.hotel.dto;

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

    public HotelAvailabilityDTO(Long hotelId, String hotelName, boolean available, 
                                 Integer availableRooms, Double pricePerNight) {
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.available = available;
        this.availableRooms = availableRooms;
        this.pricePerNight = pricePerNight;
        this.message = available ? "Hotel available" : "No rooms available";
    }
}
