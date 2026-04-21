package ru.otus.hw.repositories;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import reactor.test.StepVerifier;
import ru.otus.hw.exceptions.AuthorNotFoundException;
import ru.otus.hw.exceptions.GenreNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Long.MAX_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static ru.otus.hw.DataTest.NEW_AUTHOR_FULL_NAME;
import static ru.otus.hw.DataTest.NEW_GENDER_TEXT;
import static ru.otus.hw.DataTest.getDbGenres;

@DisplayName("Репозиторий на основе r2dbc для работы с жанрами")
@DataR2dbcTest
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class GenreRepositoryTest {

    @Autowired
    private GenreRepository repository;

    private List<Genre> dbGenres;

    @BeforeEach
    void setUp() {
        dbGenres = getDbGenres();
    }

    @DisplayName("должен загружать список всех жанров")
    @Test
    void shouldReturnCorrectGenreList() {
        StepVerifier
            .create(repository.findAll().collectList())
            .assertNext(genres -> assertThat(dbGenres).containsExactlyInAnyOrderElementsOf(genres))
            .verifyComplete();
    }

    @DisplayName("должен загружать жанры по списку ids")
    @Test
    void shouldReturnCorrectGenreById() {
        var expected = List.of(dbGenres.get(1), dbGenres.get(2));
        StepVerifier
            .create(repository.findByIdIsIn(expected.stream().map(Genre::getId).collect(Collectors.toSet())))
            .assertNext(genre -> assertThat(expected).contains(genre))
            .expectNextCount(expected.size()-1)
            .verifyComplete();
    }


    @DisplayName("должен сохранять новый жанр")
    @Test
    void shouldSaveNewGender() {
        var expected = new Genre(0L, NEW_GENDER_TEXT);
        StepVerifier
            .create(repository.save(expected))
            .assertNext(genre -> {
                    assertThat(genre).isNotNull();
                    assertThat(genre.getId()).isGreaterThan(0);
                    assertThat(genre.getName()).isEqualTo(expected.getName());
                }
            ).verifyComplete();
    }

    @DisplayName("должен сохранять измененный жанр")
    @Test
    void shouldSaveUpdatedGender() {
        var expected = new Genre(4L, NEW_GENDER_TEXT);

        StepVerifier
            .create(repository.save(expected))
            .assertNext(genre -> {
                    assertThat(genre).isNotNull();
                    assertThat(genre.getId()).isEqualTo(expected.getId());
                    assertThat(genre.getName()).isEqualTo(expected.getName());
                }
            ).verifyComplete();
    }

    @DisplayName("должен удалять жанр по id ")
    @Test
    void shouldDeleteGenre() {
        var id = 3L;
        StepVerifier
            .create(repository.deleteById(id).flatMap(it -> repository.findById(id)))
            .consumeErrorWith(throwable -> assertThat(throwable).isInstanceOf(GenreNotFoundException.class));
    }

    @DisplayName("должен не найти жанр по id")
    @Test
    void shouldReturnEmptyAuthorById() {
        StepVerifier
            .create(repository.findById(MAX_VALUE))
            .consumeErrorWith(throwable -> assertThat(throwable).isInstanceOf(GenreNotFoundException.class));
    }
}
