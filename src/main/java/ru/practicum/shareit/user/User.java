package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class User {
    private Long id;
    @NotBlank(message = "Пустой name")
    private String name;
    @NotBlank(message = "Пустой Email")
    @Email(message = "отсутствует символ @")
    private String email;
}
