package com.travel.hotel.controller;

import com.travel.hotel.dto.*;
import com.travel.hotel.service.HotelService;
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
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Hotel Service", description = "Hotel inventory and availability APIs")
public class HotelController {

    private final HotelService hotelService;

    @PostMapping
    @Operation(summary = "Create new hotel")
    public ResponseEntity<HotelResponseDTO> createHotel(@Valid @RequestBody HotelRequestDTO requestDTO) {
        log.info("POST /api/hotels - Creating new hotel");
        HotelDTO hotelDTO = hotelService.createHotel(requestDTO);
        HotelResponseDTO response = new HotelResponseDTO(
                true, "Hotel created successfully", hotelDTO
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get hotel by ID")
    public ResponseEntity<HotelResponseDTO> getHotelById(@PathVariable Long id) {
        log.info("GET /api/hotels/{}", id);
        HotelDTO hotelDTO = hotelService.getHotelById(id);
        HotelResponseDTO response = new HotelResponseDTO(
                true, "Hotel retrieved successfully", hotelDTO
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-availability/{id}")
    @Operation(summary = "Check hotel availability")
    public ResponseEntity<HotelAvailabilityDTO> checkAvailability(@PathVariable Long id) {
        log.info("GET /api/hotels/check-availability/{}", id);
        HotelAvailabilityDTO availability = hotelService.checkAvailability(id);
        return ResponseEntity.ok(availability);
    }

    @PutMapping("/{id}/reserve")
    @Operation(summary = "Reserve rooms")
    public ResponseEntity<HotelResponseDTO> reserveRooms(
            @PathVariable Long id,
            @Valid @RequestBody RoomReservationDTO reservationDTO) {
        log.info("PUT /api/hotels/{}/reserve - Reserving {} rooms", id, reservationDTO.getNumberOfRooms());
        HotelDTO hotelDTO = hotelService.reserveRooms(id, reservationDTO);
        HotelResponseDTO response = new HotelResponseDTO(
                true, "Rooms reserved successfully", hotelDTO
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search hotels")
    public ResponseEntity<List<HotelDTO>> searchHotels(
            @RequestParam String location,
            @RequestParam(required = false) Integer starRating) {
        log.info("GET /api/hotels/search");
        List<HotelDTO> hotels = hotelService.searchHotels(location, starRating);
        return ResponseEntity.ok(hotels);
    }

    @GetMapping
    @Operation(summary = "Get all hotels")
    public ResponseEntity<List<HotelDTO>> getAllHotels() {
        log.info("GET /api/hotels");
        List<HotelDTO> hotels = hotelService.getAllHotels();
        return ResponseEntity.ok(hotels);
    }
}
