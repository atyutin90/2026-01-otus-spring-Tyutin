package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.MongoDataTestDataInitializer;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.GenreConverter;

import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static ru.otus.hw.DataTest.BOOK_ID;
import static ru.otus.hw.DataTest.MODIFY_BOOK_TITLE;
import static ru.otus.hw.DataTest.NEW_BOOK_TITLE;

@DisplayName("Сервисе для работы с книгой ")
@DataMongoTest
@Import({BookServiceImpl.class, BookConverter.class, AuthorConverter.class, GenreConverter.class})
public class BookServiceImplTest extends AbstractServiceTest {

    @Autowired
    private MongoDataTestDataInitializer initializer;

    @Autowired
    private BookServiceImpl bookService;

    @Autowired
    private BookConverter bookConverter;

    @BeforeEach
    void setUp() {
        initializer.init();
    }

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
            var book =  bookService.insert(NEW_BOOK_TITLE, "1", singleton("2"));
            bookConverter.bookToString(book);
        });
    }

    @DisplayName("должен сохранять измененную книгу без ошибок")
    @Test
    void shouldSaveUpdatedBook() {
        assertDoesNotThrow(() -> {
            var book = bookService.update(BOOK_ID, MODIFY_BOOK_TITLE, "2", singleton("2"));
            bookConverter.bookToString(book);
        });
    }

    @DisplayName("должен удалять книгу по id без ошибок")
    @Test
    void shouldDeleteBook() {
        assertDoesNotThrow(() -> bookService.deleteById("3"));
    }
}
