package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public List<Item> getItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Item createItem(Item item) {
        item.setId(getNextItemId());
        items.put(item.getId(), item);
        log.info("Item {}  c id {} успешно добавлен", item.getName(), item.getId());
        return item;
    }

    @Override
    public Item updateItem(Item newItem) {
        Item oldItem = items.get(newItem.getId());
        if (newItem.getName() != null) {
            oldItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            oldItem.setDescription(newItem.getDescription());
        }

        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }
        return oldItem;
    }

    @Override
    public Item getItemById(Long id) {
        checkIfItemExists(id);
        return items.get(id);
    }

    @Override
    public void checkIfItemExists(Long id) {
        if (!items.containsKey(id)) {
            log.error("Item с id {} не найден", id);
            throw new NotFoundException("Item с id " + id + " не найден");
        }
    }

    @Override
    public List<Item> getItemsByUserId(Long id) {
        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(id))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getItemsByText(String text) {
        List<Item> itemList = new ArrayList<>();
        for (Item item : items.values()) {
            if ((item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                    item.getAvailable()) {
                itemList.add(item);
            }
        }
        return itemList;
    }

    private long getNextItemId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
