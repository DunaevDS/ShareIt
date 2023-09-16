package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final ItemMapper mapper;

    @Autowired
    public ItemServiceImpl(@Qualifier("ItemStorage") ItemStorage itemStorage, ItemMapper itemMapper) {
        this.itemStorage = itemStorage;
        this.mapper = itemMapper;
    }

    public ItemDto create(ItemDto itemDto, int ownerId) {
        if (itemDto == null) {
            log.error("EmptyObjectException: Item is null.");
            throw new UserNotFoundException("Item was not provided");
        }
        validation(itemDto);

        return mapper.toItemDto(
                itemStorage.create(mapper.toItem(itemDto, ownerId))
        );
    }

    public List<ItemDto> getItemsByOwner(int ownerId) {
        return itemStorage.getItemsByOwner(ownerId).stream()
                .map(mapper::toItemDto)
                .collect(toList());
    }

    public ItemDto getItemById(int itemId) {
        if (!itemStorage.existsById(itemId)) {
            log.error("NotFoundException: Item with id='{}' was not found.", itemId);
            throw new UserNotFoundException("Item was not found.");
        }

        return mapper.toItemDto(itemStorage.getItemById(itemId));
    }

    public ItemDto update(ItemDto itemDto, int ownerId, int itemId) {
        if (itemDto == null) {
            log.error("EmptyObjectException:  Item is null.");
            throw new UserNotFoundException("Item was not provided");
        }
        if (!itemStorage.existsById(itemId)) {
            log.error("NotFoundException: User with id='{}' was not found.", itemId);
            throw new UserNotFoundException("User was not found.");
        }

        if (!itemStorage.existsItemById(itemId, ownerId)) {
            log.error("NotFoundException: User with id='{}' dont have item with id='{}'", ownerId, itemId);
            throw new ItemNotFoundException("Item was not found!");
        }

        itemDto.setId(itemId);

        return mapper.toItemDto(
                itemStorage.update(mapper.toItem(itemDto, ownerId))
        );
    }

    public ItemDto delete(int itemId, int ownerId) {
        if (!itemStorage.existsItemById(itemId, ownerId)) {
            log.error("NotFoundException: User with id={} dont have item with id={}", ownerId, itemId);
            throw new ItemNotFoundException("Item was not found!");
        }

        return mapper.toItemDto(itemStorage.delete(itemId));
    }

    public void deleteItemsByOwner(int ownerId) {
        itemStorage.deleteItemsByOwner(ownerId);
    }

    public List<ItemDto> getItemsBySearchQuery(String text) {
        return itemStorage.getItemsBySearch(text.toLowerCase()).stream()
                .map(mapper::toItemDto)
                .collect(toList());
    }

    private void validation(ItemDto itemDto) {
        if ((itemDto.getName().isBlank()) || itemDto.getName().contains(" ")) {
            log.error("ValidationException: incorrect name");
            throw new ValidationException("Incorrect name " + itemDto.getName());
        }
        if (itemDto.getDescription().isBlank()) {
            log.error("ValidationException: incorrect description");
            throw new ValidationException("Incorrect description " + itemDto.getDescription());
        }
        if (itemDto.getAvailable() == null) {
            log.error("ValidationException: incorrect availability");
            throw new ValidationException("Incorrect availability " + itemDto.getAvailable());
        }
    }
}
