package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> jackson;

    @Test
    void itemRequestDtoTest() throws IOException {
        LocalDateTime created = LocalDateTime.of(2025, 3, 20, 12, 0);

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("ItemName")
                .description("ItemDesc")
                .available(true)
                .requestId(1L)
                .build();

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("RequestDesc")
                .created(created)
                .items(List.of(itemDto))
                .build();

        JsonContent<ItemRequestDto> json = jackson.write(itemRequestDto);
        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.description").isEqualTo("RequestDesc");
        assertThat(json).extractingJsonPathStringValue("$.created").isEqualTo("2025-03-20T12:00:00");
        assertThat(json).extractingJsonPathArrayValue("$.items").hasSize(1);
    }
}
