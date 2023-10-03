package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.service.ConnectingService;

//не смог придумать как оставить утилитарным классом и привязать connecting service
@Component
public class ItemMapper {
    private final ConnectingService connectingService;

    @Autowired
    @Lazy
    public ItemMapper(ConnectingService connectingService) {
        this.connectingService = connectingService;
    }

    public ItemDto mapToItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequestId() != null ? item.getRequestId() : null,
                null,
                null,
                connectingService.getCommentsByItemId(item.getId())
        );
    }

    public ItemDto toItemWithBookingDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequestId() != null ? item.getRequestId() : null,
                connectingService.getLastBooking(item.getId()),
                connectingService.getNextBooking(item.getId()),
                connectingService.getCommentsByItemId(item.getId()));
    }

    public Item mapToItem(ItemDto itemDto, Integer ownerId) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                connectingService.findUserById(ownerId),
                itemDto.getRequestId() != null ? itemDto.getRequestId() : null
        );
    }
}
