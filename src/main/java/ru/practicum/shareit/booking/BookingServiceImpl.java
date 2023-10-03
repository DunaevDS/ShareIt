package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.PostBookingDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    @Lazy
    public BookingServiceImpl(BookingRepository bookingRepository,
                              BookingMapper bookingMapper,
                              UserService userService,
                              ItemService itemService) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    public BookingDto create(PostBookingDto postBookingDto, Integer bookerId) {
        userService.findUserById(bookerId);

        Integer itemId = postBookingDto.getItemId();
        boolean isAvailable = itemService.findItemById(itemId).getAvailable();

        if (!isAvailable) {
            log.error("ValidationException: Item with id='{}' can not be booked.", itemId);
            throw new PermissionException("Item with id = " + itemId + " can not be booked");
        }

        Booking booking = bookingMapper.toBooking(postBookingDto, bookerId);
        Integer bookingOwnerId = booking.getItem().getOwner().getId();

        if (bookerId.equals(bookingOwnerId)) {
            log.error("PermissionException: Item with id='{}' can not be booked by owner", itemId);
            throw new UserNotFoundException("Item with id= " + itemId +
                    " can not be booked by owner");
        }

        bookingTimeValidation(booking);

        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto update(Integer bookingId, Integer userId, Boolean approved) {
        userService.findUserById(userId);

        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            log.error("BookingNotFoundException: Booking with id='{}' was not found.", bookingId);
            throw new BookingNotFoundException("Booking was not found");
        }

        bookingTimeValidation(booking);

        Integer bookerId = booking.getBooker().getId();
        Integer bookerItemId = booking.getItem().getId();

        if (bookerId.equals(userId)) {
            if (!approved) {
                booking.setStatus(Status.CANCELED);
            } else {
                log.error("BookingNotFoundException: Booking with id='{}' was not found.", bookingId);
                throw new BookingNotFoundException("Booking was not found");
            }
        } else if ((isItemOwner(bookerItemId, userId))
                && (!booking.getStatus().equals(Status.CANCELED))) {
            if (!booking.getStatus().equals(Status.WAITING)) {
                log.error("BookingAlreadyApprovedException: Booking was made already");
                throw new BookingAlreadyApprovedException("Booking was made already");
            }
            if (approved) {
                booking.setStatus(Status.APPROVED);
                log.info("Пользователь с ID={} подтвердил бронирование с ID={}", userId, bookingId);
            } else {
                booking.setStatus(Status.REJECTED);
                log.info("Пользователь с ID={} отклонил бронирование с ID={}", userId, bookingId);
            }
        } else {
            if (booking.getStatus().equals(Status.CANCELED)) {
                log.error("ValidationException: booking with id='{}' was cancelled", bookingId);
                throw new ValidationException("Booking with id= " + bookingId + " was cancelled");
            } else {
                log.error("PermissionException: user with id='{}' is not an owner. " +
                        "Booking can be approved only by an owner", userId);
                throw new PermissionException("booking can be approved only by an owner");
            }
        }

        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Integer bookingId, Integer userId) {
        userService.findUserById(userId);

        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            log.error("BookingNotFoundException: Booking with id='{}' was not found.", bookingId);
            throw new BookingNotFoundException("Booking was not found");
        }

        Integer bookerId = booking.getBooker().getId();
        Integer bookerItemId = booking.getItem().getId();

        if (bookerId.equals(userId) || isItemOwner(bookerItemId, userId)) {
            return bookingMapper.toBookingDto(booking);
        } else {
            log.error("BookingNotFoundException: Booking with id='{}' was not found.", bookingId);
            throw new BookingNotFoundException("Booking was not found");
        }
    }

    @Override
    public List<BookingDto> getBookingList(String state, Integer userId) {
        userService.findUserById(userId);

        List<Booking> bookings;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByBookerId(userId, sort);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now(), sort);
                break;
            case "PAST":
                bookings = bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), sort);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), sort);
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerIdAndStatus(userId, Status.WAITING, sort);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatus(userId, Status.REJECTED, sort);
                break;
            default:
                log.error("UnsupportedStatusException: unknown state='{}'", state);
                throw new UnsupportedStatusException("Unknown state: " + state);
        }
        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsOwner(String state, Integer userId) {
        userService.findUserById(userId);

        List<Booking> bookings;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByItem_Owner_Id(userId, sort);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now(), sort);
                break;
            case "PAST":
                bookings = bookingRepository.findByItem_Owner_IdAndEndIsBefore(userId, LocalDateTime.now(), sort);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByItem_Owner_IdAndStartIsAfter(userId, LocalDateTime.now(),
                        sort);
                break;
            case "WAITING":
                bookings = bookingRepository.findByItem_Owner_IdAndStatus(userId, Status.WAITING, sort);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByItem_Owner_IdAndStatus(userId, Status.REJECTED, sort);
                break;
            default:
                log.error("UnsupportedStatusException: unknown state='{}'", state);
                throw new UnsupportedStatusException("Unknown state: " + state);
        }
        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingShortDto getLastBooking(Integer itemId) {
        return bookingMapper.toBookingShortDto(bookingRepository.findFirstByItem_IdAndEndBeforeAndStatusNotOrderByEndDesc(itemId,
                LocalDateTime.now(), Status.REJECTED));
    }

    @Override
    public BookingShortDto getNextBooking(Integer itemId) {
        return bookingMapper.toBookingShortDto(bookingRepository.findFirstByItem_IdAndStartAfterAndStatusNotOrderByStartAsc(
                itemId, LocalDateTime.now(), Status.REJECTED));
    }

    @Override
    public Booking getBookingWithUserBookedItem(Integer itemId, Integer userId) {
        return bookingRepository.findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(itemId,
                userId, LocalDateTime.now(), Status.APPROVED);
    }

    private boolean isItemOwner(Integer itemId, Integer userId) {
        return itemService.getItemsByOwner(userId)
                .stream()
                .anyMatch(item -> item.getId().equals(itemId));
    }

    private void bookingTimeValidation(Booking booking) {
        if (booking.getStart() == null
                || booking.getEnd() == null
                || booking.getEnd().isBefore(booking.getStart())
                || booking.getStart().isAfter(booking.getEnd())
                || booking.getStart().equals(booking.getEnd())) {
            log.error("IncorrectDateException");
            throw new IncorrectDateException("IncorrectDateException");
        }
    }
}
