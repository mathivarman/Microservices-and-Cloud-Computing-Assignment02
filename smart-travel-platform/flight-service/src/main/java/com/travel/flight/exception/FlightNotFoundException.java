package com.travel.flight.exception;

public class FlightNotFoundException extends RuntimeException {

    public FlightNotFoundException(String message) {
        super(message);
    }

    public FlightNotFoundException(Long flightId) {
        super("Flight not found with id: " + flightId);
    }
}
