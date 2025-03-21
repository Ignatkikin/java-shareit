package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> jackson;

    @Test
    void userDtoTest() throws IOException {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("UserName")
                .email("UserEmail@gmail.com")
                .build();


        JsonContent<UserDto> json = jackson.write(userDto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("UserName");
        assertThat(json).extractingJsonPathStringValue("$.email").isEqualTo("UserEmail@gmail.com");

    }
}
