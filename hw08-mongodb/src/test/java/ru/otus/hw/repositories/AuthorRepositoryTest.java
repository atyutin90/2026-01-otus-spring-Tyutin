package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.otus.hw.MongoDataTestDataInitializer;
import ru.otus.hw.models.Author;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.otus.hw.DataTest.INVALID_ID;
import static ru.otus.hw.DataTest.getDbAuthors;

@DisplayName("Репозиторий для работы с авторами ")
public class AuthorRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private MongoDataTestDataInitializer initializer;

    @Autowired
    private AuthorRepository repository;

    private List<Author> dbAuthors;

    @BeforeEach
    void setUp() {
        //перед каждым тестом пересоздаем данные, чтобы не получить коллизии
        initializer.init();
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
        assertThat(repository.findById(INVALID_ID)).isEmpty();
    }
}
