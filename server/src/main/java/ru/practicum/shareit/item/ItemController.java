package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.coment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final String owner = "X-Sharer-User-Id";
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Integer itemId,
                               @RequestHeader(owner) Integer ownerId,
                               HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString()
        );

        log.info("ownerId = " + ownerId);
        log.info("itemId = " + itemId);

        return itemService.getItemById(itemId, ownerId);
    }

    @ResponseBody
    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto itemDto,
                          @RequestHeader(owner) Integer ownerId,
                          HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}', Owner ID = '{}'",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                request.getHeader(owner)
        );

        log.info("itemDto = " + itemDto);
        log.info("ownerId = " + ownerId);

        return itemService.create(itemDto, ownerId);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader(owner) Integer ownerId,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                         @Positive @RequestParam(defaultValue = "10") Integer size,
                                         HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString()
        );

        System.out.println("ownerId = " + ownerId);
        System.out.println("from = " + from);
        System.out.println("size = " + size);

        return itemService.getItemsByOwner(ownerId, from, size);
    }

    @ResponseBody
    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @PathVariable Integer itemId,
                          @RequestHeader(owner) Integer ownerId,
                          HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString()
        );
        itemDto.setId(itemId);
        return itemService.update(itemDto, ownerId);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Integer itemId,
                       @RequestHeader(owner) Integer ownerId,
                       HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString()
        );

        itemService.delete(itemId, ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySearchQuery(@RequestParam String text,
                                               @PositiveOrZero@RequestParam(defaultValue = "0") Integer from,
                                               @Positive @RequestParam(defaultValue = "10") Integer size,
                                               HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString()
        );

        return itemService.getItemsBySearchQuery(text, from, size);
    }

    @ResponseBody
    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Valid @RequestBody CommentDto commentDto, @RequestHeader(owner) Integer userId,
                                    @PathVariable Integer itemId,
                                    HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString()
        );

        return itemService.createComment(commentDto.getText(), itemId, userId);
    }
}
