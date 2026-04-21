package ru.otus.hw.routers.rest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.config.RequestParamLocaleContextResolver;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.ErrorMessageDto;
import ru.otus.hw.exceptions.CommentNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.routers.rest.handlers.CommentHandler;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.lang.Long.MAX_VALUE;
import static java.util.Locale.ENGLISH;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static ru.otus.hw.DataTest.NEW_COMMENT_TEXT;
import static ru.otus.hw.DataTest.getDbBooks;
import static ru.otus.hw.DataTest.getDbComments;

@DisplayName("Handler для работы с комментариями ")
@WebFluxTest(CommentHandler.class)
@Import({CommentRouter.class, RequestParamLocaleContextResolver.class})
public class CommentHandlerTest {

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private BookService bookService;

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private MessageSource messageSource;

    private List<CommentDto> dtoComments;
    private List<BookDto> dtoBooks;
    private List<Comment> dbComments;

    @BeforeAll
    static void setUpLocale() {
        Locale.setDefault(ENGLISH);
    }

    @BeforeEach
    void setUp() {
        var dbBooks = getDbBooks();
        dtoBooks = dbBooks.stream().map(BookConverter::bookDtoOf).toList();
        dtoComments = getDbComments(dbBooks).stream().map(CommentConverter::commentDtoOf).toList();
        dbComments = getDbComments(dbBooks);
    }

    @DisplayName("получение списка комменатриев для заданной книги")
    @Test
    void shouldReturnCorrectCommentListByBookId() {
        var bookId = 1L;
        var book = dtoBooks.stream()
            .filter(it -> it.id().equals(bookId))
            .findFirst()
            .orElseThrow();

        var dtoComments = dbComments.stream()
            .filter(it -> it.getBookId() == bookId)
            .map(CommentConverter::commentDtoOf)
            .toList();

        when(bookService.findById(bookId)).thenReturn(Mono.just(book));
        when(commentService.findByBookId(bookId)).thenReturn(Flux.fromIterable(dtoComments));

        webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/books/{bookId}/comments")
                .build(bookId))
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(CommentDto.class)
            .contains(dtoComments.toArray(new CommentDto[0]));

        verify(commentService, times(1)).findByBookId(bookId);
    }

    @DisplayName("получение комментария по заданному идентификатору")
    @Test
    void shouldReturnCorrectCommentById() {
        var id = 1L;
        var comment = dtoComments.stream()
            .filter(it -> it.id().equals(id))
            .findFirst()
            .orElseThrow();

        when(commentService.findById(id)).thenReturn(Mono.just(comment));

        webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/comments/{id}")
                .build(id))
            .exchange()
            .expectStatus().isOk()
            .expectBody(CommentDto.class)
            .isEqualTo(comment);

        verify(commentService, times(1)).findById(id);
    }

    @DisplayName("удаление по заданному идентификатору")
    @Test
    void shouldDeleteCommentById() {
        var id = 1L;

        when(commentService.deleteById(id)).thenReturn(Mono.empty().then());

        webClient.delete()
            .uri(uriBuilder -> uriBuilder
                .path("/api/comments/{id}")
                .build(id))
            .exchange()
            .expectStatus().isNoContent()
            .expectBody(Void.class);

        verify(commentService, times(1)).deleteById(id);
    }

    @DisplayName("создание нового комментария")
    @Test
    void shouldAddComment() {
        var newId = 4L;
        var bookId = 1L;
        var expect = new CommentDto(null, bookId, NEW_COMMENT_TEXT);
        when(commentService.create(expect)).thenReturn(Mono.just(expect.withId(newId)));

        webClient.post()
            .uri(uriBuilder -> uriBuilder
                .path("/api/books/{bookId}/comments")
                .build(bookId))
            .contentType(APPLICATION_JSON)
            .bodyValue(expect)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(CommentDto.class)
            .isEqualTo(expect.withId(newId));

        verify(commentService, times(1)).create(expect);
    }

    @DisplayName("редактирование комментария по заданному идентификатору")
    @Test
    void shouldUpdateCommentById() {
        var bookId = 1L;
        var id = 1L;
        var expect = new CommentDto(id, bookId, NEW_COMMENT_TEXT);

        when(commentService.update(expect)).thenReturn(Mono.just(expect));

        webClient.patch()
            .uri(uriBuilder -> uriBuilder
                .path("/api/comments/{id}")
                .build(id))
            .contentType(APPLICATION_JSON)
            .bodyValue(expect)
            .exchange()
            .expectStatus().isOk()
            .expectBody(CommentDto.class)
            .isEqualTo(expect);

        verify(commentService, times(1)).update(expect);
    }

    @DisplayName("обработка ошибки, что комментарий не найден при получении комментария по несуществующему идентификатору")
    @Test
    void shouldReturn404WithErrorMessageWhenGetCommentByIdNotFound() {
        var id = MAX_VALUE;
        var expect = new ErrorMessageDto(messageSource.getMessage("error.comment.not-found", null, ENGLISH));

        when(commentService.findById(id)).thenReturn(Mono.error(new CommentNotFoundException(null)));

        webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/comments/{id}")
                .build(id))
            .exchange()
            .expectStatus().isNotFound()
            .expectBody(ErrorMessageDto.class)
            .isEqualTo(expect);

        verify(commentService, times(1)).findById(id);
    }

    @DisplayName("обработка ошибки, что комментарий не найден при редактировании комментария по несуществующему идентификатору")
    @Test
    void shouldReturn404WithErrorMessageWhenUpdateCommentNotFound() {
        var bookId = 1L;
        var id = MAX_VALUE;
        var modify = new CommentDto(id, bookId, NEW_COMMENT_TEXT);
        var expect = new ErrorMessageDto(messageSource.getMessage("error.comment.not-found", null, ENGLISH));

        when(commentService.update(modify)).thenReturn(Mono.error(new CommentNotFoundException(null)));

        webClient.patch()
            .uri(uriBuilder -> uriBuilder
                .path("/api/comments/{id}")
                .build(id))
            .contentType(APPLICATION_JSON)
            .bodyValue(modify)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody(ErrorMessageDto.class)
            .isEqualTo(expect);

        verify(commentService, times(1)).update(modify);
    }

    @DisplayName("обработка ошибки валидации при редактировании комментария с незаполнеными обязательными полями")
    @Test
    void shouldReturn400WithErrorMessageWhenUpdateCommentWithEmptyRequiredFields() {
        var bookId = 1L;
        var id = 1L;
        var modify = new CommentDto(id, bookId, null);
        var expect = Map.of("text", "must not be blank");

        webClient.patch()
            .uri(uriBuilder -> uriBuilder
                .path("/api/comments/{id}")
                .build(id))
            .contentType(APPLICATION_JSON)
            .bodyValue(modify)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(Map.class)
            .isEqualTo(expect);

        verify(commentService, times(0)).update(modify);
    }

    @DisplayName("обработка ошибки валидации при создании комментария с незаполнеными обязательными полями")
    @Test
    void shouldReturn400WithErrorMessageWhenCreateCommentWithEmptyRequiredFields() {
        var bookId = 1L;
        var modify = new CommentDto(null, null, null);
        var expect = Map.of("text", "must not be blank");

        webClient.post()
            .uri(uriBuilder -> uriBuilder
                .path("/api/books/{bookId}/comments")
                .build(bookId))
            .contentType(APPLICATION_JSON)
            .bodyValue(modify)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(Map.class)
            .isEqualTo(expect);

        verify(commentService, times(0)).create(modify);
    }
}
