package ru.practicum.shareit.item;

import ru.practicum.shareit.item.coment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Integer ownerId);

    List<ItemDto> getItemsByOwner(Integer ownerId);

    ItemDto getItemById(Integer id, Integer userId);

    Item findItemById(Integer itemId);

    ItemDto update(ItemDto itemDto, Integer ownerId);

    void delete(Integer itemId, Integer ownerId);

    List<ItemDto> getItemsBySearchQuery(String text);

    CommentDto createComment(String commentDtoText, Integer itemId, Integer userId);

    List<CommentDto> getCommentsByItemId(Integer itemId);
}
