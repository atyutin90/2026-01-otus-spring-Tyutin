package ru.otus.hw.converters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.hw.dto.GenreDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.otus.hw.DataTest.getDbGenres;

public class GenreConverterTest {

    @DisplayName("проверка конвертации Genre -> GenreDto")
    @Test
    void shouldConvertGenreToGenreDto() {
        var genre = getDbGenres().stream().findFirst().orElse(null);
        var expectedGenreDto = new GenreDto(genre.getId(), genre.getName());
        assertEquals(GenreConverter.genreDtoOf(genre), expectedGenreDto);
    }
}
