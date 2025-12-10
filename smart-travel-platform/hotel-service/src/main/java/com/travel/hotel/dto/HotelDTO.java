package com.travel.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelDTO {
    private Long id;
    private String hotelName;
    private String location;
    private String address;
    private Double pricePerNight;
    private Integer totalRooms;
    private Integer availableRooms;
    private Integer starRating;
}
