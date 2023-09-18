package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, int ownerId);

    List<ItemDto> getItemsByOwner(int ownerId);

    ItemDto getItemById(int id);

    ItemDto update(ItemDto itemDto, int ownerId);

    ItemDto delete(int itemId, int ownerId);

    List<ItemDto> getItemsBySearchQuery(String text);
}
