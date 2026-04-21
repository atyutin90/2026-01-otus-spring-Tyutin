package ru.otus.hw.converters;

import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Genre;

public class GenreConverter {

    public static GenreDto genreDtoOf(Genre genre) {
        return new GenreDto(genre.getId(), genre.getName());
    }
}
