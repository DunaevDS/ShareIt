package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestDto itemRequestDto, Integer requesterId);

    ItemRequestDto getItemRequestById(Integer itemRequestId, Integer userId);

    List<ItemRequestDto> getOwnItemRequests(Integer requesterId);

    List<ItemRequestDto> getAllItemRequests(Integer userId, Integer from, Integer size);
}
