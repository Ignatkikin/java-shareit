package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {
    private Long id;
    @NotBlank(message = "Пустой name")
    private String name;
    @NotBlank(message = "Пустой description")
    private String description;
    @NotNull
    private Boolean available;
    private Long requestId;
}
