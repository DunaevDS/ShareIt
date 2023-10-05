package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.PostBookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.coment.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    @Lazy
    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserService userService,
                              ItemService itemService) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    public BookingDto create(PostBookingDto postBookingDto, Integer bookerId) {

        User user = UserMapper.mapToUser(userService.findUserById(bookerId));

        Integer itemId = postBookingDto.getItemId();
        Item item = itemService.findItemById(itemId);
        boolean isAvailable = item.getAvailable();

        Integer bookingOwnerId = item.getOwner().getId();

        if (bookerId.equals(bookingOwnerId)) {
            log.error("PermissionException: Item with id='{}' can not be booked by owner", itemId);
            throw new UserNotFoundException("Item with id= " + itemId +
                    " can not be booked by owner");
        }

        if (!isAvailable) {
            log.error("ValidationException: Item with id='{}' can not be booked.", itemId);
            throw new PermissionException("Item with id = " + itemId + " can not be booked");
        }

        Booking booking = new Booking(
                null,
                postBookingDto.getStart(),
                postBookingDto.getEnd(),
                item,
                user,
                Status.WAITING
        );

        bookingTimeValidation(booking);

        List<CommentDto> comments = itemService.getCommentsByItemId(itemId);

        return BookingMapper.toBookingDto(bookingRepository.save(booking), comments);
    }

    @Override
    public BookingDto update(Integer bookingId, Integer userId, Boolean approved) {
        userService.findUserById(userId);

        Booking booking = bookingRepository.findByIdAndItem_Owner_Id(bookingId, userId).orElseThrow(() -> throwBookingNotFoundException(
                "BookingNotFoundException: Booking with id=" + bookingId + " was not found."));

        bookingTimeValidation(booking);

        Integer bookerItemId = booking.getItem().getId();

        if ((isItemOwner(bookerItemId, userId))
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
        Integer itemId = booking.getItem().getId();
        List<CommentDto> comments = itemService.getCommentsByItemId(itemId);

        return BookingMapper.toBookingDto(bookingRepository.save(booking), comments);
    }

    @Override
    public BookingDto getBookingById(Integer bookingId, Integer userId) {
        userService.findUserById(userId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> throwBookingNotFoundException(
                "BookingNotFoundException: Booking with id=" + bookingId + " was not found."));

        Integer bookerId = booking.getBooker().getId();
        Integer bookerItemId = booking.getItem().getId();

        Integer itemId = booking.getItem().getId();
        List<CommentDto> comments = itemService.getCommentsByItemId(itemId);

        if (bookerId.equals(userId) || isItemOwner(bookerItemId, userId)) {
            return BookingMapper.toBookingDto(booking, comments);
        } else {
            log.error("BookingNotFoundException: Booking with id='{}' was not found.", bookingId);
            throw new BookingNotFoundException("Booking was not found");
        }
    }

    @Override
    public List<BookingDto> getBookingList(String state, Integer userId) {
        userService.findUserById(userId);

        BookingState listStates = stateToEnum(state);
        List<Booking> bookings;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        switch (listStates) {
            case ALL:
                bookings = bookingRepository.findByBookerId(userId, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now(), sort);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), sort);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatus(userId, Status.WAITING, sort);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatus(userId, Status.REJECTED, sort);
                break;
            default:
                bookings = new ArrayList<>();
        }

        return bookings.stream()
                .map(booking -> {
                    Integer itemId = booking.getItem().getId();
                    List<CommentDto> comments = itemService.getCommentsByItemId(itemId);
                    return BookingMapper.toBookingDto(booking, comments);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsOwner(String state, Integer userId) {
        userService.findUserById(userId);

        BookingState listStates = stateToEnum(state);
        List<Booking> bookings;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        switch (listStates) {
            case ALL:
                bookings = bookingRepository.findByItem_Owner_Id(userId, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now(), sort);
                break;
            case PAST:
                bookings = bookingRepository.findByItem_Owner_IdAndEndIsBefore(userId, LocalDateTime.now(), sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItem_Owner_IdAndStartIsAfter(userId, LocalDateTime.now(),
                        sort);
                break;
            case WAITING:
                bookings = bookingRepository.findByItem_Owner_IdAndStatus(userId, Status.WAITING, sort);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItem_Owner_IdAndStatus(userId, Status.REJECTED, sort);
                break;
            default:
                bookings = new ArrayList<>();
        }

        return bookings.stream()
                .map(booking -> {
                    Integer itemId = booking.getItem().getId();
                    List<CommentDto> comments = itemService.getCommentsByItemId(itemId);
                    return BookingMapper.toBookingDto(booking, comments);
                })
                .collect(Collectors.toList());
    }

    @Override
    public BookingShortDto getLastBooking(Integer itemId) {
        return BookingMapper.toBookingShortDto(bookingRepository.findFirstByItem_IdAndStartBeforeAndStatusNotOrderByStartDesc(itemId,
                LocalDateTime.now(), Status.REJECTED));
    }

    @Override
    public BookingShortDto getNextBooking(Integer itemId) {
        return BookingMapper.toBookingShortDto(bookingRepository.findFirstByItem_IdAndStartAfterAndStatusNotOrderByStart(
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

    private BookingNotFoundException throwBookingNotFoundException(String message) {
        log.error(message);
        throw new BookingNotFoundException(message);
    }

    private BookingState stateToEnum(String stateParam) {
        BookingState state;
        try {
            state = BookingState.valueOf(stateParam);
        } catch (IllegalArgumentException e) {
            String message = "Unknown state: UNSUPPORTED_STATUS";
            log.error(message);
            throw new UnsupportedStatusException(message);
        }
        return state;
    }
}
