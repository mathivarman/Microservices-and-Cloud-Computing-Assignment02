package com.travel.flight.service;

import com.travel.flight.dto.*;
import com.travel.flight.entity.Flight;
import com.travel.flight.exception.FlightNotFoundException;
import com.travel.flight.exception.NoSeatsAvailableException;
import com.travel.flight.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlightService {

    private final FlightRepository flightRepository;

    @Transactional
    public FlightDTO createFlight(FlightRequestDTO requestDTO) {
        log.info("Creating flight: {}", requestDTO.getFlightNumber());
        
        Flight flight = new Flight();
        flight.setFlightNumber(requestDTO.getFlightNumber());
        flight.setOrigin(requestDTO.getOrigin());
        flight.setDestination(requestDTO.getDestination());
        flight.setDepartureDate(requestDTO.getDepartureDate());
        flight.setDepartureTime(requestDTO.getDepartureTime());
        flight.setArrivalDate(requestDTO.getArrivalDate());
        flight.setArrivalTime(requestDTO.getArrivalTime());
        flight.setPricePerSeat(requestDTO.getPricePerSeat());
        flight.setTotalSeats(requestDTO.getTotalSeats());
        flight.setAvailableSeats(requestDTO.getTotalSeats()); // Initially all seats available
        flight.setAirline(requestDTO.getAirline());

        Flight savedFlight = flightRepository.save(flight);
        log.info("Flight created successfully with id: {}", savedFlight.getId());
        
        return convertToDTO(savedFlight);
    }

    public FlightDTO getFlightById(Long flightId) {
        log.info("Fetching flight with id: {}", flightId);
        
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new FlightNotFoundException(flightId));
        
        return convertToDTO(flight);
    }

    public FlightAvailabilityDTO checkAvailability(Long flightId) {
        log.info("Checking availability for flight id: {}", flightId);
        
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new FlightNotFoundException(flightId));
        
        boolean available = flight.getAvailableSeats() > 0;
        
        return new FlightAvailabilityDTO(
                flight.getId(),
                flight.getFlightNumber(),
                available,
                flight.getAvailableSeats(),
                flight.getPricePerSeat()
        );
    }

    @Transactional
    public FlightDTO reserveSeats(Long flightId, ReservationDTO reservationDTO) {
        log.info("Reserving {} seats for flight id: {}", reservationDTO.getNumberOfSeats(), flightId);
        
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new FlightNotFoundException(flightId));
        
        if (flight.getAvailableSeats() < reservationDTO.getNumberOfSeats()) {
            throw new NoSeatsAvailableException(flightId, 
                    reservationDTO.getNumberOfSeats(), 
                    flight.getAvailableSeats());
        }
        
        flight.setAvailableSeats(flight.getAvailableSeats() - reservationDTO.getNumberOfSeats());
        Flight updatedFlight = flightRepository.save(flight);
        
        log.info("Successfully reserved {} seats. Remaining: {}", 
                reservationDTO.getNumberOfSeats(), updatedFlight.getAvailableSeats());
        
        return convertToDTO(updatedFlight);
    }

    public List<FlightDTO> searchFlights(String origin, String destination, LocalDate date) {
        log.info("Searching flights from {} to {} on {}", origin, destination, date);
        
        List<Flight> flights;
        if (date != null) {
            flights = flightRepository.findByOriginAndDestinationAndDepartureDate(origin, destination, date);
        } else {
            flights = flightRepository.findByOriginAndDestination(origin, destination);
        }
        
        return flights.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FlightDTO> getAllFlights() {
        log.info("Fetching all flights");
        
        return flightRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private FlightDTO convertToDTO(Flight flight) {
        return new FlightDTO(
                flight.getId(),
                flight.getFlightNumber(),
                flight.getOrigin(),
                flight.getDestination(),
                flight.getDepartureDate(),
                flight.getDepartureTime(),
                flight.getArrivalDate(),
                flight.getArrivalTime(),
                flight.getPricePerSeat(),
                flight.getTotalSeats(),
                flight.getAvailableSeats(),
                flight.getAirline()
        );
    }
}
