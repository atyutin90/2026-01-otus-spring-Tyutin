package ru.otus.hw.repositories;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.otus.hw.DataTest.NEW_GENDER_TEXT;
import static ru.otus.hw.DataTest.getDbGenres;

@DisplayName("Репозиторий на основе Jdbc для работы с жанрами")
@DataJpaTest
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
        var genres = repository.findAll();
        assertEquals(dbGenres, genres);
    }

    @DisplayName("должен загружать жанры по списку ids")
    @Test
    void shouldReturnCorrectGenreById() {
        var expected = List.of(dbGenres.get(1), dbGenres.get(2));
        var genres = repository.findByIdIsIn(expected.stream().map(Genre::getId).collect(Collectors.toSet()));
        Assertions.assertThat(genres).containsExactlyElementsOf(expected);
    }


    @DisplayName("должен сохранять новый жанр")
    @Test
    void shouldSaveNewGender() {
        var expected = new Genre(0L, NEW_GENDER_TEXT);
        var returnedGenre = repository.save(expected);
        assertThat(returnedGenre).isNotNull()
                .matches(genre -> genre.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expected);

        assertThat(repository.findById(returnedGenre.getId()))
                .isPresent()
                .get()
                .isEqualTo(returnedGenre);
    }

    @DisplayName("должен сохранять измененный жанр")
    @Test
    void shouldSaveUpdatedGender() {
        var expected = new Genre(4L, NEW_GENDER_TEXT);

        assertThat(repository.findById(expected.getId()))
                .isPresent()
                .get()
                .isNotEqualTo(expected);

        var returnedGenre = repository.save(expected);
        assertThat(returnedGenre).isNotNull()
                .matches(genre -> genre.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(returnedGenre);

        assertThat(repository.findById(returnedGenre.getId()))
                .isPresent()
                .get()
                .isEqualTo(returnedGenre);
    }

    @DisplayName("должен удалять жанр по id ")
    @Test
    void shouldDeleteGenre() {
        var id = 3L;
        assertThat(repository.findById(id)).isPresent();
        repository.deleteById(id);
        assertThat(repository.findById(id)).isEmpty();
    }
}
