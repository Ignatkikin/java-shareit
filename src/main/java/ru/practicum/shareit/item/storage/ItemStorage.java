package ru.practicum.shareit.item.storage;


import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    List<Item> getItems();

    Item createItem(Item item);

    Item updateItem(Item newItem);

    Item getItemById(Long id);

    void checkIfItemExists(Long id);

    List<Item> getItemsByUserId(Long id);

    List<Item> getItemsByText(String text);
}
