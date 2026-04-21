package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.With;

@With
public record CommentDto(
        Long id,

        @NotNull(message = "{jakarta.validation.constraints.NotBlank.message}")
        Long bookId,

        @NotBlank
        String text
) {
}
