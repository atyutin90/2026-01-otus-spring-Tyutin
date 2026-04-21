package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.annotation.DirtiesContext;
import reactor.test.StepVerifier;
import ru.otus.hw.exceptions.BookNotFoundException;
import ru.otus.hw.exceptions.CommentNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;

import static java.lang.Long.MAX_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static ru.otus.hw.DataTest.BOOK_ID;
import static ru.otus.hw.DataTest.NEW_COMMENT_TEXT;
import static ru.otus.hw.DataTest.getDbAuthors;
import static ru.otus.hw.DataTest.getDbBooks;
import static ru.otus.hw.DataTest.getDbComments;
import static ru.otus.hw.DataTest.getDbGenres;

@DisplayName("Репозиторий на основе r2dbc для работы с комментариями к книге ")
@DataR2dbcTest
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository repository;

    private List<Author> dbAuthors;

    private List<Genre> dbGenres;

    private List<Book> dbBooks;

    private List<Comment> dbComments;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
        dbGenres = getDbGenres();
        dbBooks = getDbBooks(dbAuthors, dbGenres);
        dbComments = getDbComments(dbBooks);
    }

    @DisplayName("должен загружать комментарии к книге по id")
    @Test
    void shouldReturnCorrectCommentById() {
        var expected = dbComments.get(0);
        StepVerifier
            .create(repository.findById(expected.getId()))
            .assertNext(comment -> assertThat(expected).isEqualTo(comment))
            .verifyComplete();
    }

    @DisplayName("должен загружать список всех комментариев для заданой книги по ее id")
    @Test
    void shouldReturnCorrectCommentList() {
        var expected = dbComments.stream().filter(it -> it.getId() == BOOK_ID).toList();
        StepVerifier
            .create(repository.findByBookId(BOOK_ID).collectList())
            .assertNext(comments -> assertThat(comments).containsExactlyInAnyOrderElementsOf(expected))
            .verifyComplete();
    }

    @DisplayName("должен сохранять новый комментарий к книге")
    @Test
    void shouldSaveNewComment() {
        var expected = new Comment(0, NEW_COMMENT_TEXT, dbBooks.get(2).getId());
        StepVerifier
            .create(repository.save(expected))
            .assertNext(comment -> {
                    assertThat(comment).isNotNull();
                    assertThat(comment.getId()).isGreaterThan(0);
                    assertThat(comment.getText()).isEqualTo(expected.getText());
                }
            ).verifyComplete();
    }

    @DisplayName("должен сохранять измененный комментарий к книге")
    @Test
    void shouldSaveUpdatedComment() {
        var expected = new Comment(2L, NEW_COMMENT_TEXT, dbBooks.get(2).getId());

        StepVerifier
            .create(repository.save(expected))
            .assertNext(comment -> {
                    assertThat(comment).isNotNull();
                    assertThat(comment.getId()).isGreaterThan(0);
                    assertThat(comment.getText()).isEqualTo(expected.getText());
                }
            ).verifyComplete();
    }

    @DisplayName("должен удалять комментарий к книге по id ")
    @Test
    void shouldDeleteComment() {
        var id = 3L;
        StepVerifier
            .create(repository.deleteById(id).flatMap(it -> repository.findById(id)))
            .consumeErrorWith(throwable -> assertThat(throwable).isInstanceOf(CommentNotFoundException.class));
    }

    @DisplayName("должен не найти комментарий по id")
    @Test
    void shouldReturnEmptyAuthorById() {
        StepVerifier
            .create(repository.findById(MAX_VALUE))
            .consumeErrorWith(throwable -> assertThat(throwable).isInstanceOf(CommentNotFoundException.class));
    }

}
