package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.With;

import java.util.Set;

@With
public record BookDto(
        Long id,

        @NotBlank
        String title,

        @NotNull(message = "{jakarta.validation.constraints.NotBlank.message}")
        Long author,

        @NotEmpty(message = "{jakarta.validation.constraints.NotBlank.message}")
        Set<Long> genres
) {
}
