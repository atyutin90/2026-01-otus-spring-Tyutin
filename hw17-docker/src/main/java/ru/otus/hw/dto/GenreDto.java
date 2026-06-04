package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.With;

@With
public record GenreDto(
        Long id,

        @NotBlank
        String name
) {
}
