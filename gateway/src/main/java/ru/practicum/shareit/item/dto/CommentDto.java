package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CommentDto {
    private Long id;
    @NotBlank(message = "Пустой text")
    private String text;
    private Long itemId;
    private String authorName;
    private LocalDateTime created;
}
