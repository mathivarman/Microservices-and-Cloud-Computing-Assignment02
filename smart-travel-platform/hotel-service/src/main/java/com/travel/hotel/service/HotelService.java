package com.travel.hotel.service;

import com.travel.hotel.dto.*;
import com.travel.hotel.entity.Hotel;
import com.travel.hotel.exception.HotelNotFoundException;
import com.travel.hotel.exception.NoRoomsAvailableException;
import com.travel.hotel.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelService {

    private final HotelRepository hotelRepository;

    @Transactional
    public HotelDTO createHotel(HotelRequestDTO requestDTO) {
        log.info("Creating hotel: {}", requestDTO.getHotelName());
        
        Hotel hotel = new Hotel();
        hotel.setHotelName(requestDTO.getHotelName());
        hotel.setLocation(requestDTO.getLocation());
        hotel.setAddress(requestDTO.getAddress());
        hotel.setPricePerNight(requestDTO.getPricePerNight());
        hotel.setTotalRooms(requestDTO.getTotalRooms());
        hotel.setAvailableRooms(requestDTO.getTotalRooms());
        hotel.setStarRating(requestDTO.getStarRating());

        Hotel savedHotel = hotelRepository.save(hotel);
        log.info("Hotel created successfully with id: {}", savedHotel.getId());
        
        return convertToDTO(savedHotel);
    }

    public HotelDTO getHotelById(Long hotelId) {
        log.info("Fetching hotel with id: {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException(hotelId));
        return convertToDTO(hotel);
    }

    public HotelAvailabilityDTO checkAvailability(Long hotelId) {
        log.info("Checking availability for hotel id: {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException(hotelId));
        
        boolean available = hotel.getAvailableRooms() > 0;
        return new HotelAvailabilityDTO(
                hotel.getId(),
                hotel.getHotelName(),
                available,
                hotel.getAvailableRooms(),
                hotel.getPricePerNight()
        );
    }

    @Transactional
    public HotelDTO reserveRooms(Long hotelId, RoomReservationDTO reservationDTO) {
        log.info("Reserving {} rooms for hotel id: {}", reservationDTO.getNumberOfRooms(), hotelId);
        
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException(hotelId));
        
        if (hotel.getAvailableRooms() < reservationDTO.getNumberOfRooms()) {
            throw new NoRoomsAvailableException(hotelId, 
                    reservationDTO.getNumberOfRooms(), 
                    hotel.getAvailableRooms());
        }
        
        hotel.setAvailableRooms(hotel.getAvailableRooms() - reservationDTO.getNumberOfRooms());
        Hotel updatedHotel = hotelRepository.save(hotel);
        
        log.info("Successfully reserved {} rooms. Remaining: {}", 
                reservationDTO.getNumberOfRooms(), updatedHotel.getAvailableRooms());
        
        return convertToDTO(updatedHotel);
    }

    public List<HotelDTO> searchHotels(String location, Integer starRating) {
        log.info("Searching hotels in {} with rating {}", location, starRating);
        List<Hotel> hotels;
        if (starRating != null) {
            hotels = hotelRepository.findByLocationAndStarRating(location, starRating);
        } else {
            hotels = hotelRepository.findByLocation(location);
        }
        return hotels.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<HotelDTO> getAllHotels() {
        return hotelRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private HotelDTO convertToDTO(Hotel hotel) {
        return new HotelDTO(
                hotel.getId(),
                hotel.getHotelName(),
                hotel.getLocation(),
                hotel.getAddress(),
                hotel.getPricePerNight(),
                hotel.getTotalRooms(),
                hotel.getAvailableRooms(),
                hotel.getStarRating()
        );
    }
}
