package com.travel.hotel.exception;

public class NoRoomsAvailableException extends RuntimeException {
    public NoRoomsAvailableException(Long hotelId, Integer requested, Integer available) {
        super(String.format("Not enough rooms available at hotel %d. Requested: %d, Available: %d", 
                hotelId, requested, available));
    }
}
