package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.coment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@UtilityClass
public class ItemMapper {

    public ItemDto mapToItemDto(Item item, List<CommentDto> comments) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequestId() != null ? item.getRequestId() : null,
                null,
                null,
                comments
        );
    }

    public ItemDto toItemWithBookingDto(Item item,
                                        BookingShortDto lastBooking,
                                        BookingShortDto nextBooking,
                                        List<CommentDto> comments) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequestId() != null ? item.getRequestId() : null,
                lastBooking,
                nextBooking,
                comments);
    }

    public Item mapToItem(ItemDto itemDto, User owner) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner,
                itemDto.getRequestId() != null ? itemDto.getRequestId() : null
        );
    }
}
