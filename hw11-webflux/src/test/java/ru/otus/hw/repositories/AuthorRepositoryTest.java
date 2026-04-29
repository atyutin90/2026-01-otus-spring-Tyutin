package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.annotation.DirtiesContext;
import reactor.test.StepVerifier;
import ru.otus.hw.exceptions.AuthorNotFoundException;
import ru.otus.hw.models.Author;

import java.util.List;

import static java.lang.Long.MAX_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static ru.otus.hw.DataTest.NEW_AUTHOR_FULL_NAME;
import static ru.otus.hw.DataTest.getDbAuthors;

@DisplayName("Репозиторий на основе r2dbc для работы с авторами ")
@DataR2dbcTest
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
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
        StepVerifier
            .create(repository.findAll().collectList())
            .assertNext(authors -> assertThat(dbAuthors).containsExactlyInAnyOrderElementsOf(authors))
            .verifyComplete();
    }

    @DisplayName("должен загружать автора по id")
    @Test
    void shouldReturnCorrectAuthorById() {
        var expectedAuthor = dbAuthors.get(1);
        StepVerifier
            .create(repository.findById(expectedAuthor.getId()))
            .assertNext(author -> assertThat(author).isEqualTo(expectedAuthor))
            .verifyComplete();
    }

    @DisplayName("должен сохранять нового автора")
    @Test
    void shouldSaveNewAuthor() {
        var expected = new Author(0L, NEW_AUTHOR_FULL_NAME);
        StepVerifier
            .create(repository.save(expected))
            .assertNext(author -> {
                    assertThat(author).isNotNull();
                    assertThat(author.getId()).isGreaterThan(0);
                    assertThat(author.getFullName()).isEqualTo(expected.getFullName());
                }
            ).verifyComplete();
    }

    @DisplayName("должен сохранять измененного автора")
    @Test
    void shouldSaveUpdatedAuthor() {
        var expected = new Author(3L, NEW_AUTHOR_FULL_NAME);
        StepVerifier
            .create(repository.save(expected))
            .assertNext(author -> {
                    assertThat(author).isNotNull();
                    assertThat(author.getId()).isEqualTo(expected.getId());
                    assertThat(author.getFullName()).isEqualTo(expected.getFullName());
                }
            ).verifyComplete();
    }

    @DisplayName("должен удалять автора по id ")
    @Test
    void shouldDeleteAuthor() {
        var id = 3L;
        StepVerifier
            .create(repository.deleteById(id).flatMap(it -> repository.findById(id)))
            .consumeErrorWith(throwable -> assertThat(throwable).isInstanceOf(AuthorNotFoundException.class));
    }

    @DisplayName("должен не найти автора по id")
    @Test
    void shouldReturnEmptyAuthorById() {
        StepVerifier
            .create(repository.findById(MAX_VALUE))
            .consumeErrorWith(throwable -> assertThat(throwable).isInstanceOf(AuthorNotFoundException.class));
    }
}
