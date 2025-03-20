package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.*;

public class ItemMapperTest {

    private User owner;
    private Item item;
    private ItemDto itemDto;
    private ItemWithBookingDto itemWithBookingDto;
    private ItemRequest itemRequest;

    @BeforeEach
    void init() {
        owner = User.builder()
                .id(1L)
                .name("OwnerName")
                .email("OwnerEmail@gmail.com")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("RequestDescription")
                .requester(owner)
                .build();

        item = Item.builder()
                .id(1L)
                .name("ItemName")
                .description("ItemDesc")
                .available(true)
                .owner(owner)
                .itemRequest(itemRequest)
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("ItemName")
                .description("ItemDesc")
                .available(true)
                .requestId(1L)
                .build();

        itemWithBookingDto = ItemWithBookingDto.builder()
                .id(1L)
                .name("ItemName")
                .description("ItemDesc")
                .available(true)
                .build();
    }

    @Test
    void toItemDtoTest() {
        ItemDto result = ItemMapper.toItemDto(item);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertEquals(item.getItemRequest().getId(), result.getRequestId());
    }

    @Test
    void toItemDtoWithNullRequestTest() {
        item.setItemRequest(null);
        ItemDto result = ItemMapper.toItemDto(item);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertNull(result.getRequestId());
    }

    @Test
    void toItemWithBookingDtoTest() {
        ItemWithBookingDto result = ItemMapper.toItemWithBookingDto(item);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
    }

    @Test
    void toItemTest() {
        Item result = ItemMapper.toItem(itemDto);

        assertNotNull(result);
        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getAvailable(), result.getAvailable());
    }
}
