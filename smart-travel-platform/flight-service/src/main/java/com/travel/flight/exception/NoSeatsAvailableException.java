package com.travel.flight.exception;

public class NoSeatsAvailableException extends RuntimeException {

    public NoSeatsAvailableException(String message) {
        super(message);
    }

    public NoSeatsAvailableException(Long flightId, Integer requested, Integer available) {
        super(String.format("Not enough seats available on flight %d. Requested: %d, Available: %d", 
                flightId, requested, available));
    }
}
