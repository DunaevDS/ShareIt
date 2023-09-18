package ru.practicum.shareit.item;

import java.util.List;

public interface ItemStorage {
    Item create(Item item);

    Item update(Item item);

    Item delete(int userId);

    List<Item> getItemsByOwner(int ownerId);

    List<Item> getItemsBySearch(String text);

    void deleteAllItems(int ownerId);

    Item getItemById(int itemId);

    boolean existsById(int itemId);

    boolean existsItemById(int itemId, int ownerId);
}
