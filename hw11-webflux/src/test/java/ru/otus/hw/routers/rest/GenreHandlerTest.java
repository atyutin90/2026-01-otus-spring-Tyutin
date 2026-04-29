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
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.ErrorMessageDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.GenreNotFoundException;
import ru.otus.hw.routers.rest.handlers.GenreHandler;
import ru.otus.hw.services.GenreService;

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
import static ru.otus.hw.DataTest.NEW_GENDER_TEXT;
import static ru.otus.hw.DataTest.getDbGenres;

@DisplayName("Handler для работы с жанрами ")
@WebFluxTest(GenreHandler.class)
@Import({GenreRouter.class, RequestParamLocaleContextResolver.class})
public class GenreHandlerTest {

    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    private GenreService genreService;

    @Autowired
    private MessageSource messageSource;

    private List<GenreDto> dtoGenres;

    @BeforeAll
    static void setUpLocale() {
        Locale.setDefault(ENGLISH);
    }

    @BeforeEach
    void setUp() {
        dtoGenres = getDbGenres().stream().map(GenreConverter::genreDtoOf).toList();
    }

    @DisplayName("получение списка жанров")
    @Test
    void shouldReturnCorrectGenreList() {
        when(genreService.findAll()).thenReturn(Flux.fromIterable(dtoGenres));

        webClient.get()
            .uri("/api/genres")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(GenreDto.class)
            .contains(dtoGenres.toArray(new GenreDto[0]));

        verify(genreService, times(1)).findAll();
    }

    @DisplayName("получение жанра по заданному идентификатору")
    @Test
    void shouldReturnCorrectGenreById() {
        var id = 1L;
        var genre = dtoGenres.stream()
            .filter(it -> it.id().equals(id))
            .findFirst()
            .orElseThrow();

        when(genreService.findById(id)).thenReturn(Mono.just(genre));

        webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/genres/{id}")
                .build(id))
            .exchange()
            .expectStatus().isOk()
            .expectBody(GenreDto.class)
            .isEqualTo(genre);

        verify(genreService, times(1)).findById(id);
    }

    @DisplayName("удаление жанра по заданному идентификатору")
    @Test
    void shouldDeleteGenreById() {
        var id = 1L;

        when(genreService.deleteById(id)).thenReturn(Mono.empty().then());

        webClient.delete()
            .uri(uriBuilder -> uriBuilder
                .path("/api/genres/{id}")
                .build(id))
            .exchange()
            .expectStatus().isNoContent()
            .expectBody(Void.class);

        verify(genreService, times(1)).deleteById(id);
    }

    @DisplayName("создание нового жанра")
    @Test
    void shouldAddAuthor() {
        var newId = 7L;
        var expect = new GenreDto(null, NEW_GENDER_TEXT);

        when(genreService.create(expect)).thenReturn(Mono.just(expect.withId(newId)));

        webClient.post()
            .uri("/api/genres")
            .contentType(APPLICATION_JSON)
            .bodyValue(expect)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(GenreDto.class)
            .isEqualTo(expect.withId(newId));

        verify(genreService, times(1)).create(expect);
    }

    @DisplayName("редактирование жанра по заданному идентификатору")
    @Test
    void shouldUpdateAuthorById() {
        var id = 1L;
        var expect = new GenreDto(id, NEW_GENDER_TEXT);

        when(genreService.update(expect)).thenReturn(Mono.just(expect));

        webClient.patch()
            .uri(uriBuilder -> uriBuilder
                .path("/api/genres/{id}")
                .build(id))
            .contentType(APPLICATION_JSON)
            .bodyValue(expect)
            .exchange()
            .expectStatus().isOk()
            .expectBody(GenreDto.class)
            .isEqualTo(expect);

        verify(genreService, times(1)).update(expect);
    }

    @DisplayName("обработка ошибки, что жанр не найден при получении жанра по несуществующему идентификатору")
    @Test
    void shouldReturn404WithErrorMessageWhenGetAuthorByIdNotFound() {
        var id = MAX_VALUE;
        var expect = new ErrorMessageDto(messageSource.getMessage("error.genre.not-found", null, ENGLISH));

        when(genreService.findById(id)).thenReturn(Mono.error(new GenreNotFoundException(null)));

        webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/genres/{id}")
                .build(id))
            .exchange()
            .expectStatus().isNotFound()
            .expectBody(ErrorMessageDto.class)
            .isEqualTo(expect);

        verify(genreService, times(1)).findById(id);
    }

    @DisplayName("обработка ошибки, что жанр не найден при редактировании жанра по несуществующему идентификатору")
    @Test
    void shouldReturn404WithErrorMessageWhenUpdateAuthorNotFound() {
        var id = MAX_VALUE;
        var modify = new GenreDto(id, NEW_GENDER_TEXT);
        var expect = new ErrorMessageDto(messageSource.getMessage("error.genre.not-found", null, ENGLISH));

        when(genreService.update(modify)).thenReturn(Mono.error(new GenreNotFoundException(null)));

        webClient.patch()
            .uri(uriBuilder -> uriBuilder
                .path("/api/genres/{id}")
                .build(id))
            .contentType(APPLICATION_JSON)
            .bodyValue(modify)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody(ErrorMessageDto.class)
            .isEqualTo(expect);

        verify(genreService, times(1)).update(modify);
    }

    @DisplayName("обработка ошибки валидации при создании жанра с незаполнеными обязательными полями")
    @Test
    void shouldReturn400WithErrorMessageWhenCreateAuthorWithEmptyRequiredFields() {
        var newGenre = new GenreDto(null, null);
        var expect = Map.of("name", "must not be blank");

        webClient.post()
            .uri("/api/genres")
            .contentType(APPLICATION_JSON)
            .bodyValue(newGenre)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(Map.class)
            .isEqualTo(expect);

        verify(genreService, times(0)).create(newGenre);
    }

    @DisplayName("обработка ошибки валидации при редактовании жанра с незаполнеными обязательными полями")
    @Test
    void shouldReturn400WithErrorMessageWhenUpdateAuthorWithEmptyRequiredFields() {
        var id = 1L;
        var modify = new GenreDto(id, null);
        var expect = Map.of("name", "must not be blank");

        webClient.patch()
            .uri(uriBuilder -> uriBuilder
                .path("/api/genres/{id}")
                .build(id))
            .contentType(APPLICATION_JSON)
            .bodyValue(modify)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(Map.class)
            .isEqualTo(expect);

        verify(genreService, times(0)).update(modify);
    }
}
