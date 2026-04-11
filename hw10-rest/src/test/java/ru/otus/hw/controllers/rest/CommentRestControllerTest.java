package ru.otus.hw.controllers.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.ErrorMessageDto;
import ru.otus.hw.exceptions.CommentNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

import java.util.List;
import java.util.Map;

import static java.lang.Long.MAX_VALUE;
import static java.lang.String.valueOf;
import static java.util.Locale.ENGLISH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.otus.hw.DataTest.NEW_COMMENT_TEXT;
import static ru.otus.hw.DataTest.getDbBooks;
import static ru.otus.hw.DataTest.getDbComments;

@DisplayName("Rest контроллеры для работы с комментариями ")
@WebMvcTest(CommentRestController.class)
@Import({ResourceBundleMessageSource.class})
public class CommentRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private BookService bookService;

    @Autowired
    private MessageSource messageSource;

    private List<CommentDto> dtoComments;
    private List<BookDto> dtoBooks;
    private List<Comment> dbComments;

    @BeforeEach
    void setUp() {
        var dbBooks = getDbBooks();
        dtoBooks = dbBooks.stream().map(BookConverter::bookDtoOf).toList();
        dtoComments = getDbComments(dbBooks).stream().map(CommentConverter::commentDtoOf).toList();
        dbComments = getDbComments(dbBooks);
    }

    @DisplayName("получение списка комменатриева для заданной книги")
    @Test
    void shouldReturnCorrectCommentListByBookId() throws Exception {
        var bookId = 1L;
        var book = dtoBooks.stream().filter(it -> it.id().equals(bookId)).findFirst().orElse(null);
        var dtoComments = dbComments.stream()
                .filter(it -> it.getBook().getId() == bookId)
                .map(CommentConverter::commentDtoOf)
                .toList();
        when(bookService.findById(bookId)).thenReturn(book);
        when(commentService.findByBookId(bookId)).thenReturn(dtoComments);
        mvc.perform(get("/api/books/%d/comments".formatted(bookId)).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(dtoComments)));
        verify(commentService, times(1)).findByBookId(bookId);
    }

    @DisplayName("получение комментария по заданному идентификатору")
    @Test
    void shouldReturnCorrectCommentById() throws Exception {
        var id = 1L;
        var comment = dtoComments.stream().filter(it -> it.id().equals(id)).findFirst().orElse(null);
        when(commentService.findById(id)).thenReturn(comment);
        mvc.perform(get("/api/comments/%d".formatted(id)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(comment)));
        verify(commentService, times(1)).findById(id);
    }

    @DisplayName("удаление по заданному идентификатору")
    @Test
    void shouldDeleteCommentById() throws Exception {
        var id = 1L;
        mvc.perform(delete("/api/comments/%d".formatted(id)))
                .andExpect(status().isNoContent());
        verify(commentService, times(1)).deleteById(id);
    }

    @DisplayName("создание нового комментария")
    @Test
    void shouldAddComment() throws Exception {
        var bookId = 1L;
        var expect = new CommentDto(null, bookId, NEW_COMMENT_TEXT);
        when(commentService.create(any(CommentDto.class))).thenReturn(expect);
        mvc.perform(post("/api/books/%d/comments".formatted(bookId))
                        .content(mapper.writeValueAsString(expect))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
        verify(commentService, times(1)).create(any(CommentDto.class));
    }

    @DisplayName("редактирование комментария по заданному идентификатору")
    @Test
    void shouldUpdateCommentById() throws Exception {
        var bookId = 1L;
        var id = 1L;
        var expect = new CommentDto(id, bookId, NEW_COMMENT_TEXT);
        mvc.perform(patch("/api/comments/%d".formatted(id))
                        .content(mapper.writeValueAsString(expect))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(commentService, times(1)).update(expect);
    }

    @DisplayName("обработка ошибки, что комментарий не найден при получении комментария по несуществующему идентификатору")
    @Test
    void shouldReturn404WithErrorMessageWhenGetCommentByIdNotFound() throws Exception {
        var id = MAX_VALUE;
        var expect = new ErrorMessageDto(messageSource.getMessage("error.comment.not-found", null, ENGLISH));
        when(commentService.findById(id)).thenThrow(new CommentNotFoundException(any()));
        mvc.perform(get("/api/comments/%d".formatted(id)).locale(ENGLISH))
                .andExpect(status().isNotFound())
                .andExpect(content().json(mapper.writeValueAsString(expect)));
        verify(commentService, times(1)).findById(id);
    }

    @DisplayName("обработка ошибки, что комментарий не найден при редактировании комментария по несуществующему идентификатору")
    @Test
    void shouldReturn404WithErrorMessageWhenUpdateCommentNotFound() throws Exception {
        var bookId = 1L;
        var id = MAX_VALUE;
        var modify = new CommentDto(id, bookId, NEW_COMMENT_TEXT);
        var expect = new ErrorMessageDto(messageSource.getMessage("error.comment.not-found", null, ENGLISH));
        when(commentService.update(modify)).thenThrow(new CommentNotFoundException(any()));
        mvc.perform(patch("/api/comments/%d".formatted(id)).locale(ENGLISH)
                        .content(mapper.writeValueAsString(modify))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(mapper.writeValueAsString(expect)));
        verify(commentService, times(1)).update(modify);
    }

    @DisplayName("обработка ошибки валидации при редактировании комментария с незаполнеными обязательными полями")
    @Test
    void shouldReturn400WithErrorMessageWhenUpdateCommentWithEmptyRequiredFields() throws Exception {
        var bookId = 1L;
        var id = 1L;
        var modify = new CommentDto(id, bookId, null);
        var expect = Map.of("text", "must not be blank");
        mvc.perform(patch("/api/comments/%d".formatted(id)).locale(ENGLISH)
                        .content(mapper.writeValueAsString(modify))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(expect)));
        verify(commentService, times(0)).update(any(CommentDto.class));
    }

    @DisplayName("обработка ошибки валидации при создании комментария с незаполнеными обязательными полями")
    @Test
    void shouldReturn400WithErrorMessageWhenCreateCommentWithEmptyRequiredFields() throws Exception {
        var bookId = 1L;
        var modify = new CommentDto(null, bookId, null);
        var expect = Map.of("text", "must not be blank");
        mvc.perform(post("/api/books/%d/comments".formatted(bookId)).locale(ENGLISH)
                        .content(mapper.writeValueAsString(modify))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(expect)));
        verify(commentService, times(0)).create(any(CommentDto.class));
    }
}
