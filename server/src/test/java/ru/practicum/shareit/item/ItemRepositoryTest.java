package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;

    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private Item item3;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;
    private ItemRequest itemRequest3;

    @BeforeEach
    void init() {
        user1 = User.builder()
                .name("User1")
                .email("User1@gmai.com")
                .build();
        user2 = User.builder()
                .name("User2")
                .email("User2@gmai.com")
                .build();

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        itemRequest1 = ItemRequest.builder()
                .description("itemRequest1")
                .requester(user1)
                .created(LocalDateTime.now())
                .build();

        itemRequest2 = ItemRequest.builder()
                .description("itemRequest2")
                .requester(user2)
                .created(LocalDateTime.now())
                .build();

        itemRequest3 = ItemRequest.builder()
                .description("itemRequest3")
                .requester(user2)
                .created(LocalDateTime.now())
                .build();

        itemRequest1 = itemRequestRepository.save(itemRequest1);
        itemRequest2 = itemRequestRepository.save(itemRequest2);
        itemRequest3 = itemRequestRepository.save(itemRequest3);

        item1 = Item.builder()
                .name("ItemName1")
                .description("ItemDesc1")
                .available(true)
                .owner(user1)
                .itemRequest(itemRequest1)
                .build();

        item2 = Item.builder()
                .name("ItemName2")
                .description("ItemDesc2")
                .available(true)
                .owner(user2)
                .itemRequest(itemRequest2)
                .build();

        item3 = Item.builder()
                .name("ItemName3")
                .description("ItemDesc3")
                .available(true)
                .owner(user2)
                .itemRequest(itemRequest3)
                .build();

        item1 = itemRepository.save(item1);
        item2 = itemRepository.save(item2);
        item3 = itemRepository.save(item3);
    }

    @Test
    void searchByTextTest() {
        List<Item> result = itemRepository.searchByText("ItemName1");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ItemName1", result.get(0).getName());
    }

    @Test
    void searchByTextWithEmptyResultTest() {
        List<Item> result = itemRepository.searchByText("NonExistingItem");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByOwnerIdTest() {
        List<Item> result = itemRepository.findAllByOwnerId(user1.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ItemName1", result.get(0).getName());
    }

    @Test
    void findAllByOwnerIdWithEmptyResultTest() {
        List<Item> result = itemRepository.findAllByOwnerId(10L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByItemRequestIdTest() {
        List<Item> result = itemRepository.findAllByItemRequestId(itemRequest1.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ItemName1", result.get(0).getName());
    }

    @Test
    void findAllByItemRequestIdWithEmptyResultTest() {
        List<Item> result = itemRepository.findAllByItemRequestId(10L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByItemRequestIdInTest() {
        List<Long> requestIds = List.of(itemRequest1.getId(), itemRequest2.getId());
        List<Item> result = itemRepository.findAllByItemRequestIdIn(requestIds);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(item -> item.getName().equals("ItemName1")));
        assertTrue(result.stream().anyMatch(item -> item.getName().equals("ItemName2")));
    }

    @Test
    void findAllByItemRequestIdInWithEmptyResultTest() {
        List<Long> requestIds = List.of(10L, 20L);
        List<Item> result = itemRepository.findAllByItemRequestIdIn(requestIds);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }



}
