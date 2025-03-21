package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;
    private ItemRequest itemRequest3;

    @BeforeEach
    void init() {
        user1 = User.builder()
                .name("User1")
                .email("user1@gmail.com")
                .build();
        user2 = User.builder()
                .name("User2")
                .email("user2@gmail.com")
                .build();

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        itemRequest1 = ItemRequest.builder()
                .description("Request1")
                .requester(user1)
                .created(LocalDateTime.now().minusDays(2))
                .build();

        itemRequest2 = ItemRequest.builder()
                .description("Request2")
                .requester(user1)
                .created(LocalDateTime.now().minusDays(1))
                .build();

        itemRequest3 = ItemRequest.builder()
                .description("Request3")
                .requester(user2)
                .created(LocalDateTime.now())
                .build();

        itemRequest1 = itemRequestRepository.save(itemRequest1);
        itemRequest2 = itemRequestRepository.save(itemRequest2);
        itemRequest3 = itemRequestRepository.save(itemRequest3);
    }

    @Test
    void findAllByRequesterIdOrderByCreatedAscTest() {
        List<ItemRequest> result = itemRequestRepository.findAllByRequesterIdOrderByCreatedAsc(user1.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(itemRequest1.getId(), result.get(0).getId());
        assertEquals(itemRequest2.getId(), result.get(1).getId());
    }

    @Test
    void findAllByRequesterIdOrderByCreatedAscWithEmptyResultTest() {
        List<ItemRequest> result = itemRequestRepository.findAllByRequesterIdOrderByCreatedAsc(999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByRequesterIdNotOrderByCreatedDescTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ItemRequest> result = itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(user1.getId(), pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(itemRequest3.getId(), result.getContent().get(0).getId());
    }

    @Test
    void findAllByRequesterIdNotOrderByCreatedDescWithEmptyResultTest() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ItemRequest> result = itemRequestRepository
                .findAllByRequesterIdNotOrderByCreatedDesc(user2.getId(), pageable)
                .getContent();

        assertNotNull(result);
        assertEquals(2, result.size());

        for (ItemRequest request : result) {
            assertNotEquals(user2.getId(), request.getRequester().getId());
        }
    }
}
