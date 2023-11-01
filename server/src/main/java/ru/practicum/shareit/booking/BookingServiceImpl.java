package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.PostBookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.coment.CommentMapper;
import ru.practicum.shareit.item.coment.CommentRepository;
import ru.practicum.shareit.item.coment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Pagination;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserService userService,
                              ItemRepository itemRepository,
                              CommentRepository commentRepository) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.itemRepository = itemRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public BookingDto create(PostBookingDto postBookingDto, Integer bookerId) {

        User user = UserMapper.mapToUser(userService.findUserById(bookerId));

        log.info("Start post method in BookingServiceImpl...");
        log.info("user = " + user);
        log.info("postBookingDto = " + postBookingDto);

        Integer itemId = postBookingDto.getItemId();
        Item item = itemRepository.findById(itemId).orElseThrow(() -> throwNotFoundException(
                "NotFoundException: Item with id= " + itemId + " was not found."));
        boolean isAvailable = item.getAvailable();

        Integer bookingOwnerId = item.getOwner().getId();

        if (bookerId.equals(bookingOwnerId)) {
            log.error("BadRequestException: Item with id='{}' can not be booked by owner", itemId);
            throw new NotFoundException("Item with id= " + itemId +
                    " can not be booked by owner");
        }

        if (!isAvailable) {
            log.error("ConflictException: Item with id='{}' can not be booked.", itemId);
            throw new BadRequestException("Item with id = " + itemId + " can not be booked");
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

        List<CommentDto> comments = getCommentsByItemId(itemId);


        BookingDto savedBooking = BookingMapper.toBookingDto(bookingRepository.save(booking), comments);

        log.info("savedBooking = " + savedBooking);
        log.info("end of post method in BookingServiceImpl..");
        return savedBooking;
    }

    @Override
    public BookingDto update(Integer bookingId, Integer userId, Boolean approved) {
        log.info("Start post method in BookingServiceImpl...");

        userService.findUserById(userId);


        Booking booking = bookingRepository.findByIdAndItem_Owner_Id(bookingId, userId).orElseThrow(() -> throwNotFoundException(
                "NotFoundException: Booking with id=" + bookingId + " was not found."));

        bookingTimeValidation(booking);

        Integer bookerItemId = booking.getItem().getId();

        if ((isItemOwner(bookerItemId, userId))
                && (!booking.getStatus().equals(Status.CANCELED))) {
            if (!booking.getStatus().equals(Status.WAITING)) {
                log.error("BadRequestException: Booking was made already");
                throw new BadRequestException("Booking was made already");
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
                log.error("ConflictException: booking with id='{}' was cancelled", bookingId);
                throw new ConflictException("Booking with id= " + bookingId + " was cancelled");
            } else {
                log.error("BadRequestException: user with id='{}' is not an owner. " +
                        "Booking can be approved only by an owner", userId);
                throw new BadRequestException("booking can be approved only by an owner");
            }
        }
        Integer itemId = booking.getItem().getId();
        List<CommentDto> comments = getCommentsByItemId(itemId);

        BookingDto savedBooking = BookingMapper.toBookingDto(bookingRepository.save(booking), comments);

        log.info("savedBooking = " + savedBooking);
        log.info("end of patch method in BookingServiceImpl..");
        return savedBooking;
    }

    @Override
    public BookingDto getBookingById(Integer bookingId, Integer userId) {
        userService.findUserById(userId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> throwNotFoundException(
                "NotFoundException: Booking with id=" + bookingId + " was not found."));

        Integer bookerId = booking.getBooker().getId();
        Integer bookerItemId = booking.getItem().getId();

        Integer itemId = booking.getItem().getId();
        List<CommentDto> comments = getCommentsByItemId(itemId);

        if (bookerId.equals(userId) || isItemOwner(bookerItemId, userId)) {
            return BookingMapper.toBookingDto(booking, comments);
        } else {
            log.error("NotFoundException: Booking with id='{}' was not found.", bookingId);
            throw new NotFoundException("Booking was not found");
        }
    }

    @Override
    public List<BookingDto> getBookingList(String state, Integer userId, Integer from, Integer size) {
        userService.findUserById(userId);

        List<BookingDto> listBookingDto = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pagination pager = new Pagination(from, size);
        Pageable pageable = PageRequest.of(pager.getIndex(), pager.getPageSize(), sort);

        Page<Booking> page = getPageBookings(state, userId, pageable);
        listBookingDto.addAll(page.stream()
                .map(booking -> {
                    Integer itemId = booking.getItem().getId();
                    List<CommentDto> comments = getCommentsByItemId(itemId);
                    return BookingMapper.toBookingDto(booking, comments);
                })
                .collect(Collectors.toList()));

        return listBookingDto;
    }

    private Page<Booking> getPageBookings(String state, Integer userId, Pageable pageable) {
        Page<Booking> page;
        BookingState listStates = stateToEnum(state);
        switch (listStates) {
            case ALL:
                page = bookingRepository.findByBookerId(userId, pageable);
                break;
            case CURRENT:
                page = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now(), pageable);
                break;
            case PAST:
                page = bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                page = bookingRepository.findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), pageable);
                break;
            case WAITING:
                page = bookingRepository.findByBookerIdAndStatus(userId, Status.WAITING, pageable);
                break;
            case REJECTED:
                page = bookingRepository.findByBookerIdAndStatus(userId, Status.REJECTED, pageable);
                break;
            default:
                throw new ConflictException("Unknown state: " + state);
        }
        return page;
    }

    @Override
    public List<BookingDto> getBookingsOwner(String state, Integer userId, Integer from, Integer size) {
        userService.findUserById(userId);

        List<BookingDto> listBookingDto = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pagination pager = new Pagination(from, size);
        Pageable pageable = PageRequest.of(pager.getIndex(), pager.getPageSize(), sort);

        Page<Booking> page = getPageBookingsOwner(state, userId, pageable);
        listBookingDto.addAll(page.stream()
                .map(booking -> {
                    Integer itemId = booking.getItem().getId();
                    List<CommentDto> comments = getCommentsByItemId(itemId);
                    return BookingMapper.toBookingDto(booking, comments);
                })
                .collect(Collectors.toList()));

        return listBookingDto;
    }

    private Page<Booking> getPageBookingsOwner(String state, Integer userId, Pageable pageable) {
        Page<Booking> page;
        BookingState listStates = stateToEnum(state);
        switch (listStates) {
            case ALL:
                page = bookingRepository.findByItem_Owner_Id(userId, pageable);
                break;
            case CURRENT:
                page = bookingRepository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now(), pageable);
                break;
            case PAST:
                page = bookingRepository.findByItem_Owner_IdAndEndIsBefore(userId, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                page = bookingRepository.findByItem_Owner_IdAndStartIsAfter(userId, LocalDateTime.now(),
                        pageable);
                break;
            case WAITING:
                page = bookingRepository.findByItem_Owner_IdAndStatus(userId, Status.WAITING, pageable);
                break;
            case REJECTED:
                page = bookingRepository.findByItem_Owner_IdAndStatus(userId, Status.REJECTED, pageable);
                break;
            default:
                throw new ConflictException("Unknown state: " + state);
        }
        return page;
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
        List<ItemDto> items = itemRepository.findByOwnerId(userId, Pageable.unpaged()).stream()
                .map(item -> {
                    Integer id = item.getId();
                    List<CommentDto> comments = getCommentsByItemId(id);
                    BookingShortDto lastBooking = getLastBooking(id);
                    BookingShortDto nextBooking = getNextBooking(id);
                    return ItemMapper.toItemWithBookingDto(item, lastBooking, nextBooking, comments);
                })
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(toList());
        return items.stream()
                .anyMatch(item -> item.getId().equals(itemId));
    }

    private void bookingTimeValidation(Booking booking) {
        if (booking.getStart() == null
                || booking.getEnd() == null
                || booking.getEnd().isBefore(booking.getStart())
                || booking.getStart().isAfter(booking.getEnd())
                || booking.getStart().equals(booking.getEnd())) {
            log.error("BadRequestException");
            throw new BadRequestException("BadRequestException");
        }
    }

    private NotFoundException throwNotFoundException(String message) {
        log.error(message);
        throw new NotFoundException(message);
    }

    private BookingState stateToEnum(String stateParam) {
        BookingState state;
        try {
            state = BookingState.valueOf(stateParam);
        } catch (IllegalArgumentException e) {
            String message = "Unknown state: UNSUPPORTED_STATUS";
            log.error(message);
            throw new InternalServerErrorException(message);
        }
        return state;
    }

    private List<CommentDto> getCommentsByItemId(Integer itemId) {
        return commentRepository.findAllByItem_Id(itemId,
                        Sort.by(Sort.Direction.DESC, "created")).stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(toList());
    }
}
