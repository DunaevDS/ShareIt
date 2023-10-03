package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.PostBookingDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String booker = "X-Sharer-User-Id";
    private final BookingService service;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.service = bookingService;
    }

    @ResponseBody
    @PostMapping
    public BookingDto create(@Valid @RequestBody PostBookingDto postBookingDto,
                             @RequestHeader(booker) Integer bookerId,
                             HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString()
        );

        return service.create(postBookingDto, bookerId);
    }

    @ResponseBody
    @PatchMapping("/{bookingId}")
    public BookingDto update(@PathVariable Integer bookingId,
                             @RequestHeader(booker) Integer userId,
                             @RequestParam Boolean approved,
                             HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}', booker ID = '{}",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                request.getHeader(booker)
        );

        return service.update(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Integer bookingId,
                                     @RequestHeader(booker) Integer userId,
                                     HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}', booker ID = '{}",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                request.getHeader(booker)
        );

        return service.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookings(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                        @RequestHeader(booker) Integer userId,
                                        HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}', booker ID = '{}",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                request.getHeader(booker)
        );

        return service.getBookingList(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsOwner(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                             @RequestHeader(booker) Integer userId,
                                             HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}', booker ID = '{}",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                request.getHeader(booker)
        );

        return service.getBookingsOwner(state, userId);
    }
}
