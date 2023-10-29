package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.PostBookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;


public interface BookingService {
    BookingDto create(PostBookingDto postBookingDto, Integer bookerId);

    BookingDto update(Integer bookingId, Integer userId, Boolean approved);

    BookingDto getBookingById(Integer bookingId, Integer userId);

    List<BookingDto> getBookingList(String state, Integer userId, Integer from, Integer size);

    List<BookingDto> getBookingsOwner(String state, Integer userId, Integer from, Integer size);

    BookingShortDto getLastBooking(Integer itemId);

    BookingShortDto getNextBooking(Integer itemId);

    Booking getBookingWithUserBookedItem(Integer itemId, Integer userId);
}
