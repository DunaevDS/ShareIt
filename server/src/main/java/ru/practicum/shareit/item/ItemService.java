package ru.practicum.shareit.item;

import ru.practicum.shareit.item.coment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Integer ownerId);

    List<ItemDto> getItemsByOwner(Integer ownerId, Integer from, Integer size);

    ItemDto getItemById(Integer id, Integer userId);

    ItemDto update(ItemDto itemDto, Integer ownerId);

    void delete(Integer itemId, Integer ownerId);

    List<ItemDto> getItemsBySearchQuery(String text, Integer from, Integer size);

    CommentDto createComment(String commentDtoText, Integer itemId, Integer userId);

    List<CommentDto> getCommentsByItemId(Integer itemId);
}
