package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import reactor.test.StepVerifier;
import ru.otus.hw.exceptions.AuthorNotFoundException;
import ru.otus.hw.exceptions.BookNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorServiceImpl;

import java.util.List;

import static java.lang.Long.MAX_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static ru.otus.hw.DataTest.NEW_BOOK_TITLE;
import static ru.otus.hw.DataTest.getDbAuthors;
import static ru.otus.hw.DataTest.getDbBooks;
import static ru.otus.hw.DataTest.getDbGenres;

@DisplayName("Репозиторий на основе r2dbc для работы с книгами ")
@DataR2dbcTest
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@Import({BookRepositoryImpl.class})
public class BookRepositoryTest {

    @Autowired
    private BookRepositoryImpl repository;

    private List<Author> dbAuthors;

    private List<Genre> dbGenres;

    private List<Book> dbBooks;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
        dbGenres = getDbGenres();
        dbBooks = getDbBooks(dbAuthors, dbGenres);
    }

    @DisplayName("должен загружать книгу по id")
    @ParameterizedTest
    @MethodSource("getBooks")
    void shouldReturnCorrectBookById(Book expectedBook) {
        StepVerifier
            .create(repository.findById(expectedBook.getId()))
            .assertNext(book -> assertThat(book).isEqualTo(expectedBook))
            .verifyComplete();
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        StepVerifier
            .create(repository.findAll().collectList())
            .assertNext(books -> assertThat(dbBooks).containsExactlyInAnyOrderElementsOf(books))
            .verifyComplete();
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var expected = new Book(0, NEW_BOOK_TITLE, dbAuthors.get(0).getId(),
            List.of(dbGenres.get(0).getId(), dbGenres.get(2).getId()));
        StepVerifier
            .create(repository.save(expected))
            .assertNext(book -> {
                    assertThat(book).isNotNull();
                    assertThat(book.getId()).isGreaterThan(0);
                    assertThat(book.getTitle()).isEqualTo(expected.getTitle());
                    assertThat(book.getAuthorId()).isEqualTo(expected.getAuthorId());
                    assertThat(book.getGenresIds()).containsExactlyInAnyOrderElementsOf(expected.getGenresIds());
                }
            ).verifyComplete();
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var expected = new Book(1L, NEW_BOOK_TITLE, dbAuthors.get(2).getId(),
            List.of(dbGenres.get(4).getId(), dbGenres.get(5).getId()));

        StepVerifier
            .create(repository.save(expected))
            .assertNext(book -> {
                    assertThat(book).isNotNull();
                    assertThat(book.getId()).isEqualTo(expected.getId());
                    assertThat(book.getTitle()).isEqualTo(expected.getTitle());
                    assertThat(book.getAuthorId()).isEqualTo(expected.getAuthorId());
                    assertThat(book.getGenresIds()).containsExactlyInAnyOrderElementsOf(expected.getGenresIds());
                }
            ).verifyComplete();
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        var id = 1L;
        StepVerifier
            .create(repository.deleteById(id).flatMap(it -> repository.findById(id)))
            .consumeErrorWith(throwable -> assertThat(throwable).isInstanceOf(BookNotFoundException.class));
    }

    @DisplayName("должен не найти книгу по id")
    @Test
    void shouldReturnEmptyAuthorById() {
        StepVerifier
            .create(repository.findById(MAX_VALUE))
            .consumeErrorWith(throwable -> assertThat(throwable).isInstanceOf(BookNotFoundException.class));
    }

    private static List<Book> getBooks() {
        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        return getDbBooks(dbAuthors, dbGenres);
    }
}
