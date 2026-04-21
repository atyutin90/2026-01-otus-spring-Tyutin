package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import reactor.test.StepVerifier;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.BookNotFoundException;
import ru.otus.hw.exceptions.GenreNotFoundException;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.BookRepositoryImpl;

import java.util.List;

import static java.lang.Long.MAX_VALUE;
import static java.util.Set.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static ru.otus.hw.DataTest.NEW_BOOK_TITLE;
import static ru.otus.hw.DataTest.getDbAuthors;
import static ru.otus.hw.DataTest.getDbBooks;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.otus.hw.DataTest.getDbGenres;

@DisplayName("Сервис для работы с книгой ")
@DataR2dbcTest
@Import({BookServiceImpl.class, BookRepositoryImpl.class})
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class BookServiceImplTest {

    @Autowired
    private BookService bookService;

    private List<BookDto> bookDtoList;

    private List<GenreDto> genreDtoList;

    private List<AuthorDto> authorDtoList;

    @BeforeEach
    void setUp() {
        bookDtoList = getDbBooks().stream().map(BookConverter::bookDtoOf).toList();
        genreDtoList = getDbGenres().stream().map(GenreConverter::genreDtoOf).toList();
        authorDtoList = getDbAuthors().stream().map(AuthorConverter::authorDtoOf).toList();
    }

    @DisplayName("должен загружать книгу по id")
    @Test
    void shouldReturnCorrectBookById() {
        var id = 1L;
        var expected = bookDtoList.stream()
            .filter(it -> it.id().equals(id))
            .findFirst()
            .orElse(null);

        StepVerifier
            .create(bookService.findById(id))
            .assertNext(it -> assertThat(expected).isEqualTo(it))
            .verifyComplete();
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooks() {
        StepVerifier
            .create(bookService.findAll().collectList())
            .assertNext(books -> assertThat(bookDtoList).containsExactlyInAnyOrderElementsOf(books))
            .verifyComplete();
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var expected = new BookDto(0L, NEW_BOOK_TITLE, authorDtoList.get(2).id(), of(genreDtoList.get(4).id(), genreDtoList.get(5).id()));
        StepVerifier
            .create(bookService.create(expected))
            .assertNext(it -> {
                assertThat(it).isNotNull();
                assertThat(it.id()).isGreaterThan(0);
                assertThat(it.authorId()).isEqualTo(expected.authorId());
                assertThat(it.title()).isEqualTo(expected.title());
                assertThat(it.genreIds()).containsExactlyInAnyOrderElementsOf(expected.genreIds());
            }).verifyComplete();
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var expected = new BookDto(1L, NEW_BOOK_TITLE, authorDtoList.get(2).id(), of(genreDtoList.get(4).id(), genreDtoList.get(5).id()));
        StepVerifier
            .create(bookService.update(expected))
            .assertNext(it -> {
                assertThat(it).isNotNull();
                assertThat(it.id()).isEqualTo(expected.id());
                assertThat(it.authorId()).isEqualTo(expected.authorId());
                assertThat(it.title()).isEqualTo(expected.title());
                assertThat(it.genreIds()).containsExactlyInAnyOrderElementsOf(expected.genreIds());
            }).verifyComplete();
    }

    @DisplayName("должен удалять книгу по id")
    @Test
    void shouldDeleteById() {
        var id = 3L;
        StepVerifier
            .create(bookService.deleteById(id).then(bookService.findById(id)))
            .consumeErrorWith(throwable -> assertThat(throwable).isInstanceOf(BookNotFoundException.class));
    }
}
