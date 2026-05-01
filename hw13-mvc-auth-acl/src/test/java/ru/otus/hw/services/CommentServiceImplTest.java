package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.converters.AuthorConverterTest;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.CommentNotFoundException;
import ru.otus.hw.models.Comment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.otus.hw.DataTest.NEW_COMMENT_TEXT;
import static ru.otus.hw.DataTest.getDbBooks;
import static ru.otus.hw.DataTest.getDbComments;

@DisplayName("Сервисе для работы с комментариями к книге ")
@DataJpaTest
@Import({CommentServiceImpl.class, CommentConverter.class, BookConverter.class, BookConverter.class, AuthorConverterTest.class, GenreConverter.class})
public class CommentServiceImplTest {

    @Autowired
    private CommentServiceImpl commentService;

    @MockitoBean
    private AclServiceService aclServiceService;

    private List<CommentDto> commentDtoList;

    private List<Comment> comments;

    @BeforeEach
    void setUp() {
        var books = getDbBooks();
        comments = getDbComments(books);
        commentDtoList = comments.stream().map(CommentConverter::commentDtoOf).toList();
    }

    @DisplayName("должен загружать комментарии к книге по id")
    @Test
    void shouldReturnCorrectCommentById() {
        var id = 1L;
        var expected = commentDtoList.stream()
                .filter(it -> it.id().equals(id))
                .findFirst()
                .orElse(null);
        var dto = commentService.findById(id);
        assertEquals(expected, dto);
    }

    @DisplayName("должен загружать список всех комментариев для заданной книги по ее id")
    @Test
    void shouldReturnCorrectCommentsByAuthorId() {
        var bookId = 1L;
        var expected = comments.stream()
                .filter(it -> it.getBook().getId() == bookId)
                .map(CommentConverter::commentDtoOf)
                .toList();
        var returnedDtoList = commentService.findByBookId(bookId);
        assertEquals(returnedDtoList, expected);
    }

    @DisplayName("должен сохранять новый комментарий к книге")
    @Test
    void shouldSaveNewComment() {
        var bookId = 1L;
        var expected = new CommentDto(0L, NEW_COMMENT_TEXT);
        var returned = commentService.create(bookId, expected);
        assertThat(commentService.findById(returned.id())).isEqualTo(expected.withId(returned.id()));
    }

    @DisplayName("должен сохранять измененный комментарий к книге")
    @Test
    void shouldSaveUpdatedComment() {
        var expected = new CommentDto(1L, NEW_COMMENT_TEXT);
        assertThat(commentService.findById(expected.id())).isNotEqualTo(expected);
        commentService.update(expected);
        assertThat(commentService.findById(expected.id())).isEqualTo(expected);
    }

    @DisplayName("должен удалять комментарий к книге по id")
    @Test
    void shouldDeleteById() {
        var id = 3L;
        assertDoesNotThrow(() -> commentService.findById(id));
        commentService.deleteById(id);
        assertThatThrownBy(() -> commentService.findById(id)).isInstanceOf(CommentNotFoundException.class);
    }
}
