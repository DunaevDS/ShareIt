package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.coment.dto.CommentDto;
import ru.practicum.shareit.user.UserMapper;

import java.util.List;

@UtilityClass
public class BookingMapper {
    public BookingDto toBookingDto(Booking booking, List<CommentDto> comments) {
        if (booking != null) {
            return new BookingDto(
                    booking.getId(),
                    booking.getStart(),
                    booking.getEnd(),
                    ItemMapper.mapToItemDto(booking.getItem(), comments),
                    UserMapper.mapToUserDto(booking.getBooker()),
                    booking.getStatus()
            );
        } else {
            return null;
        }
    }

    public BookingShortDto toBookingShortDto(Booking booking) {
        if (booking != null) {
            return new BookingShortDto(
                    booking.getId(),
                    booking.getBooker().getId(),
                    booking.getStart(),
                    booking.getEnd()
            );
        } else {
            return null;
        }
    }
}
