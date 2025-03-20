package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;

    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private Comment comment;

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

        item1 = Item.builder()
                .name("ItemName1")
                .description("ItemDesc1")
                .available(true)
                .owner(user1)
                .build();

        item2 = Item.builder()
                .name("ItemName2")
                .description("ItemDesc2")
                .available(true)
                .owner(user2)
                .build();

        item1 = itemRepository.save(item1);
        item2 = itemRepository.save(item2);

        comment = Comment.builder()
                .text("CommentText")
                .item(item1)
                .author(user2)
                .created(LocalDateTime.now())
                .build();

        comment = commentRepository.save(comment);
    }

    @Test
    void findByItemIdTest() {
        List<Comment> result = commentRepository.findByItemId(item1.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CommentText", result.get(0).getText());
        assertEquals(item1.getId(), result.get(0).getItem().getId());
        assertEquals(user2.getId(), result.get(0).getAuthor().getId());
    }

    @Test
    void findByItemIdWithEmptyResultTest() {
        List<Comment> result = commentRepository.findByItemId(999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByItemInTest() {
        List<Item> items = List.of(item1, item2);
        List<Comment> result = commentRepository.findByItemIn(items);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CommentText", result.get(0).getText());
    }

    @Test
    void findByItemInWithEmptyListTest() {
        List<Item> items = List.of();
        List<Comment> result = commentRepository.findByItemIn(items);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByItemInWithNoMatchingCommentsTest() {
        List<Item> items = List.of(item2);
        List<Comment> result = commentRepository.findByItemIn(items);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}
