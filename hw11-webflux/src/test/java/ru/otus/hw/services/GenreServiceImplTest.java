package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import reactor.test.StepVerifier;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.BookNotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static ru.otus.hw.DataTest.NEW_COMMENT_TEXT;
import static ru.otus.hw.DataTest.getDbGenres;

@DisplayName("Сервис для работы с жанрами ")
@DataR2dbcTest
@Import({GenreServiceImpl.class})
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class GenreServiceImplTest {

    @Autowired
    private GenreServiceImpl genreService;

    private List<GenreDto> genreDtoList;

    @BeforeEach
    void setUp() {
        genreDtoList = getDbGenres().stream().map(GenreConverter::genreDtoOf).toList();
    }

    @DisplayName("должен загружать жанр по id")
    @Test
    void shouldReturnCorrectGenreById() {
        var id = 1L;
        var expected = genreDtoList.stream()
            .filter(it -> it.id().equals(id))
            .findFirst()
            .orElse(null);

        StepVerifier
            .create(genreService.findById(id))
            .assertNext(it -> assertThat(expected).isEqualTo(it))
            .verifyComplete();
    }

    @DisplayName("должен загружать список всех жанров")
    @Test
    void shouldReturnCorrectGenres() {
        StepVerifier
            .create(genreService.findAll().collectList())
            .assertNext(genres -> assertThat(genreDtoList).containsExactlyInAnyOrderElementsOf(genres))
            .verifyComplete();
    }

    @DisplayName("должен сохранять новый жанр")
    @Test
    void shouldSaveNewGenre() {
        var expected = new GenreDto(0L, NEW_COMMENT_TEXT);
        StepVerifier
            .create(genreService.create(expected))
            .assertNext(it -> {
                assertThat(it).isNotNull();
                assertThat(it.id()).isGreaterThan(0);
                assertThat(it.name()).isEqualTo(expected.name());
            }).verifyComplete();
    }

    @DisplayName("должен сохранять измененный жанр")
    @Test
    void shouldSaveUpdatedGenre() {
        var expected = new GenreDto(1L, NEW_COMMENT_TEXT);
        StepVerifier
            .create(genreService.update(expected))
            .assertNext(it -> {
                assertThat(it).isNotNull();
                assertThat(it.id()).isEqualTo(expected.id());
                assertThat(it.name()).isEqualTo(expected.name());
            }).verifyComplete();
    }

    @DisplayName("должен удалять жанр по id")
    @Test
    void shouldDeleteById() {
        var id = 3L;
        StepVerifier
            .create(genreService.deleteById(id).then(genreService.findById(id)))
            .consumeErrorWith(throwable -> assertThat(throwable).isInstanceOf(BookNotFoundException.class));
    }
}
