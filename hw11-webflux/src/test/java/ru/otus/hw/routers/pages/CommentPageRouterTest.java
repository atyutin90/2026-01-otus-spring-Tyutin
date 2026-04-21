package ru.otus.hw.routers.pages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.otus.hw.routers.pages.handlers.CommentPagesHandler;

import static ru.otus.hw.DataTest.INVALID_PARAMS;

@DisplayName("Handler для работы с комментариями к книге ")
@WebFluxTest(CommentPagesRouter.class)
@Import({CommentPagesHandler.class})
public class CommentPageRouterTest {

    @Autowired
    private WebTestClient webClient;

    @DisplayName("страница для создания нового комментария должна отображаться с корректными атрибутами представления")
    @Test
    void shouldRenderNewEditPageWithCorrectViewAndModelAttributes() {
        var bookId = 1L;
        webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/books/{bookId}/comments")
                .build(bookId))
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class);
    }

    @DisplayName("страница редактирования комментария должна отображаться с корректными атрибутами представления")
    @Test
    void shouldRenderEditPageWithCorrectViewAndModelAttributes() {
        var id = 1L;
        var bookId = 1L;
        webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/books/{bookId}/comments/{id}")
                .build(bookId, id))
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class);
    }

    @DisplayName("не валидный параметр запроса комментария, перенаправление на страницу с ошибкой")
    @Test
    void shouldRenderErrorPageWhenInPathInvalidParameter() {
        var bookId = 1L;
        webClient.get()
            .uri(uriBuilder -> uriBuilder
            .path("/books/{bookId}/comments/{id}")
            .build(bookId, INVALID_PARAMS))
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(String.class);
    }
}
