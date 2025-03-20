package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;

import java.util.List;

public interface BookingService {

    public BookingDto createBooking(Long userId, RequestBookingDto requestBookingDto);

    public BookingDto updateBooking(Long userId, Long bookingId, Boolean approved);

    public BookingDto getBookingById(Long userId, Long bookingId);

    public List<BookingDto> getAllBookingsByUserId(Long userId, String state, Integer from, Integer size);

    public List<BookingDto> getBookingsByOwnerId(Long userId, String state, Integer from, Integer size);
}
