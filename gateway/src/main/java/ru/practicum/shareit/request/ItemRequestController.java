package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @ResponseBody
    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid ItemRequestDto itemRequestDto,
                                         @RequestHeader(USER_ID) Integer requesterId) {
        log.info("Получен POST-запрос к эндпоинту: '/requests' " +
                "на создание запроса вещи от пользователя с ID={}", requesterId);
        return itemRequestClient.create(itemRequestDto, requesterId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable("requestId") Integer itemRequestId,
                                                     @RequestHeader(USER_ID) Integer userId) {
        log.info("Получен GET-запрос к эндпоинту: '/requests' на получение запроса с ID={}", itemRequestId);
        return itemRequestClient.getItemRequestById(userId, itemRequestId);
    }


    @GetMapping
    public ResponseEntity<Object> getOwnItemRequests(@RequestHeader(USER_ID) Integer userId) {
        log.info("Получен GET-запрос к эндпоинту: '/requests' на получение запросов пользователя ID={}",
                userId);
        return itemRequestClient.getOwnItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(USER_ID) Integer userId,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                     Integer from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получен GET-запрос к эндпоинту: '/requests/all' от пользователя с ID={} на получение всех запросов",
                userId);
        return itemRequestClient.getAllItemRequests(userId, from, size);
    }
}
