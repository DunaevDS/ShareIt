package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.PostBookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.InternalServerErrorException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class BookingServiceTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final User user = new User(30, "First", "first@first30.ru");
    private final UserDto userDto1 = new UserDto(301, "AlexOne", "alexone@alex30.ru");
    private final UserDto userDto2 = new UserDto(302, "AlexTwo", "alextwo@alex30.ru");
    private final ItemDto itemDto1 = new ItemDto(301, "Item1", "Description1", true,
            user, null, null, null, null);
    private final ItemDto itemDto2 = new ItemDto(302, "Item2", "Description2", true,
            user, null, null, null, null);

    @Test
    void test_CreateBookingByOwnerItem() {
        UserDto ownerDto = userService.create(userDto1);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        PostBookingDto bookingInputDto = new PostBookingDto(
                newItemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));
        NotFoundException exp = assertThrows(NotFoundException.class,
                () -> bookingService.create(bookingInputDto, ownerDto.getId()));
        assertEquals("Item with id= " + newItemDto.getId() + " can not be booked by owner",
                exp.getMessage());
    }

    @Test
    void test_GetBookingByNotOwnerOrNotBooker() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        UserDto userDto3 = new UserDto(303, "AlexThird", "alexthird@alex30.ru");
        userDto3 = userService.create(userDto3);
        Integer userId = userDto3.getId();
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        PostBookingDto bookingInputDto = new PostBookingDto(
                newItemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));
        BookingDto bookingDto = bookingService.create(bookingInputDto, newUserDto.getId());
        NotFoundException exp = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(bookingDto.getId(), userId));
        assertEquals("Booking was not found", exp.getMessage());
    }

    @Test
    void test_GetBookingsByBooker_SizeNotNull() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        PostBookingDto bookingInputDto = new PostBookingDto(
                newItemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));
        bookingService.create(bookingInputDto, newUserDto.getId());
        PostBookingDto bookingInputDto1 = new PostBookingDto(
                newItemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 0, 0),
                LocalDateTime.of(2031, 12, 26, 12, 0, 0));
        bookingService.create(bookingInputDto1, newUserDto.getId());
        List<BookingDto> listBookings = bookingService.getBookingList("ALL", newUserDto.getId(), 0, 1);
        assertEquals(1, listBookings.size());
    }

    @Test
    void test_GetBookingsByBooker_WaitingStatus_SizeNotNull() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        PostBookingDto bookingInputDto = new PostBookingDto(
                newItemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));
        bookingService.create(bookingInputDto, newUserDto.getId());
        PostBookingDto bookingInputDto1 = new PostBookingDto(
                newItemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 0, 0),
                LocalDateTime.of(2031, 12, 26, 12, 0, 0));
        bookingService.create(bookingInputDto1, newUserDto.getId());
        List<BookingDto> listBookings = bookingService.getBookingList("WAITING", newUserDto.getId(),
                0, 1);
        assertEquals(1, listBookings.size());
    }

    @Test
    void test_GetBookingsByBooker_RejectedStatus_SizeNotNull() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        PostBookingDto bookingInputDto = new PostBookingDto(
                newItemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));
        bookingService.create(bookingInputDto, newUserDto.getId());
        PostBookingDto bookingInputDto1 = new PostBookingDto(
                newItemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 0, 0),
                LocalDateTime.of(2031, 12, 26, 12, 0, 0));
        bookingService.create(bookingInputDto1, newUserDto.getId());
        List<BookingDto> listBookings = bookingService.getBookingList("REJECTED", newUserDto.getId(),
                0, 1);
        assertEquals(0, listBookings.size());
    }

    @Test
    void test_GetBookingsByOwner_SizeNotNull() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        PostBookingDto bookingInputDto = new PostBookingDto(
                newItemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));
        bookingService.create(bookingInputDto, newUserDto.getId());
        PostBookingDto bookingInputDto1 = new PostBookingDto(
                newItemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 0, 0),
                LocalDateTime.of(2031, 12, 26, 12, 0, 0));
        bookingService.create(bookingInputDto1, newUserDto.getId());
        List<BookingDto> listBookings = bookingService.getBookingsOwner("ALL", ownerDto.getId(),
                0, 1);
        assertEquals(1, listBookings.size());
    }

    @Test
    void test_GetBookingsByOwner_StatusWaiting_SizeNotNull() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        PostBookingDto bookingInputDto = new PostBookingDto(
                newItemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));
        bookingService.create(bookingInputDto, newUserDto.getId());
        PostBookingDto bookingInputDto1 = new PostBookingDto(
                newItemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 0, 0),
                LocalDateTime.of(2031, 12, 26, 12, 0, 0));
        bookingService.create(bookingInputDto1, newUserDto.getId());
        List<BookingDto> listBookings = bookingService.getBookingsOwner("WAITING", ownerDto.getId(),
                0, 1);
        assertEquals(1, listBookings.size());
    }

    @Test
    void test_GetBookingsByOwner_StatusRejected_SizeNotNull() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        PostBookingDto bookingInputDto = new PostBookingDto(
                newItemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));
        bookingService.create(bookingInputDto, newUserDto.getId());
        PostBookingDto bookingInputDto1 = new PostBookingDto(
                newItemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 0, 0),
                LocalDateTime.of(2031, 12, 26, 12, 0, 0));
        bookingService.create(bookingInputDto1, newUserDto.getId());
        List<BookingDto> listBookings = bookingService.getBookingsOwner("REJECTED", ownerDto.getId(),
                0, 1);
        assertEquals(0, listBookings.size());
    }

    @Test
    void test_GetBookingsByOwner_StatusCurrent_SizeNotNull() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        PostBookingDto bookingInputDto = new PostBookingDto(
                newItemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));
        bookingService.create(bookingInputDto, newUserDto.getId());
        PostBookingDto bookingInputDto1 = new PostBookingDto(
                newItemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 0, 0),
                LocalDateTime.of(2031, 12, 26, 12, 0, 0));
        bookingService.create(bookingInputDto1, newUserDto.getId());
        List<BookingDto> listBookings = bookingService.getBookingsOwner("CURRENT", ownerDto.getId(),
                0, 1);
        assertEquals(0, listBookings.size());
    }

    @Test
    void test_GetBookingsByOwner_StatusUnknown() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        PostBookingDto bookingInputDto = new PostBookingDto(
                newItemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 0, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0, 0));
        bookingService.create(bookingInputDto, newUserDto.getId());
        PostBookingDto bookingInputDto1 = new PostBookingDto(
                newItemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 0, 0),
                LocalDateTime.of(2031, 12, 26, 12, 0, 0));
        bookingService.create(bookingInputDto1, newUserDto.getId());

        InternalServerErrorException exp = assertThrows(InternalServerErrorException.class,
                () -> bookingService.getBookingsOwner("UNKNOWN", ownerDto.getId(),
                        0, 1));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", exp.getMessage());
    }

    @Test
    void test_GetBookingsByBooker_FutureStatus_SizeNotNull() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());

        PostBookingDto bookingInputDto = new PostBookingDto(
                newItemDto.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));
        bookingService.create(bookingInputDto, newUserDto.getId());

        PostBookingDto bookingInputDto1 = new PostBookingDto(
                newItemDto.getId(),
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1));
        bookingService.create(bookingInputDto1, newUserDto.getId());

        List<BookingDto> listBookings = bookingService.getBookingList("FUTURE", newUserDto.getId(), 0, 1);
        assertEquals(1, listBookings.size());
    }

    @Test
    void test_GetBookingsByBooker_PastStatus_SizeNotNull() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());

        PostBookingDto bookingInputDto = new PostBookingDto(
                newItemDto.getId(),
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1));
        bookingService.create(bookingInputDto, newUserDto.getId());

        PostBookingDto bookingInputDto1 = new PostBookingDto(
                newItemDto.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));
        bookingService.create(bookingInputDto1, newUserDto.getId());

        List<BookingDto> listBookings = bookingService.getBookingList("PAST", newUserDto.getId(), 0, 1);
        assertEquals(1, listBookings.size());
    }

    @Test
    void test_GetBookingsByBooker_CurrentStatus_SizeNotNull() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());

        PostBookingDto bookingInputDto = new PostBookingDto(
                newItemDto.getId(),
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().plusHours(2));
        bookingService.create(bookingInputDto, newUserDto.getId());

        PostBookingDto bookingInputDto1 = new PostBookingDto(
                newItemDto.getId(),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1).plusHours(2));
        bookingService.create(bookingInputDto1, newUserDto.getId());

        List<BookingDto> listBookings = bookingService.getBookingList("CURRENT", newUserDto.getId(), 0, 1);
        assertEquals(1, listBookings.size());
    }
}