package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Component("ItemStorage")
@Slf4j
public class ItemStorageImpl implements ItemStorage {

    public Map<Integer, Item> items = new HashMap<>();
    private int id;

    @Override
    public Item create(Item item) {
        item.setId(generateNewId());
        items.put(item.getId(), item);

        log.info("New item was created: id='{}', name = '{}'",
                item.getId(), item.getName());

        return item;
    }

    @Override
    public Item update(Item item) {
        if (item.getName() == null) {
            item.setName(items.get(item.getId()).getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(items.get(item.getId()).getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(items.get(item.getId()).getAvailable());
        }
        items.put(item.getId(), item);

        log.info("Item info was updated: id='{}', name = '{}'",
                item.getId(), item.getName());

        return item;
    }

    @Override
    public Item delete(int itemId) {
        log.info("Item with id='{}' was removed",
                itemId);

        return items.remove(itemId);
    }

    @Override
    public List<Item> getItemsByOwner(int ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId() == ownerId)
                .collect(toList());
    }

    @Override
    public void deleteItemsByOwner(int ownerId) {
        List<Integer> deleteIds = items.values().stream()
                .filter(item -> item.getOwnerId() == ownerId)
                .map(Item::getId)
                .collect(toList());

        for (int deleteId : deleteIds) {
            items.remove(deleteId);
        }
    }

    @Override
    public Item getItemById(int itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getItemsBySearch(String text) {
        List<Item> searchItems = new ArrayList<>();
        if (!text.isBlank()) {
            searchItems = items.values().stream()
                    .filter(Item::getAvailable)
                    .filter(item -> item.getName().toLowerCase().contains(text)
                            || item.getDescription().toLowerCase().contains(text))
                    .collect(toList());
        }
        return searchItems;
    }

    @Override
    public boolean existsById(int userId) {
        return items.containsKey(userId);
    }

    @Override
    public boolean existsItemById(int itemId, int ownerId) {
        Item item = getItemById(itemId);

        return item.getOwnerId() == ownerId;
    }

    private int generateNewId() {
        return ++id;
    }
}
