package com.travel.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private Long id;
    private Long userId;
    private Long flightId;
    private Long hotelId;
    private LocalDate travelDate;
    private Integer numberOfNights;
    private Integer numberOfPassengers;
    private Double flightCost;
    private Double hotelCost;
    private Double totalCost;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
}
