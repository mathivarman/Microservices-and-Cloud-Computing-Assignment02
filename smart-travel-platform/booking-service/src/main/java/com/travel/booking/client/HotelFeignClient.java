package com.travel.booking.client;

import com.travel.booking.dto.HotelAvailabilityDTO;
import com.travel.booking.dto.RoomReservationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Feign Client for Hotel Service
 */
@FeignClient(name = "hotel-service", url = "${services.hotel-service.url}")
public interface HotelFeignClient {

    @GetMapping("/api/hotels/check-availability/{id}")
    ResponseEntity<HotelAvailabilityDTO> checkAvailability(@PathVariable("id") Long id);

    @PutMapping("/api/hotels/{id}/reserve")
    ResponseEntity<?> reserveRooms(@PathVariable("id") Long id, @RequestBody RoomReservationDTO reservationDTO);
}
