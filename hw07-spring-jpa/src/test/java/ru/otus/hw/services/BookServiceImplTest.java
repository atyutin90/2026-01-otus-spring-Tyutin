package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.GenreRepository;

import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static ru.otus.hw.DataTest.BOOK_ID;
import static ru.otus.hw.DataTest.MODIFY_BOOK_TITLE;
import static ru.otus.hw.DataTest.NEW_BOOK_TITLE;

@DisplayName("Сервисе для работы с книгой ")
@DataJpaTest
@Import({BookServiceImpl.class, BookConverter.class, BookConverter.class, AuthorConverter.class, GenreConverter.class})
@Transactional(propagation = Propagation.NEVER)
public class BookServiceImplTest {

    @Autowired
    private BookServiceImpl bookService;

    @Autowired
    private BookConverter bookConverter;

    @DisplayName("должен загружать книгу по id без ошибок")
    @Test
    void shouldReturnCorrectBookById() {
        assertDoesNotThrow(() -> {
            bookService.findById(BOOK_ID).ifPresent(b -> bookConverter.bookToString(b));
        });
    }

    @DisplayName("должен загружать список всех книг без ошибок")
    @Test
    void shouldReturnCorrectBooksList() {
        assertDoesNotThrow(() -> {
            var books =  bookService.findAll();
            books.forEach(b -> bookConverter.bookToString(b));
        });
    }

    @DisplayName("должен сохранять новую книгу без ошибок")
    @Test
    void shouldSaveNewBook() {
        assertDoesNotThrow(() -> {
            var book =  bookService.insert(NEW_BOOK_TITLE, 1L, singleton(2L));
            bookConverter.bookToString(book);
        });
    }

    @DisplayName("должен сохранять измененную книгу без ошибок")
    @Test
    void shouldSaveUpdatedBook() {
        assertDoesNotThrow(() -> {
            var book = bookService.update(BOOK_ID, MODIFY_BOOK_TITLE, 2L, singleton(2L));
            bookConverter.bookToString(book);
        });
    }

    @DisplayName("должен удалять книгу по id без ошибок")
    @Test
    void shouldDeleteBook() {
        assertDoesNotThrow(() -> bookService.deleteById(3L));
    }
}
