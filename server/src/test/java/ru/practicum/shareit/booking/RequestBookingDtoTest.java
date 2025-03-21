package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.RequestBookingDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestBookingDtoTest {

    @Autowired
    private JacksonTester<RequestBookingDto> jackson;

    @Test
    void requestBookingDtoTest() throws IOException {
        RequestBookingDto requestBookingDto = RequestBookingDto.builder()
                .id(1L)
                .itemId(1L)
                .start(LocalDateTime.of(2023, 3, 10, 12, 0))
                .end(LocalDateTime.of(2023, 3, 11, 12, 0))
                .bookerId(2L)
                .status("APPROVED")
                .build();

        JsonContent<RequestBookingDto> json = jackson.write(requestBookingDto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.start").isEqualTo("2023-03-10T12:00:00");
        assertThat(json).extractingJsonPathStringValue("$.end").isEqualTo("2023-03-11T12:00:00");
        assertThat(json).extractingJsonPathNumberValue("$.bookerId").isEqualTo(2);
        assertThat(json).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }
}
