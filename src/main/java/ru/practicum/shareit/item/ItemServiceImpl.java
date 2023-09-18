package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    @Autowired
    public ItemServiceImpl(@Qualifier("ItemStorage") ItemStorage itemStorage, UserService userService) {
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    @Override
    public ItemDto create(ItemDto itemDto, int ownerId) {
        if (itemDto == null) {
            log.error("EmptyObjectException: Item is null.");
            throw new ItemNotFoundException("Item was not provided");
        }
        if (userService.existsById(ownerId)) {
            validation(itemDto);

            return ItemMapper.toItemDto(
                    itemStorage.create(ItemMapper.toItem(itemDto, ownerId))
            );
        } else {
            log.error("NotFoundException: User with id='{}' was not found.", ownerId);
            throw new UserNotFoundException("User with ID = " + ownerId + " was not found.");
        }
    }

    @Override
    public List<ItemDto> getItemsByOwner(int ownerId) {
        return itemStorage.getItemsByOwner(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }

    @Override
    public ItemDto getItemById(int itemId) {
        Item item = itemStorage.getItemById(itemId);
        if (item == null) {
            log.error("NotFoundException: Item with id='{}' was not found.", itemId);
            throw new ItemNotFoundException("Item was not found");
        }

        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(ItemDto itemDto, int ownerId) {
        if (itemDto == null) {
            log.error("EmptyObjectException:  Item is null.");
            throw new ItemNotFoundException("Item was not provided");
        }
        if (!itemStorage.existsById(itemDto.getId())) {
            log.error("NotFoundException: Item with id='{}' was not found.", itemDto.getId());
            throw new ItemNotFoundException("Item was not found");
        }

        if (!itemStorage.existsItemById(itemDto.getId(), ownerId)) {
            log.error("NotFoundException: User with id='{}' dont have item with id='{}'", ownerId, itemDto.getId());
            throw new ItemNotFoundException("User with id= " + ownerId + " dont have item with id= " + itemDto.getId());
        }
        if (userService.existsById(ownerId)) {
            itemDto.setId(itemDto.getId());

            return ItemMapper.toItemDto(
                    itemStorage.update(ItemMapper.toItem(itemDto, ownerId))
            );
        } else {
            log.error("NotFoundException: User with id='{}' was not found.", ownerId);
            throw new UserNotFoundException("User with ID = " + ownerId + " was not found.");
        }
    }

    @Override
    public ItemDto delete(int itemId, int ownerId) {
        if (!itemStorage.existsItemById(itemId, ownerId)) {
            log.error("NotFoundException: User with id={} dont have item with id={}", ownerId, itemId);
            throw new ItemNotFoundException("Item was not found!");
        }

        return ItemMapper.toItemDto(itemStorage.delete(itemId));
    }

    @Override
    public List<ItemDto> getItemsBySearchQuery(String text) {
        return itemStorage.getItemsBySearch(text.toLowerCase()).stream()
                .map(ItemMapper::toItemDto)
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
