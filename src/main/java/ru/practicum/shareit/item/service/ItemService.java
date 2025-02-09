package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> getItems();

    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto newItemDto);

    ItemDto getItemById(Long id);

    List<ItemDto> getItemsByUserId(Long id);

    List<ItemDto> getItemsByText(String text);
}
