package com.travel.booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long flightId;

    @Column(nullable = false)
    private Long hotelId;

    @Column(nullable = false)
    private LocalDate travelDate;

    @Column(nullable = false)
    private Integer numberOfNights;

    @Column(nullable = false)
    private Integer numberOfPassengers;

    @Column(nullable = false)
    private Double flightCost;

    @Column(nullable = false)
    private Double hotelCost;

    @Column(nullable = false)
    private Double totalCost;

    @Column(nullable = false)
    private String status; // PENDING, CONFIRMED, CANCELLED, FAILED

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = "PENDING";
        }
    }
}
