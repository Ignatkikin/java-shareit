package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemWithBookingDtoTest {

    @Autowired
    private JacksonTester<ItemWithBookingDto> jackson;

    @Test
    void itemWithBookingDtoTest() throws IOException {
        BookingDtoForItem lastBooking = BookingDtoForItem.builder()
                .id(1L)
                .bookerId(10L)
                .start(LocalDateTime.of(2023, 3, 10, 12, 0))
                .end(LocalDateTime.of(2023, 3, 11, 12, 0))
                .build();

        BookingDtoForItem nextBooking = BookingDtoForItem.builder()
                .id(2L)
                .bookerId(20L)
                .start(LocalDateTime.of(2023, 3, 12, 12, 0))
                .end(LocalDateTime.of(2023, 3, 13, 12, 0))
                .build();

        CommentDto comment = CommentDto.builder()
                .id(1L)
                .text("Text")
                .itemId(1L)
                .authorName("UserName")
                .created(LocalDateTime.of(2023, 3, 14, 12, 0))
                .build();

        ItemWithBookingDto itemWithBookingDto = ItemWithBookingDto.builder()
                .id(1L)
                .name("ItemName")
                .description("ItemDesc")
                .available(true)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(List.of(comment))
                .build();

        JsonContent<ItemWithBookingDto> json = jackson.write(itemWithBookingDto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("ItemName");
        assertThat(json).extractingJsonPathStringValue("$.description").isEqualTo("ItemDesc");
        assertThat(json).extractingJsonPathBooleanValue("$.available").isEqualTo(true);

        assertThat(json).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(json).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(10);

        assertThat(json).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(2);
        assertThat(json).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(20);

        assertThat(json).extractingJsonPathArrayValue("$.comments").hasSize(1);
        assertThat(json).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("Text");

    }
}
