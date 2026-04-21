package ru.otus.hw.routers.pages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.otus.hw.routers.pages.handlers.AuthorPagesHandler;

import static ru.otus.hw.DataTest.INVALID_PARAMS;

@DisplayName("Handler для работы с авторами ")
@WebFluxTest(AuthorPagesRouter.class)
@Import({AuthorPagesHandler.class})
public class AuthorPagesRouterTest {

    @Autowired
    private WebTestClient webClient;

    @DisplayName("страница со списком авторов должна отображаться с корректными атрибутами представления")
    @Test
    void shouldRenderListPageWithCorrectViewAndModelAttributes() {
        webClient.get().uri("/authors")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class);
    }

    @DisplayName("страница c просмотром автора должна отображаться с корректными атрибутами представления")
    @Test
    void shouldRenderShowPageWithCorrectViewAndModelAttributes() {
        var id = 1L;
        webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/authors/{id}")
                .build(id))
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class);
    }

    @DisplayName("страница для создания нового автора должна отображаться с корректными атрибутами представления")
    @Test
    void shouldRenderNewEditPageWithCorrectViewAndModelAttributes() {
        webClient.get().uri("/authors/new")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class);
    }

    @DisplayName("не валидный параметр запроса автора, перенаправление на страницу с ошибкой")
    @Test
    void shouldRenderErrorPageWhenInPathInvalidParameter() {
        webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/authors/{id}")
                .build(INVALID_PARAMS))
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(String.class);
    }
}
