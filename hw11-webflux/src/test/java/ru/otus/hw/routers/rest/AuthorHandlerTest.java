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
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.ErrorMessageDto;
import ru.otus.hw.exceptions.AuthorNotFoundException;
import ru.otus.hw.routers.rest.handlers.AuthorHandler;
import ru.otus.hw.services.AuthorService;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.lang.Long.MAX_VALUE;
import static java.util.Locale.ENGLISH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static ru.otus.hw.DataTest.NEW_AUTHOR_FULL_NAME;
import static ru.otus.hw.DataTest.getDbAuthors;

@DisplayName("Handler для работы с авторами ")
@WebFluxTest(AuthorHandler.class)
@Import({AuthorRouter.class, RequestParamLocaleContextResolver.class})
public class AuthorHandlerTest {

    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    private AuthorService authorService;

    @Autowired
    private MessageSource messageSource;

    private List<AuthorDto> dtoAuthors;

    @BeforeAll
    static void setUpLocale() {
        Locale.setDefault(ENGLISH);
    }

    @BeforeEach
    void setUp() {
        dtoAuthors = getDbAuthors().stream().map(AuthorConverter::authorDtoOf).toList();
    }

    @DisplayName("получение списка авторов")
    @Test
    void shouldReturnCorrectAuthorList() {
        when(authorService.findAll()).thenReturn(Flux.fromIterable(dtoAuthors));

        webClient.get()
            .uri("/api/authors")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(AuthorDto.class)
            .contains(dtoAuthors.toArray(new AuthorDto[0]));

        verify(authorService, times(1)).findAll();
    }

    @DisplayName("получение автора по заданному идентификатору")
    @Test
    void shouldReturnCorrectAuthorById() {
        var id = 1L;
        var author = dtoAuthors.stream()
            .filter(it -> it.id().equals(id))
            .findFirst()
            .orElseThrow();

        when(authorService.findById(id)).thenReturn(Mono.just(author));

        webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/authors/{id}")
                .build(id))
            .exchange()
            .expectStatus().isOk()
            .expectBody(AuthorDto.class)
            .isEqualTo(author);

        verify(authorService, times(1)).findById(id);
    }

    @DisplayName("удаление автора по заданному идентификатору")
    @Test
    void shouldDeleteAuthorById() {
        var id = 1L;

        when(authorService.deleteById(id)).thenReturn(Mono.empty().then());

        webClient.delete()
            .uri(uriBuilder -> uriBuilder
                .path("/api/authors/{id}")
                .build(id))
            .exchange()
            .expectStatus().isNoContent()
            .expectBody(Void.class);

        verify(authorService, times(1)).deleteById(id);
    }

    @DisplayName("создание нового автора")
    @Test
    void shouldAddAuthor() {
        var newId = 4L;
        var expect = new AuthorDto(null, NEW_AUTHOR_FULL_NAME);

        when(authorService.create(any(AuthorDto.class))).thenReturn(Mono.just(expect.withId(newId)));

        webClient.post()
            .uri("/api/authors")
            .contentType(APPLICATION_JSON)
            .bodyValue(expect)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(AuthorDto.class)
            .isEqualTo(expect.withId(newId));

        verify(authorService, times(1)).create(any(AuthorDto.class));
    }

    @DisplayName("редактирование автора по заданному идентификатору")
    @Test
    void shouldUpdateAuthorById() {
        var id = 1L;
        var expect = new AuthorDto(id, NEW_AUTHOR_FULL_NAME);

        when(authorService.update(expect)).thenReturn(Mono.just(expect));

        webClient.patch()
            .uri(uriBuilder -> uriBuilder
                .path("/api/authors/{id}")
                .build(id))
            .contentType(APPLICATION_JSON)
            .bodyValue(expect)
            .exchange()
            .expectStatus().isOk()
            .expectBody(AuthorDto.class)
            .isEqualTo(expect);

        verify(authorService, times(1)).update(expect);
    }

    @DisplayName("обработка ошибки, что автор не найден при получении автора по несуществующему идентификатору")
    @Test
    void shouldReturn404WithErrorMessageWhenGetAuthorByIdNotFound() {
        var id = MAX_VALUE;
        var expect = new ErrorMessageDto(messageSource.getMessage("error.author.not-found", null, ENGLISH));

        when(authorService.findById(id)).thenReturn(Mono.error(new AuthorNotFoundException(null)));

        webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/authors/{id}")
                .build(id))
            .exchange()
            .expectStatus().isNotFound()
            .expectBody(ErrorMessageDto.class)
            .isEqualTo(expect);

        verify(authorService, times(1)).findById(id);
    }

    @DisplayName("обработка ошибки, что автор не найден при редактировании автора по несуществующему идентификатору")
    @Test
    void shouldReturn404WithErrorMessageWhenUpdateAuthorNotFound() {
        var id = MAX_VALUE;
        var modify = new AuthorDto(id, NEW_AUTHOR_FULL_NAME);
        var expect = new ErrorMessageDto(messageSource.getMessage("error.author.not-found", null, ENGLISH));

        when(authorService.update(modify)).thenReturn(Mono.error(new AuthorNotFoundException(null)));

        webClient.patch()
            .uri(uriBuilder -> uriBuilder
                .path("/api/authors/{id}")
                .build(id))
            .contentType(APPLICATION_JSON)
            .bodyValue(modify)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody(ErrorMessageDto.class)
            .isEqualTo(expect);

        verify(authorService, times(1)).update(modify);
    }

    @DisplayName("обработка ошибки валидации при создании автора с незаполнеными обязательными полями")
    @Test
    void shouldReturn400WithErrorMessageWhenCreateAuthorWithEmptyRequiredFields() {
        var newGenre = new AuthorDto(null, null);
        var expect = Map.of("fullName", "must not be blank");

        webClient.post()
            .uri("/api/authors")
            .contentType(APPLICATION_JSON)
            .bodyValue(newGenre)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(Map.class)
            .isEqualTo(expect);

        verify(authorService, times(0)).create(any(AuthorDto.class));
    }

    @DisplayName("обработка ошибки валидации при редактовании автора с незаполнеными обязательными полями")
    @Test
    void shouldReturn400WithErrorMessageWhenUpdateAuthorWithEmptyRequiredFields() {
        var id = 1L;
        var modify = new AuthorDto(id, null);
        var expect = Map.of("fullName", "must not be blank");

        webClient.patch()
            .uri(uriBuilder -> uriBuilder
                .path("/api/authors/{id}")
                .build(id))
            .contentType(APPLICATION_JSON)
            .bodyValue(modify)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(Map.class)
            .isEqualTo(expect);

        verify(authorService, times(0)).update(any(AuthorDto.class));
    }
}
