package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public List<ItemDto> getItems() {
        return itemStorage.getItems().stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        userStorage.checkIfUserExists(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwnerId(userId);
        return ItemMapper.toItemDto(itemStorage.createItem(item));
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto newItemDto) {
        userStorage.checkIfUserExists(userId);
        itemStorage.checkIfItemExists(itemId);
        checkOwner(userId, itemId);
        Item item = ItemMapper.toItem(newItemDto);
        item.setId(itemId);
        item.setOwnerId(userId);
        return ItemMapper.toItemDto(itemStorage.updateItem(item));
    }

    @Override
    public ItemDto getItemById(Long id) {
        return ItemMapper.toItemDto(itemStorage.getItemById(id));
    }

    @Override
    public List<ItemDto> getItemsByUserId(Long id) {
        userStorage.checkIfUserExists(id);
        return itemStorage.getItemsByUserId(id).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsByText(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemStorage.getItemsByText(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void checkOwner(long userId, long itemId) {
        Item item = itemStorage.getItemById(itemId);
        if (!item.getOwnerId().equals(userId)) {
            log.warn("Доступ запрещен: User с id {} не владеет Item с id {}", userId, itemId);
            throw new AccessDeniedException("Доступ запрещен: User c id " + userId + " не владеет Item с id " + itemId);
        }
    }
}
