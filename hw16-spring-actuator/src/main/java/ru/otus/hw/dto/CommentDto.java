package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.With;

@With
public record CommentDto(
        Long id,

        @NotBlank
        String text
) {
}
