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
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.GenreRepository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static ru.otus.hw.DataTest.BOOK_ID;
import static ru.otus.hw.DataTest.COMMENT_ID;
import static ru.otus.hw.DataTest.DELETE_COMMENT_ID;
import static ru.otus.hw.DataTest.MODIFY_COMMENT_TEXT;
import static ru.otus.hw.DataTest.NEW_COMMENT_TEXT;

@DisplayName("Сервисе для работы с комментариями к книге ")
@DataJpaTest
@Import({CommentServiceImpl.class, CommentConverter.class, BookConverter.class, BookConverter.class, AuthorConverter.class, GenreConverter.class})
@Transactional(propagation = Propagation.NEVER)
public class CommentServiceImplTest {

    @Autowired
    private CommentServiceImpl commentService;

    @Autowired
    private CommentConverter commentConverter;

    @Autowired
    private BookConverter bookConverter;

    @DisplayName("должен загружать комментарии к книге по id без ошибок")
    @Test
    void shouldReturnCorrectCommentById() {
        assertDoesNotThrow(() ->
            commentService.findById(COMMENT_ID)
                    .ifPresent(c -> commentConverter.commentToString(c))
        );
    }

    @DisplayName("должен загружать список всех комментариев для заданой книги по ее id без ошибок")
    @Test
    void shouldReturnCorrectCommentsAuthorById() {
        assertDoesNotThrow(() -> {
            var comments = commentService.findByBookId(BOOK_ID);
            comments.forEach(c -> {
                commentConverter.commentToString(c);
                bookConverter.bookToString(c.getBook());
            });
        });
    }

    @DisplayName("должен сохранять новый комментарий к книге без ошибок")
    @Test
    void shouldSaveNewComment() {
        assertDoesNotThrow(() -> {
            var comments = commentService.insert(NEW_COMMENT_TEXT, BOOK_ID);
            commentConverter.commentToString(comments);
            bookConverter.bookToString(comments.getBook());
        });
    }

    @DisplayName("должен сохранять измененный комментарий к книге без ошибок")
    @Test
    void shouldSaveUpdatedComment() {
        assertDoesNotThrow(() -> {
            var comments = commentService.update(COMMENT_ID, MODIFY_COMMENT_TEXT, BOOK_ID);
            commentConverter.commentToString(comments);
            bookConverter.bookToString(comments.getBook());
        });
    }

    @DisplayName("должен удалять комментарий к книге по id без ошибок")
    @Test
    void shouldDeleteComment() {
        assertDoesNotThrow(() -> commentService.deleteById(DELETE_COMMENT_ID));
    }
}
