package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.models.Author;

import java.util.List;

import static java.lang.Long.MAX_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.otus.hw.DataTest.getDbAuthors;

@DisplayName("Репозиторий на основе JPA для работы с авторами ")
@DataJpaTest
public class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository repository;

    private List<Author> dbAuthors;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
    }

    @DisplayName("должен загружать список всех авторов")
    @Test
    void shouldReturnCorrectAuthorList() {
        var authors = repository.findAll();
        assertEquals(dbAuthors, authors);
    }

    @DisplayName("должен загружать автора по id")
    @Test
    void shouldReturnCorrectAuthorById() {
        var expectedAuthor = dbAuthors.get(1);
        var author = repository.findById(expectedAuthor.getId());
        assertThat(author).isPresent().get().isEqualTo(expectedAuthor);
    }

    @DisplayName("должен не найти автора по id")
    @Test
    void shouldReturnEmptyAuthorById() {
        assertThat(repository.findById(MAX_VALUE)).isEmpty();
    }
}
