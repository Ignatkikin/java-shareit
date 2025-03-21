package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserDto {
    private Long id;
    @NotBlank(message = "Пустой name")
    private String name;
    @NotBlank(message = "Пустой Email")
    @Email(message = "отсутствует символ @")
    private String email;
}
