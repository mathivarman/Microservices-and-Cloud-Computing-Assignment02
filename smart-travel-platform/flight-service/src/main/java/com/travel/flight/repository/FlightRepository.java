package com.travel.flight.repository;

import com.travel.flight.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    
    Optional<Flight> findByFlightNumber(String flightNumber);
    
    List<Flight> findByOriginAndDestinationAndDepartureDate(
            String origin, String destination, LocalDate departureDate);
    
    List<Flight> findByOriginAndDestination(String origin, String destination);
}
