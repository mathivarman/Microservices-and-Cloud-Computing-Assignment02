package com.travel.booking.controller;

import com.travel.booking.dto.BookingDTO;
import com.travel.booking.dto.BookingRequestDTO;
import com.travel.booking.dto.BookingResponseDTO;
import com.travel.booking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Booking Service", description = "Main orchestrator - Booking management APIs")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @Operation(summary = "Create new booking", description = "Orchestrates booking creation using Feign Client and WebClient")
    public ResponseEntity<BookingResponseDTO> createBooking(@Valid @RequestBody BookingRequestDTO requestDTO) {
        log.info("POST /api/bookings - Creating new booking");
        
        BookingDTO bookingDTO = bookingService.createBooking(requestDTO);
        BookingResponseDTO response = new BookingResponseDTO(
                true,
                "Booking created successfully. Status: PENDING. Please proceed to payment.",
                bookingDTO
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking by ID")
    public ResponseEntity<BookingResponseDTO> getBookingById(@PathVariable Long id) {
        log.info("GET /api/bookings/{}", id);
        
        BookingDTO bookingDTO = bookingService.getBookingById(id);
        BookingResponseDTO response = new BookingResponseDTO(
                true,
                "Booking retrieved successfully",
                bookingDTO
        );
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/confirm")
    @Operation(summary = "Confirm booking", description = "Called by Payment Service via WebClient")
    public ResponseEntity<BookingResponseDTO> confirmBooking(@PathVariable Long id) {
        log.info("PUT /api/bookings/{}/confirm - Confirming booking (called by Payment Service)", id);
        
        BookingDTO bookingDTO = bookingService.confirmBooking(id);
        BookingResponseDTO response = new BookingResponseDTO(
                true,
                "Booking confirmed successfully",
                bookingDTO
        );
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user bookings")
    public ResponseEntity<List<BookingDTO>> getUserBookings(@PathVariable Long userId) {
        log.info("GET /api/bookings/user/{}", userId);
        
        List<BookingDTO> bookings = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(bookings);
    }
}
