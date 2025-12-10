package com.travel.booking.service;

import com.travel.booking.client.FlightFeignClient;
import com.travel.booking.client.HotelFeignClient;
import com.travel.booking.dto.*;
import com.travel.booking.entity.Booking;
import com.travel.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main orchestrator service using Feign Client and WebClient
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlightFeignClient flightFeignClient;
    private final HotelFeignClient hotelFeignClient;
    private final WebClient userServiceWebClient;
    private final WebClient notificationServiceWebClient;

    @Transactional
    public BookingDTO createBooking(BookingRequestDTO requestDTO) {
        log.info("🎫 Creating booking for user {}", requestDTO.getUserId());
        
        // Step 1: Validate user via WebClient
        log.info("Step 1: Validating user {} via WebClient", requestDTO.getUserId());
        validateUserViaWebClient(requestDTO.getUserId());
        
        // Step 2: Check flight availability via Feign Client
        log.info("Step 2: Checking flight {} availability via Feign Client", requestDTO.getFlightId());
        ResponseEntity<FlightAvailabilityDTO> flightResponse = 
                flightFeignClient.checkAvailability(requestDTO.getFlightId());
        FlightAvailabilityDTO flightAvailability = flightResponse.getBody();
        
        if (flightAvailability == null || !flightAvailability.isAvailable() || 
                flightAvailability.getAvailableSeats() < requestDTO.getNumberOfPassengers()) {
            throw new RuntimeException("Flight not available or insufficient seats");
        }
        
        // Step 3: Check hotel availability via Feign Client
        log.info("Step 3: Checking hotel {} availability via Feign Client", requestDTO.getHotelId());
        ResponseEntity<HotelAvailabilityDTO> hotelResponse = 
                hotelFeignClient.checkAvailability(requestDTO.getHotelId());
        HotelAvailabilityDTO hotelAvailability = hotelResponse.getBody();
        
        if (hotelAvailability == null || !hotelAvailability.isAvailable() || 
                hotelAvailability.getAvailableRooms() < 1) {
            throw new RuntimeException("Hotel not available or no rooms");
        }
        
        // Step 4: Calculate total cost
        log.info("Step 4: Calculating total cost");
        double flightCost = flightAvailability.getPricePerSeat() * requestDTO.getNumberOfPassengers();
        double hotelCost = hotelAvailability.getPricePerNight() * requestDTO.getNumberOfNights();
        double totalCost = flightCost + hotelCost;
        
        // Step 5: Save booking as PENDING
        log.info("Step 5: Saving booking as PENDING");
        Booking booking = new Booking();
        booking.setUserId(requestDTO.getUserId());
        booking.setFlightId(requestDTO.getFlightId());
        booking.setHotelId(requestDTO.getHotelId());
        booking.setTravelDate(requestDTO.getTravelDate());
        booking.setNumberOfNights(requestDTO.getNumberOfNights());
        booking.setNumberOfPassengers(requestDTO.getNumberOfPassengers());
        booking.setFlightCost(flightCost);
        booking.setHotelCost(hotelCost);
        booking.setTotalCost(totalCost);
        booking.setStatus("PENDING");
        
        Booking savedBooking = bookingRepository.save(booking);
        log.info("✅ Booking created with ID: {} - Status: PENDING - Total: ${}", 
                savedBooking.getId(), totalCost);
        log.info("💳 Please proceed to Payment Service with booking ID: {}", savedBooking.getId());
        
        return convertToDTO(savedBooking);
    }

    @Transactional
    public BookingDTO confirmBooking(Long bookingId) {
        log.info("🎉 Confirming booking {}", bookingId);
        
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));
        
        if (!"PENDING".equals(booking.getStatus())) {
            log.warn("Booking {} is already in status: {}", bookingId, booking.getStatus());
            return convertToDTO(booking);
        }
        
        try {
            // Step 8a: Reserve flight seats via Feign Client
            log.info("Step 8a: Reserving {} flight seats via Feign Client", booking.getNumberOfPassengers());
            ReservationDTO flightReservation = new ReservationDTO(booking.getNumberOfPassengers());
            flightFeignClient.reserveSeats(booking.getFlightId(), flightReservation);
            
            // Step 8b: Reserve hotel rooms via Feign Client
            log.info("Step 8b: Reserving 1 hotel room via Feign Client");
            RoomReservationDTO hotelReservation = new RoomReservationDTO(1);
            hotelFeignClient.reserveRooms(booking.getHotelId(), hotelReservation);
            
            // Step 9: Update booking to CONFIRMED
            log.info("Step 9: Updating booking to CONFIRMED");
            booking.setStatus("CONFIRMED");
            booking.setConfirmedAt(LocalDateTime.now());
            Booking confirmedBooking = bookingRepository.save(booking);
            
            // Step 10: Send notification via WebClient
            log.info("Step 10: Sending confirmation notification via WebClient");
            sendNotificationViaWebClient(
                    booking.getUserId(),
                    String.format("Your booking #%d has been CONFIRMED! Total: $%.2f", 
                            bookingId, booking.getTotalCost())
            );
            
            log.info("✅ Booking {} successfully CONFIRMED!", bookingId);
            return convertToDTO(confirmedBooking);
            
        } catch (Exception e) {
            log.error("Error confirming booking: {}", e.getMessage());
            booking.setStatus("FAILED");
            bookingRepository.save(booking);
            throw new RuntimeException("Failed to confirm booking: " + e.getMessage());
        }
    }

    public BookingDTO getBookingById(Long bookingId) {
        log.info("Fetching booking with id: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));
        return convertToDTO(booking);
    }

    public List<BookingDTO> getUserBookings(Long userId) {
        log.info("Fetching bookings for user {}", userId);
        return bookingRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Validate user via WebClient (User Service)
     */
    private void validateUserViaWebClient(Long userId) {
        try {
            String response = userServiceWebClient
                    .get()
                    .uri("/api/users/validate/{id}", userId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info("✅ User {} validated via WebClient", userId);
        } catch (Exception e) {
            log.error("❌ User validation failed: {}", e.getMessage());
            throw new RuntimeException("User not found with id: " + userId);
        }
    }

    /**
     * Send notification via WebClient (Notification Service)
     */
    private void sendNotificationViaWebClient(Long userId, String message) {
        try {
            NotificationRequestDTO notificationRequest = new NotificationRequestDTO(
                    userId, message, "EMAIL"
            );
            
            notificationServiceWebClient
                    .post()
                    .uri("/api/notifications/send")
                    .bodyValue(notificationRequest)
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(
                            response -> log.info("✅ Notification sent via WebClient"),
                            error -> log.error("❌ Notification failed: {}", error.getMessage())
                    );
        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage());
        }
    }

    private BookingDTO convertToDTO(Booking booking) {
        return new BookingDTO(
                booking.getId(),
                booking.getUserId(),
                booking.getFlightId(),
                booking.getHotelId(),
                booking.getTravelDate(),
                booking.getNumberOfNights(),
                booking.getNumberOfPassengers(),
                booking.getFlightCost(),
                booking.getHotelCost(),
                booking.getTotalCost(),
                booking.getStatus(),
                booking.getCreatedAt(),
                booking.getConfirmedAt()
        );
    }
}
