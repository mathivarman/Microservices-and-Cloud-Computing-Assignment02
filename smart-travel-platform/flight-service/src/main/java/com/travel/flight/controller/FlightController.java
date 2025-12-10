package com.travel.flight.controller;

import com.travel.flight.dto.*;
import com.travel.flight.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Flight Service", description = "Flight inventory and availability APIs")
public class FlightController {

    private final FlightService flightService;

    @PostMapping
    @Operation(summary = "Create new flight", description = "Creates a new flight in the system")
    public ResponseEntity<FlightResponseDTO> createFlight(@Valid @RequestBody FlightRequestDTO requestDTO) {
        log.info("POST /api/flights - Creating new flight");
        
        FlightDTO flightDTO = flightService.createFlight(requestDTO);
        FlightResponseDTO response = new FlightResponseDTO(
                true,
                "Flight created successfully",
                flightDTO
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get flight by ID", description = "Retrieves flight details by ID")
    public ResponseEntity<FlightResponseDTO> getFlightById(@PathVariable Long id) {
        log.info("GET /api/flights/{} - Fetching flight", id);
        
        FlightDTO flightDTO = flightService.getFlightById(id);
        FlightResponseDTO response = new FlightResponseDTO(
                true,
                "Flight retrieved successfully",
                flightDTO
        );
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-availability/{id}")
    @Operation(summary = "Check flight availability", description = "Checks if flight has available seats")
    public ResponseEntity<FlightAvailabilityDTO> checkAvailability(@PathVariable Long id) {
        log.info("GET /api/flights/check-availability/{} - Checking availability", id);
        
        FlightAvailabilityDTO availability = flightService.checkAvailability(id);
        return ResponseEntity.ok(availability);
    }

    @PutMapping("/{id}/reserve")
    @Operation(summary = "Reserve seats", description = "Reserves specified number of seats on a flight")
    public ResponseEntity<FlightResponseDTO> reserveSeats(
            @PathVariable Long id,
            @Valid @RequestBody ReservationDTO reservationDTO) {
        log.info("PUT /api/flights/{}/reserve - Reserving {} seats", id, reservationDTO.getNumberOfSeats());
        
        FlightDTO flightDTO = flightService.reserveSeats(id, reservationDTO);
        FlightResponseDTO response = new FlightResponseDTO(
                true,
                "Seats reserved successfully",
                flightDTO
        );
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search flights", description = "Search flights by origin, destination and optional date")
    public ResponseEntity<List<FlightDTO>> searchFlights(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("GET /api/flights/search - Searching flights from {} to {} on {}", origin, destination, date);
        
        List<FlightDTO> flights = flightService.searchFlights(origin, destination, date);
        return ResponseEntity.ok(flights);
    }

    @GetMapping
    @Operation(summary = "Get all flights", description = "Retrieves all flights in the system")
    public ResponseEntity<List<FlightDTO>> getAllFlights() {
        log.info("GET /api/flights - Fetching all flights");
        
        List<FlightDTO> flights = flightService.getAllFlights();
        return ResponseEntity.ok(flights);
    }
}
