package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CommentMapperTest {

    private User user;
    private Item item;
    private Comment comment;
    private CommentDto commentDto;

    @BeforeEach
    void init() {
        user = User.builder()
                .id(1L)
                .name("UserName")
                .email("UserEmail@gmail.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("ItemName")
                .description("ItemDesc")
                .available(true)
                .owner(user)
                .build();

        comment = Comment.builder()
                .id(1L)
                .text("CommentText")
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .text("CommentText")
                .itemId(1L)
                .authorName("UserName")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void toCommentDtoTest() {
        CommentDto result = CommentMapper.toCommentDto(comment);

        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getText(), result.getText());
        assertEquals(comment.getItem().getId(), result.getItemId());
        assertEquals(comment.getAuthor().getName(), result.getAuthorName());
        assertEquals(comment.getCreated(), result.getCreated());
    }

    @Test
    void toCommentTest() {
        Comment result = CommentMapper.toComment(commentDto, item, user);

        assertNotNull(result);
        assertEquals(commentDto.getId(), result.getId());
        assertEquals(commentDto.getText(), result.getText());
        assertEquals(commentDto.getItemId(), result.getItem().getId());
        assertEquals(commentDto.getAuthorName(), result.getAuthor().getName());
        assertEquals(commentDto.getCreated(), result.getCreated());
    }
}
