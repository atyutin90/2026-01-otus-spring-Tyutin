package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.GenreNotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.otus.hw.DataTest.NEW_COMMENT_TEXT;
import static ru.otus.hw.DataTest.getDbGenres;

@DisplayName("Сервис для работы с жанрами ")
@DataJpaTest
@Import({GenreServiceImpl.class})
public class GenreServiceImplTest {

    @Autowired
    private GenreService genreService;

    @MockitoBean
    private AclServiceService aclServiceService;

    private List<GenreDto> genreDtoList;

    @BeforeEach
    void setUp() {
        genreDtoList = getDbGenres().stream().map(GenreConverter::genreDtoOf).toList();
    }

    @DisplayName("должен загружать жанр по id")
    @Test
    void shouldReturnCorrectGenreById() {
        var id = 1L;
        var expect = genreDtoList.stream()
                .filter(it -> it.id().equals(id))
                .findFirst()
                .orElse(null);
        var dto = genreService.findById(id);
        assertEquals(expect, dto);
    }

    @DisplayName("должен загружать список всех жанров")
    @Test
    void shouldReturnCorrectGenres() {
        var returnedDtoList = genreService.findAll();
        assertEquals(returnedDtoList, genreDtoList);
    }

    @DisplayName("должен сохранять новый жанр")
    @Test
    void shouldSaveNewGenre() {
        var expected = new GenreDto(0L, NEW_COMMENT_TEXT);
        var returned = genreService.create(expected);
        assertThat(genreService.findById(returned.id())).isEqualTo(expected.withId(returned.id()));
    }

    @DisplayName("должен сохранять измененный жанр")
    @Test
    void shouldSaveUpdatedGenre() {
        var expected = new GenreDto(1L, NEW_COMMENT_TEXT);
        assertThat(genreService.findById(expected.id())).isNotEqualTo(expected);
        genreService.update(expected);
        assertThat(genreService.findById(expected.id())).isEqualTo(expected);
    }

    @DisplayName("должен удалять жанр по id")
    @Test
    void shouldDeleteById() {
        var id = 3L;
        assertDoesNotThrow(() -> genreService.findById(id));
        genreService.deleteById(id);
        assertThatThrownBy(() -> genreService.findById(id)).isInstanceOf(GenreNotFoundException.class);
    }
}
