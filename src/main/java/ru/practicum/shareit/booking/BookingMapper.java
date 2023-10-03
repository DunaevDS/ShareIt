package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.PostBookingDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.service.ConnectingService;
import ru.practicum.shareit.user.UserMapper;

@Component
public class BookingMapper {
    private final ConnectingService connectingService;
    private final ItemMapper itemMapper;

    @Autowired
    public BookingMapper(ConnectingService connectingService,
                         ItemMapper itemMapper) {
        this.connectingService = connectingService;
        this.itemMapper = itemMapper;
    }

    public BookingDto toBookingDto(Booking booking) {
        if (booking != null) {
            return new BookingDto(
                    booking.getId(),
                    booking.getStart(),
                    booking.getEnd(),
                    itemMapper.mapToItemDto(booking.getItem()),
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

    public Booking toBooking(PostBookingDto postBookingDto, Integer bookerId) {
        return new Booking(
                null,
                postBookingDto.getStart(),
                postBookingDto.getEnd(),
                connectingService.findItemById(postBookingDto.getItemId()),
                connectingService.findUserById(bookerId),
                Status.WAITING
        );
    }
}
