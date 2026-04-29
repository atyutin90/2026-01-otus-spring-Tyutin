package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.With;

@With
public record AuthorDto(
        Long id,

        @NotBlank
        String fullName
) {
}
