package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> jackson;

    @Test
    void commentDtoTest() throws IOException {
        LocalDateTime created = LocalDateTime.of(2025, 3, 20, 12, 0);

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Text")
                .itemId(2L)
                .authorName("UserName")
                .created(created)
                .build();

        JsonContent<CommentDto> json = jackson.write(commentDto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.text").isEqualTo("Text");
        assertThat(json).extractingJsonPathNumberValue("$.itemId").isEqualTo(2);
        assertThat(json).extractingJsonPathStringValue("$.authorName").isEqualTo("UserName");
        assertThat(json).extractingJsonPathStringValue("$.created").isEqualTo("2025-03-20T12:00:00");
    }
}
