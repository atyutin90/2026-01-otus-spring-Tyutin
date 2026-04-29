package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import reactor.test.StepVerifier;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.BookNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepositoryImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static ru.otus.hw.DataTest.NEW_COMMENT_TEXT;
import static ru.otus.hw.DataTest.getDbBooks;
import static ru.otus.hw.DataTest.getDbComments;

@DisplayName("Сервисе для работы с комментариями к книге ")
@DataR2dbcTest
@Import({CommentServiceImpl.class, BookRepositoryImpl.class})
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class CommentServiceImplTest {

    @Autowired
    private CommentService commentService;

    private List<CommentDto> commentDtoList;

    private List<Comment> comments;

    @BeforeEach
    void setUp() {
        var books = getDbBooks();
        comments = getDbComments(books);
        commentDtoList = comments.stream().map(CommentConverter::commentDtoOf).toList();
    }

    @DisplayName("должен загружать список всех комментариев")
    @Test
    void shouldReturnCorrectCommentList() {
        StepVerifier
            .create(commentService.findAll().collectList())
            .assertNext(it -> assertThat(commentDtoList).containsExactlyInAnyOrderElementsOf(it))
            .verifyComplete();
    }

    @DisplayName("должен загружать комментарии к книге по id")
    @Test
    void shouldReturnCorrectCommentListByBookId() {
        var id = 1L;
        var expected = commentDtoList.stream()
            .filter(it -> it.id().equals(id))
            .findFirst()
            .orElseThrow();

        StepVerifier
            .create(commentService.findById(id))
            .assertNext(it -> assertThat(expected).isEqualTo(it))
            .verifyComplete();
    }

    @DisplayName("должен загружать список всех комментариев для заданной книги по ее id")
    @Test
    void shouldReturnCorrectCommentsByAuthorId() {
        var bookId = 1L;
        var expected = comments.stream()
            .filter(it -> it.getBookId() == bookId)
            .map(CommentConverter::commentDtoOf)
            .toList();

        StepVerifier
            .create(commentService.findByBookId(bookId).collectList())
            .assertNext(comments -> assertThat(expected).containsExactlyInAnyOrderElementsOf(comments))
            .verifyComplete();
    }

    @DisplayName("должен сохранять новый комментарий к книге")
    @Test
    void shouldSaveNewComment() {
        var bookId = 1L;
        var expected = new CommentDto(0L, bookId, NEW_COMMENT_TEXT);
        StepVerifier
            .create(commentService.create(expected))
            .assertNext(it -> {
                assertThat(it).isNotNull();
                assertThat(it.id()).isGreaterThan(0);
                assertThat(it.bookId()).isEqualTo(expected.bookId());
                assertThat(it.text()).isEqualTo(expected.text());
            }).verifyComplete();
    }

    @DisplayName("должен сохранять измененный комментарий к книге")
    @Test
    void shouldSaveUpdatedComment() {
        var expected = new CommentDto(1L, 1L, NEW_COMMENT_TEXT);
        StepVerifier
            .create(commentService.update(expected))
            .assertNext(it -> {
                assertThat(it).isNotNull();
                assertThat(it.id()).isEqualTo(expected.id());
                assertThat(it.bookId()).isEqualTo(expected.bookId());
                assertThat(it.text()).isEqualTo(expected.text());
            }).verifyComplete();
    }

    @DisplayName("должен удалять комментарий к книге по id")
    @Test
    void shouldDeleteById() {
        var id = 3L;
        StepVerifier
            .create(commentService.deleteById(id).then(commentService.findById(id)))
            .consumeErrorWith(throwable -> assertThat(throwable).isInstanceOf(BookNotFoundException.class));
    }
}
