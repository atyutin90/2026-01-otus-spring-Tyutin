package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record UserDto(
    Long id,
    @NotBlank
    String username,
    @NotBlank
    String password
) {
}
