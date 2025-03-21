package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> jackson;

    @Test
    void bookingDtoTest() throws IOException {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("ItemName")
                .description("ItemDescription")
                .available(true)
                .requestId(10L)
                .build();

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("UserName")
                .email("user@example.com")
                .build();

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 3, 10, 12, 0))
                .end(LocalDateTime.of(2023, 3, 11, 12, 0))
                .item(itemDto)
                .booker(userDto)
                .status(BookingStatus.APPROVED)
                .build();

        JsonContent<BookingDto> json = jackson.write(bookingDto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.start").isEqualTo("2023-03-10T12:00:00");
        assertThat(json).extractingJsonPathStringValue("$.end").isEqualTo("2023-03-11T12:00:00");
        assertThat(json).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }
}
