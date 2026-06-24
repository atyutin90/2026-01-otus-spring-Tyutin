package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.BookNotFoundException;
import java.util.List;
import static java.util.Set.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.otus.hw.DataTest.NEW_BOOK_TITLE;
import static ru.otus.hw.DataTest.getDbAuthors;
import static ru.otus.hw.DataTest.getDbBooks;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.otus.hw.DataTest.getDbGenres;

@DisplayName("Сервис для работы с книгой ")
@DataJpaTest
@Import({BookServiceImpl.class})
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
        var dto = bookService.findById(id);
        assertEquals(expected, dto);
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooks() {
        var returnedDtoList = bookService.findAll();
        assertEquals(returnedDtoList, bookDtoList);
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var expected = new BookDto(0L, NEW_BOOK_TITLE, authorDtoList.get(2).id(), of(genreDtoList.get(4).id(), genreDtoList.get(5).id()));
        var returned = bookService.create(expected);
        assertThat(bookService.findById(returned.id())).isEqualTo(expected.withId(returned.id()));
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var expected = new BookDto(1L, NEW_BOOK_TITLE, authorDtoList.get(2).id(), of(genreDtoList.get(4).id(), genreDtoList.get(5).id()));
        assertThat(bookService.findById(expected.id())).isNotEqualTo(expected);
        bookService.update(expected);
        assertThat(bookService.findById(expected.id())).isEqualTo(expected);
    }

    @DisplayName("должен удалять книгу по id")
    @Test
    void shouldDeleteById() {
        var id = 3L;
        assertDoesNotThrow(() -> bookService.findById(id));
        bookService.deleteById(id);
        assertThatThrownBy(() -> bookService.findById(id)).isInstanceOf(BookNotFoundException.class);
    }
}
