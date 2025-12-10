package com.travel.booking.client;

import com.travel.booking.dto.FlightAvailabilityDTO;
import com.travel.booking.dto.ReservationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Feign Client for Flight Service
 */
@FeignClient(name = "flight-service", url = "${services.flight-service.url}")
public interface FlightFeignClient {

    @GetMapping("/api/flights/check-availability/{id}")
    ResponseEntity<FlightAvailabilityDTO> checkAvailability(@PathVariable("id") Long id);

    @PutMapping("/api/flights/{id}/reserve")
    ResponseEntity<?> reserveSeats(@PathVariable("id") Long id, @RequestBody ReservationDTO reservationDTO);
}
