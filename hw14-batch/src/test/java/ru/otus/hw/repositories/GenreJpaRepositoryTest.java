package ru.otus.hw.repositories;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import ru.otus.hw.JpaConfig;
import ru.otus.hw.models.jpa.Genre;
import ru.otus.hw.repositories.jpa.GenreJpaRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.otus.hw.DataTest.getDbGenres;

@DisplayName("Репозиторий на основе Jdbc для работы с жанрами ")
@DataJpaTest
@ContextConfiguration(classes = JpaConfig.class)
public class GenreJpaRepositoryTest {

    @Autowired
    private GenreJpaRepository repository;

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
        var expectedGenres = List.of(dbGenres.get(1), dbGenres.get(2));
        var genres = repository.findByIdIsIn(expectedGenres.stream().map(Genre::getId).collect(Collectors.toSet()));
        Assertions.assertThat(genres).containsExactlyElementsOf(expectedGenres);
    }
}
