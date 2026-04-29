package ru.otus.hw.routers.pages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.otus.hw.routers.pages.handlers.GenrePagesHandler;

import static ru.otus.hw.DataTest.INVALID_PARAMS;

@DisplayName("Handler для работы с жанрами ")
@WebFluxTest(GenrePagesRouter.class)
@Import({GenrePagesHandler.class})
public class GenrePageRouterTest {

    @Autowired
    private WebTestClient webClient;

    @DisplayName("страница со списком жанров должна отображаться с корректными атрибутами представления")
    @Test
    void shouldRenderListPageWithCorrectViewAndModelAttributes() {
        webClient.get().uri("/genres")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class);
    }

    @DisplayName("страница c просмотром жанров должна отображаться с корректными атрибутами представления")
    @Test
    void shouldRenderShowPageWithCorrectViewAndModelAttributes() {
        var id = 1L;
        webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/genres/{id}")
                .build(id))
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class);
    }

    @DisplayName("страница для создания нового жанра должна отображаться с корректными атрибутами представления")
    @Test
    void shouldRenderNewEditPageWithCorrectViewAndModelAttributes() {
        webClient.get().uri("/genres/new")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class);
    }

    @DisplayName("не валидный параметр запроса жанра, перенаправление на страницу с ошибкой")
    @Test
    void shouldRenderErrorPageWhenInPathInvalidParameter() {
        webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/genres/{id}")
                .build(INVALID_PARAMS))
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(String.class);
    }
}
