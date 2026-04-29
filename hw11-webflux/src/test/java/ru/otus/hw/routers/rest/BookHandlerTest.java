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
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.ErrorMessageDto;
import ru.otus.hw.exceptions.BookNotFoundException;
import ru.otus.hw.routers.rest.handlers.BookHandler;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static java.lang.Long.MAX_VALUE;
import static java.util.Locale.ENGLISH;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static ru.otus.hw.DataTest.NEW_BOOK_TITLE;
import static ru.otus.hw.DataTest.getDbBooks;

@DisplayName("Handler для работы с жанрами ")
@WebFluxTest(BookHandler.class)
@Import({BookRouter.class, RequestParamLocaleContextResolver.class})
public class BookHandlerTest {

    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    private BookService bookService;

    @Autowired
    private MessageSource messageSource;

    private List<BookDto> dtoBooks;

    @BeforeAll
    static void setUpLocale() {
        Locale.setDefault(ENGLISH);
    }

    @BeforeEach
    void setUp() {
        var dbBooks = getDbBooks();
        dtoBooks = dbBooks.stream().map(BookConverter::bookDtoOf).toList();
    }

    @DisplayName("получение списка книг")
    @Test
    void shouldReturnCorrectBookList() {
        when(bookService.findAll()).thenReturn(Flux.fromIterable(dtoBooks));

        webClient.get()
            .uri("/api/books")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(BookDto.class)
            .contains(dtoBooks.toArray(new BookDto[0]));

        verify(bookService, times(1)).findAll();
    }

    @DisplayName("получение книги по заданному идентификатору")
    @Test
    void shouldReturnCorrectBookById() {
        var id = 1L;
        var book = dtoBooks.stream()
            .filter(it -> it.id().equals(id))
            .findFirst()
            .orElseThrow();

        when(bookService.findById(id)).thenReturn(Mono.just(book));

        webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/books/{id}")
                .build(id))
            .exchange()
            .expectStatus().isOk()
            .expectBody(BookDto.class)
            .isEqualTo(book);

        verify(bookService, times(1)).findById(id);
    }

    @DisplayName("удаление книги по заданному идентификатору")
    @Test
    void shouldDeleteBookById() {
        var id = 1L;
        when(bookService.deleteById(id)).thenReturn(Mono.empty().then());

        webClient.delete()
            .uri(uriBuilder -> uriBuilder
                .path("/api/books/{id}")
                .build(id))
            .exchange()
            .expectStatus().isNoContent()
            .expectBody(Void.class);

        verify(bookService, times(1)).deleteById(id);
    }

    @DisplayName("создание новой книги")
    @Test
    void shouldAddBook() {
        var newId = 4L;
        var expect = new BookDto(null, NEW_BOOK_TITLE, 1L, Set.of(1L, 2L));

        when(bookService.create(expect)).thenReturn(Mono.just(expect.withId(newId)));

        webClient.post()
            .uri("/api/books")
            .contentType(APPLICATION_JSON)
            .bodyValue(expect)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(BookDto.class)
            .isEqualTo(expect.withId(newId));

        verify(bookService, times(1)).create(expect);
    }

    @DisplayName("редактирование книги по заданному идентификатору")
    @Test
    void shouldUpdateBookById() {
        var id = 1L;
        var expect = new BookDto(id, NEW_BOOK_TITLE, 1L, Set.of(1L, 2L));

        when(bookService.update(expect)).thenReturn(Mono.just(expect));

        webClient.patch()
            .uri(uriBuilder -> uriBuilder
                .path("/api/books/{id}")
                .build(id))
            .contentType(APPLICATION_JSON)
            .bodyValue(expect)
            .exchange()
            .expectStatus().isOk()
            .expectBody(BookDto.class)
            .isEqualTo(expect);

        verify(bookService, times(1)).update(expect);
    }

    @DisplayName("обработка ошибки, что книга не найден при получении книги по несуществующему идентификатору")
    @Test
    void shouldReturn404WithErrorMessageWhenGetBookByIdNotFound() throws Exception {
        var id = MAX_VALUE;
        var expect = new ErrorMessageDto(messageSource.getMessage("error.book.not-found", null, ENGLISH));

        when(bookService.findById(id)).thenReturn(Mono.error(new BookNotFoundException(null)));

        webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/books/{id}")
                .build(id))
            .exchange()
            .expectStatus().isNotFound()
            .expectBody(ErrorMessageDto.class)
            .isEqualTo(expect);

        verify(bookService, times(1)).findById(id);
    }

    @DisplayName("обработка ошибки, что книга не найден при редактировании книги по несуществующему идентификатору")
    @Test
    void shouldReturn404WithErrorMessageWhenUpdateBookNotFound() throws Exception {
        var id = MAX_VALUE;
        var modify = new BookDto(id, NEW_BOOK_TITLE, 1L, Set.of(1L, 2L));
        var expect = new ErrorMessageDto(messageSource.getMessage("error.book.not-found", null, ENGLISH));
        when(bookService.update(modify)).thenReturn(Mono.error(new BookNotFoundException(null)));

        webClient.patch()
            .uri(uriBuilder -> uriBuilder
                .path("/api/books/{id}")
                .build(id))
            .contentType(APPLICATION_JSON)
            .bodyValue(modify)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody(ErrorMessageDto.class)
            .isEqualTo(expect);

        verify(bookService, times(1)).update(modify);
    }

    @DisplayName("обработка ошибки валидации при создании книги с незаполнеными обязательными полями")
    @Test
    void shouldReturn400WithErrorMessageWhenCreateBookWithEmptyRequiredFields() throws Exception {
        var newBook = new BookDto(null, null, null, null);
        var expect = Map.of(
            "title", "must not be blank",
            "authorId", "must not be blank",
            "genreIds", "must not be blank"
        );

        webClient.post()
            .uri("/api/books")
            .contentType(APPLICATION_JSON)
            .bodyValue(newBook)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(Map.class)
            .isEqualTo(expect);

        verify(bookService, times(0)).create(newBook);
    }

    @DisplayName("обработка ошибки валидации при редактировании книги с незаполнеными обязательными полями")
    @Test
    void shouldReturn400WithErrorMessageWhenUpdateBookWithEmptyRequiredFields() throws Exception {
        var id = 1L;
        var modify =  new BookDto(id, null, null, null);
        var expect = Map.of(
            "title", "must not be blank",
            "authorId", "must not be blank",
            "genreIds", "must not be blank"
        );

        webClient.patch()
            .uri(uriBuilder -> uriBuilder
                .path("/api/books/{id}")
                .build(id))
            .contentType(APPLICATION_JSON)
            .bodyValue(modify)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(Map.class)
            .isEqualTo(expect);

        verify(bookService, times(0)).create(modify);
    }
}
